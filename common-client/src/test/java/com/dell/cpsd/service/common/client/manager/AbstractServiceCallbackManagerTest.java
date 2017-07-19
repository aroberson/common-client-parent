/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.manager;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.callback.ServiceTimeout;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.common.client.task.ServiceTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for AbstractServiceCallbackManager.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class AbstractServiceCallbackManagerTest
{
    private boolean                        isHandleServiceTimeoutCalled;

    private AbstractServiceCallbackManager serviceCallbackManagerToTest;

    @Before
    public void setUp() throws Exception
    {
        serviceCallbackManagerToTest = new AbstractServiceCallbackManagerToTest();
    }

    @After
    public void tearDown()
    {
        serviceCallbackManagerToTest.waitForRequests(0);
        serviceCallbackManagerToTest.release();
    }

    @Test
    public void addAndGetServiceTask() throws Exception
    {
        String requestId = "request-1";
        long timeout = 1;
        ServiceCallback<ServiceResponse<?>> callback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> expectedTask = new ServiceTask<>(requestId, callback, timeout);

        serviceCallbackManagerToTest.addServiceTask(requestId, expectedTask);
        ServiceTask<IServiceCallback<?>> task = serviceCallbackManagerToTest.getServiceTask(requestId);

        assertEquals(expectedTask, task);
    }

    @Test
    public void addAndRemoveServiceTask() throws Exception
    {
        String requestId = "request-1";
        long timeout = 1;
        ServiceCallback<ServiceResponse<?>> callback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> expectedTask = new ServiceTask<>(requestId, callback, timeout);

        serviceCallbackManagerToTest.addServiceTask(requestId, expectedTask);
        ServiceTask<IServiceCallback<?>> task = serviceCallbackManagerToTest.removeServiceTask(requestId);
        ServiceTask<IServiceCallback<?>> removedTask = serviceCallbackManagerToTest.getServiceTask(requestId);

        assertEquals(expectedTask, task);
        assertNull(removedTask);
    }

    @Test
    public void removeServiceCakkback() throws Exception
    {
        String requestId = "request-1";
        long timeout = 1;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);

        serviceCallbackManagerToTest.addServiceTask(requestId, task);
        IServiceCallback<?> callback = serviceCallbackManagerToTest.removeServiceCallback(requestId);
        ServiceTask<IServiceCallback<?>> removedTask = serviceCallbackManagerToTest.getServiceTask(requestId);

        assertEquals(expectedCallback, callback);
        assertNull(removedTask);
    }

    @Test
    public void cancel() throws Exception
    {
        String requestId = "request-1";
        long timeout = 1;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);

        serviceCallbackManagerToTest.addServiceTask(requestId, task);
        boolean isCancelled = serviceCallbackManagerToTest.cancel(requestId);
        ServiceTask<IServiceCallback<?>> cancelledTask = serviceCallbackManagerToTest.getServiceTask(requestId);

        assertTrue(isCancelled);
        assertNull(cancelledTask);
    }

    @Test
    public void releaseTimeout() throws Exception
    {
        String requestId = "request-1";
        long timeout = 10;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);

        AbstractServiceCallbackManager serviceCallbackManagerToTest = new AbstractServiceCallbackManagerToTest(500);
        serviceCallbackManagerToTest.addServiceTask(requestId, task);
        serviceCallbackManagerToTest.release();
        ServiceTask<IServiceCallback<?>> cancelledTask = serviceCallbackManagerToTest.getServiceTask(requestId);

        assertTrue(serviceCallbackManagerToTest.isShutDown());
        assertNull(cancelledTask);

    }

    @Test
    public void release() throws Exception
    {
        String requestId = "request-1";
        long timeout = 10;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);

        AbstractServiceCallbackManager serviceCallbackManagerToTest = new FastAbstractServiceCallbackManagerToTest(10000);
        serviceCallbackManagerToTest.addServiceTask(requestId, task);
        serviceCallbackManagerToTest.release();
        ServiceTask<IServiceCallback<?>> cancelledTask = serviceCallbackManagerToTest.getServiceTask(requestId);

        assertTrue(serviceCallbackManagerToTest.isShutDown());
        assertNull(cancelledTask);

    }

    @Test
    public void checkForTimedoutTasks() throws Exception
    {
        isHandleServiceTimeoutCalled = false;
        String requestId = "request-1";
        long timeout = 1;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<ServiceResponse<?>>()
        {

            @Override
            public void handleServiceTimeout(final ServiceTimeout timeout)
            {
                isHandleServiceTimeoutCalled = true;
            }
        };
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);
        Thread.sleep(10);
        serviceCallbackManagerToTest.addServiceTask(requestId, task);

        serviceCallbackManagerToTest.checkForTimedoutTasks();
        assertTrue(isHandleServiceTimeoutCalled);
        assertNull(serviceCallbackManagerToTest.getServiceTask(requestId));
    }

    @Test
    public void makeScheduledExecutorService() throws Exception
    {
        ScheduledExecutorService executorService = serviceCallbackManagerToTest.makeScheduledExecutorService(10, 20, TimeUnit.MICROSECONDS);
        assertNotNull(executorService);
    }

    @Test
    public void waitForRequests() throws Exception
    {
        String requestId = "request-1";
        long timeout = 100;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);
        Thread.sleep(10);
        serviceCallbackManagerToTest.addServiceTask(requestId, task);

        boolean completed = serviceCallbackManagerToTest.waitForRequests(2500);

        assertTrue(completed);
        assertNull(serviceCallbackManagerToTest.getServiceTask(requestId));

    }

    @Test
    public void waitForRequestsTimeout() throws Exception
    {
        String requestId = "request-1";
        long timeout = 100;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<>();
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);
        Thread.sleep(10);
        serviceCallbackManagerToTest.addServiceTask(requestId, task);

        boolean completed = serviceCallbackManagerToTest.waitForRequests(250);

        assertFalse(completed);
        assertNull(serviceCallbackManagerToTest.getServiceTask(requestId));

    }

    @Test
    public void shutdown() throws Exception
    {
        ScheduledExecutorService executorService = serviceCallbackManagerToTest.makeScheduledExecutorService(10, 20, TimeUnit.MICROSECONDS);
        assertFalse(executorService.isShutdown());

        serviceCallbackManagerToTest.shutdown(executorService);

        assertTrue(executorService.isShutdown());
    }

    @Test
    public void waitForServiceCallback() throws Exception
    {
        String requestId = "request-1";
        long timeout = 10000;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<ServiceResponse<?>>()
        {
            int isDoneCnt = 0;

            @Override
            public boolean isDone()
            {
                return ++isDoneCnt > 2;
            }
        };
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);

        serviceCallbackManagerToTest.addServiceTask(requestId, task);

        serviceCallbackManagerToTest.waitForServiceCallback(expectedCallback, requestId, 21);

        assertNotNull(serviceCallbackManagerToTest.getServiceTask(requestId));
    }

    @Test(expected = ServiceTimeoutException.class)
    public void waitForServiceCallbackError() throws Exception
    {
        String requestId = "request-1";
        long timeout = 10000;
        ServiceCallback<ServiceResponse<?>> expectedCallback = new ServiceCallback<ServiceResponse<?>>()
        {
            int isDoneCnt = 0;

            @Override
            public boolean isDone()
            {
                return ++isDoneCnt > 2;
            }
        };
        ServiceTask<IServiceCallback<?>> task = new ServiceTask<>(requestId, expectedCallback, timeout);
        serviceCallbackManagerToTest.addServiceTask(requestId, task);

        try
        {
            serviceCallbackManagerToTest.waitForServiceCallback(expectedCallback, requestId, 10);
        }
        catch (ServiceTimeoutException e)
        {
            assertNull(serviceCallbackManagerToTest.getServiceTask(requestId));
            throw e;
        }
        fail("Expected ServiceTimeoutException");
    }

    class AbstractServiceCallbackManagerToTest extends AbstractServiceCallbackManager
    {
        long timeout = -1;

        AbstractServiceCallbackManagerToTest()
        {
            this(-1);
        }

        AbstractServiceCallbackManagerToTest(long timeout)
        {
            super();
            this.timeout = timeout;
        }

        @Override
        protected boolean waitForRequests(long timeoutParam)
        {
            if (timeout > 0)
            {
                return super.waitForRequests(timeout);
            }
            else
            {
                return super.waitForRequests(timeoutParam);
            }
        }
    }

    class FastAbstractServiceCallbackManagerToTest extends AbstractServiceCallbackManager
    {
        long timeout = -1;

        FastAbstractServiceCallbackManagerToTest()
        {
            this(-1);
        }

        FastAbstractServiceCallbackManagerToTest(long timeout)
        {
            super();
            this.timeout = timeout;
            // make a fast ScheduledExecutorService to clear out tasks before timeout occurs
            makeScheduledExecutorService(500, 500, TimeUnit.MILLISECONDS);
        }

        @Override
        protected boolean waitForRequests(long timeoutParam)
        {
            if (timeout > 0)
            {
                return super.waitForRequests(timeout);
            }
            else
            {
                return super.waitForRequests(timeoutParam);
            }
        }
    }
}