/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.common.client.log.SCCLMessageCode;
import com.dell.cpsd.service.common.client.manager.AbstractServiceCallbackManager;
import com.dell.cpsd.service.common.client.task.ServiceTask;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public abstract class AbstractServiceClient extends AbstractServiceCallbackManager implements ServiceCallbackRegistry
{
    /*
     * The logger for this class.
     */
    private final ILogger logger;

    protected AbstractServiceClient(ILogger logger)
    {
        this.logger = logger;
    }

    protected <REQ, RES extends ServiceResponse<?>> RES processRequest(long timeout, final ServiceRequestCallback serviceRequestCallback)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        this.shutdownCheck();

        // create a correlation identifier for the operation
        String requestId = serviceRequestCallback.getRequestId();
        if (requestId == null)
        {
            requestId = createRequestId();
        }

        final ServiceCallback<RES> serviceCallback = new ServiceCallback<>();

        this.createAndAddServiceTask(requestId, serviceCallback, timeout);

        // publish the list system compliance message to the service
        try
        {
            serviceRequestCallback.executeRequest(requestId);
        }
        catch (Exception exception)
        {
            // remove the compute callback if the message cannot be published
            this.removeServiceTask(requestId);

            logAndThrowException(exception);
        }

        // wait from the response from the service
        this.waitForServiceCallback(serviceCallback, requestId, timeout);

        this.checkForServiceError(serviceCallback);

        // if there was no compute error, then return the compute result
        return serviceCallback.getServiceResponse();
    }

    /**
     * This releases any resources associated with this manager.
     *
     * @since 1.0
     */
    @Override
    public void release()
    {
        super.release();
    }

    private void createAndAddServiceTask(final String requestId, final ServiceCallback<?> callback, final long timeout)
    {
        // the infinite timeout is used for the task because it is handled with
        // this synchronous call.
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, callback, timeout);

        // add the compute callback using the correlation identifier as key
        this.addServiceTask(requestId, task);
    }

    private void logAndThrowException(final Exception exception) throws ServiceExecutionException
    {
        Object[] lparams = {exception.getMessage()};
        String lmessage = logger.error(SCCLMessageCode.PUBLISH_MESSAGE_FAIL_E.getMessageCode(), lparams, exception);

        throw new ServiceExecutionException(lmessage, exception);
    }

    protected String createRequestId()
    {
        return uuid();
    }

    protected Date timestamp()
    {
        return Calendar.getInstance().getTime();
    }

    protected String uuid()
    {
        return UUID.randomUUID().toString();
    }

    private <R extends ServiceResponse<?>> ServiceCallback<R> createCallback(final Class<R> callbackType)
    {
        return new ServiceCallback<R>();
    }

    private void checkForServiceError(final ServiceCallback<?> callback) throws ServiceExecutionException
    {
        // check to see if a compute error has been handled by the manager
        ServiceError error = callback.getServiceError();

        // throw a compute exception using the message in the compute error
        if (error != null)
        {
            throw new ServiceExecutionException(error.getErrorMessage());
        }
    }

    private void shutdownCheck() throws ServiceExecutionException
    {
        if (this.isShutDown())
        {
            String lmessage = logger.error(SCCLMessageCode.MANAGER_SHUTDOWN_E.getMessageCode());
            throw new ServiceExecutionException(lmessage);
        }
    }
}
