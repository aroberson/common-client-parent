/**
 * Copyright &copy; 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import com.dell.cpsd.common.logging.ILogger;

import com.dell.cpsd.service.common.client.log.SCCLLoggingManager;
import com.dell.cpsd.service.common.client.log.SCCLMessageCode;

import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

import com.dell.cpsd.service.common.client.callback.ServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceTimeout;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;

import com.dell.cpsd.service.common.client.task.ITimeoutTaskManager;
import com.dell.cpsd.service.common.client.task.TimeoutTask;
import com.dell.cpsd.service.common.client.task.ServiceTask;

/**
 * This class is responsible for handling service callbacks at the client.
 * 
 * <p/>
 * Copyright &copy; 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public abstract class AbstractServiceCallbackManager implements ITimeoutTaskManager 
                                                    
{
    /*
     * The logger for this class.
     */
    private static final ILogger LOGGER = 
            SCCLLoggingManager.getLogger(AbstractServiceCallbackManager.class);
    
    /*
     * The infinite timeout for a request.
     */
    private static final long                             INFINITE_TIMEOUT         = -1l;

    /*
     * The flag to indicate that this manager is shutting down.
     */
    private volatile boolean                              shutdown                 = false;


    /*
     * The map of correlation identifier to <code>IServiceCallback</code>s
     */
    private Map<String, ServiceTask<IServiceCallback<?>>> requests                 = null;

    /*
     * The <code>ScheduledExecutorService</code> used to check for timed out tasks.
     */
    private ScheduledExecutorService                      executorService          = null;
    
    
   
    /**
     * AbstractServiceCallbackManager constructor.
     * 
     * @since   1.0
     */
    public AbstractServiceCallbackManager()
    {
        super();
        
        this.requests = new HashMap<String, ServiceTask<IServiceCallback<?>>>();
        
        this.executorService = this.makeScheduledExecutorService(
                                           1000l, 1000l, TimeUnit.MILLISECONDS);
    }
    
    
    /**
     * This returns true if this manager is shutting down.
     * 
     * @return  True if this manager is shutting down.
     * 
     * @since   1.0
     */
    public boolean isShutDown()
    {
        return this.shutdown;
    }
    
    
    
    /**
     * This adds a service task, with its corresponding request identifier, to
     * the managed requests.
     * 
     * @param   requestId       The request identifier.
     * @param   serviceTask     The service task to add.
     * 
     * @since   1.0
     */
    public void addServiceTask(final String requestId,
                        final ServiceTask<IServiceCallback<?>> serviceTask)
    {
        if (requestId == null)
        {
            return;
        }
        
        if (serviceTask == null)
        {
            return;
        }
        
        // add the callback using the requestId identifier as key
        synchronized(this.requests)
        {
            this.requests.put(requestId, serviceTask);
        }        
    }
    
    
    /**
     * This removes the service task with the specified request identifier
     * 
     * @param   requestId       The request identifier.
     * 
     * @return  The serivce task with the specified identifier, or null.
     * 
     * @since   1.0
     */
    public ServiceTask<IServiceCallback<?>> removeServiceTask(final String requestId)
    {
        synchronized(this.requests)
        {
            return this.requests.remove(requestId);
        }
    }
    
    
  
    /**
     * This returns the service task using the specified request identifier
     * 
     * @param   requestId       The request identifier.
     * 
     * @return  The serivce task with the specified identifier, or null.
     * 
     * @since   1.0
     */
    public ServiceTask<IServiceCallback<?>> getServiceTask(final String requestId)
    {
        synchronized(this.requests)
        {
            return this.requests.get(requestId);
        }
    }
    
    
    /**
     * This returns the <code>IServiceCallback></code> for the specified
     * request identifier, or null.
     * 
     * @param   requestId   The request identifier.
     * 
     * @return  The service callback for the identifier, or null.
     * 
     * @since   1.0
     */
    public IServiceCallback<?> removeServiceCallback(final String requestId)
    {
        if (requestId == null)
        {
            return null;
        }
        
        ServiceTask<IServiceCallback<?>> task = null;
        
        synchronized(this.requests)
        {
            task = this.requests.remove(requestId);
        }
        
        if (task == null)
        {
            return null;
        }
        
        return task.getServiceCallback();
    }

     
    /**
     * This cancels the request with the specified identifier.
     * 
     * @param   requestId   The request identifier.
     * 
     * @since   1.0
     */
    public boolean cancel(final String requestId)
    {
        if (requestId == null)
        {
            return false;
        }
        
        ServiceTask<IServiceCallback<?>> task = null;
        
        synchronized(this.requests)
        {
            task = this.requests.remove(requestId);
        }
        
        return (task != null);
    }

    
    /**
     * This releases any resources associated with this manager.
     * 
     * @since   1.0
     */
    public void release()
    {
        // if the manager has already been shutdown then return
        if (this.shutdown)
        {
            return;
        }
        
        // set the shutdown flag which will cause requests to be rejected
        this.shutdown = true;
        
        LOGGER.info(SCCLMessageCode.EXECUTOR_SHUTDOWN_I.getMessageCode());
        this.shutdown(this.executorService);

        // wait for the current set of requests to complete and clear down
        LOGGER.info(SCCLMessageCode.WAIT_ON_REQUESTS_I.getMessageCode());
        this.waitForRequests(10000l);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void checkForTimedoutTasks()
    {
        final List<ServiceTask<IServiceCallback<?>>> timedOutTasks = 
                new ArrayList<ServiceTask<IServiceCallback<?>>>();
        
        String[] keyArray = null;
        
        synchronized(this.requests)
        {
            final Set<String> keySet = this.requests.keySet();
            
            keyArray = keySet.toArray(new String[keySet.size()]);
        }
        
        long currentTime = System.currentTimeMillis();
        
        for (int i = 0; i < keyArray.length; i++)
        {
            final String requestId = keyArray[i];
            
            ServiceTask<IServiceCallback<?>> task = this.requests.get(requestId);
            
            if (task == null)
            {
                continue;
            }
            
            if (task.hasTimedout(currentTime))
            {
                synchronized(this.requests)
                {
                    task = this.requests.remove(requestId);
                    
                    if (task != null)
                    {
                        timedOutTasks.add(task);
                    }
                }
            }
        }
        

        for (int i = 0; i < timedOutTasks.size(); i++)
        {
            final ServiceTask<IServiceCallback<?>> task = timedOutTasks.get(i);

            final IServiceCallback<?> callback = task.getServiceCallback();

            final ServiceTimeout timeout = 
                    new ServiceTimeout(task.getRequestId(), task.getTimeout());
            
            // TODO : Take the callback processing off the timeout thread
            try
            {
                callback.handleServiceTimeout(timeout);
            }
            catch (Exception exception)
            {
                // log the exception thrown by the callback
                Object[] lparams = {"handleServiceTimeout", exception.getMessage()};
                LOGGER.error(SCCLMessageCode.ERROR_CALLBACK_FAIL_E.getMessageCode(),
                            lparams, exception);
            }
        }        
    }
    
    
    /**
     * This creates the <code>ScheduledExecutorService</code> for this manager.
     * 
     * @param   initialDelay    The time to delay first execution.
     * @param   delay           The period between successive executions.
     * @param   timeUnit        The <code>TimeUnit</code> for the delays.
     * 
     * @return  The <code>ScheduledExecutorService</code> for this manager.
     * 
     * @since   1.0
     */
    protected ScheduledExecutorService makeScheduledExecutorService(
            long initialDelay, long delay, TimeUnit timeUnit)
    {
        TimeoutTask timeoutTask = new TimeoutTask(this);
        
        final ScheduledExecutorService executorService = 
                                Executors.newSingleThreadScheduledExecutor();
        
        executorService.scheduleWithFixedDelay(
                    timeoutTask, initialDelay, delay, TimeUnit.MILLISECONDS);
        
        return executorService;
    }
    
    
    
    /**
     * This waits for any currently running requests to complete and clear down.
     * 
     * @param   timeout  The time in milliseconds to wait.
     * 
     * @return  True if all requests were completed, otherwise false.
     * 
     * @since   1.0
     */
    protected boolean waitForRequests(long timeout)
    {
        if ((this.requests == null) || (this.requests.size() == 0))
        {
            return true;
        }
        
        long timeLimit = timeout;
        long sleepTime = 250l;
        long elapsedTime = 0;
       
        // wait for the requests to complete and clear down
        while (this.requests.size() > 0)
        {
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException exception)
            {
            }

            elapsedTime += sleepTime;
            
            if (elapsedTime >= timeLimit)
            {
                // clear the requests
                this.requests.clear();
                
                return false;
            }
        }

        return true;
    }
    
    
    /**
     * This shuts down the specified <code>ExecutorService</code>.
     * 
     * @param   executorService  The <code>ExecutorService</code> to shutdown.
     * 
     * @since   1.0
     */
    protected void shutdown(final ExecutorService executorService)
    {
        if (executorService == null)
        {
            return;
        }
        
        // stop new tasks from being submitted
        executorService.shutdown();
        
        try 
        {
            boolean graceful = 
                executorService.awaitTermination(3, TimeUnit.SECONDS);
            
            // allow pending tasks to finish
            if (graceful == false) 
            {
                // cancel currently executing tasks
                executorService.shutdownNow();
              
                executorService.awaitTermination(3, TimeUnit.SECONDS);
            }
            
        } catch (InterruptedException ie) 
        {
            // (re-)cancel if current thread also interrupted
            executorService.shutdownNow();
            
            // preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
    
    
    /**
     * This waits the specified timeout for the service callback. 
     * 
     * @param   serviceCallback The service callback.
     * @param   requestId       The request identifier.
     * @param   timeout         The wait time period.
     * 
     * @throws  ServiceTimeoutException Thrown if there is a timeout.
     * 
     * @since   1.0
     */
    protected void waitForServiceCallback(final ServiceCallback<?> serviceCallback, 
            final String requestId, long timeout)
        throws ServiceTimeoutException
    {
        if (serviceCallback == null)
        {
            return;
        }
        
        // wait from the response from the service
        long timeLimit = timeout;
        long sleepTime = 10l;
        long elapsedTime = 0;
       
        // the callback is done if a response or error is handled by the manager
        while (serviceCallback.isDone() == false)
        {
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException exception)
            {
            }
            
            // if the timeout is greater than zero then check for elapsed time
            if (timeLimit > 0l)
            {
                elapsedTime += sleepTime;
                
                if (elapsedTime >= timeLimit)
                {
                    synchronized(this.requests)
                    {
                        this.requests.remove(requestId);
                    }
                    
                    Object[] lparams = {requestId, "" + timeLimit};
                    String lmessage = LOGGER.error(
                                            SCCLMessageCode.MESSAGE_TIMEOUT_E.getMessageCode(), 
                                            lparams);
                    
                    throw new ServiceTimeoutException(lmessage);
                }
            }
        }
    }
}