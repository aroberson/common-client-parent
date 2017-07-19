/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.logging.LoggingManager;
import com.dell.cpsd.service.common.client.callback.ServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.task.ServiceTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for AbstractServiceClient.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class AbstractServiceClientTest
{

    private AbstractServiceClientToTest serviceClientToTest;

    @Before
    public void setUp() throws Exception
    {
        ILogger logger = new LoggingManager().getLogger(AbstractServiceClient.class);
        serviceClientToTest = new AbstractServiceClientToTest(logger);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void processRequest() throws Exception
    {
        String requestId = "request1";
        String message = "finished";
        long timeout = 100;
        ServiceResponse<String> expectedResponse = new ServiceResponse<>(requestId, "response", message);
        MockServiceRequestCallback callback = new MockServiceRequestCallback(requestId, serviceClientToTest, expectedResponse);

        ServiceResponse<?> response = serviceClientToTest.processRequest(timeout, callback);

        assertEquals(response.getMessage(), message);
        assertTrue(callback.isRequestExecurted());
    }

    @Test(expected = ServiceExecutionException.class)
    public void processRequestError() throws Exception
    {
        String requestId = "request1";
        String message = "error";
        long timeout = 100;
        ServiceError expectedResponse = new ServiceError(requestId, "errorCode", message);
        MockServiceRequestCallback callback = new MockServiceRequestCallback(requestId, serviceClientToTest, expectedResponse);

        serviceClientToTest.processRequest(timeout, callback);
    }

    @Test(expected = ServiceExecutionException.class)
    public void processRequestException() throws Exception
    {
        String requestId = "exception";
        String message = "finished";
        long timeout = 100;
        ServiceResponse<String> expectedResponse = new ServiceResponse<>(requestId, "response", message);
        MockServiceRequestCallback callback = new MockServiceRequestCallback(requestId, serviceClientToTest, expectedResponse);

        serviceClientToTest.processRequest(timeout, callback);
    }

    class AbstractServiceClientToTest extends AbstractServiceClient
    {
        AbstractServiceClientToTest(final ILogger logger)
        {
            super(logger);
        }
    }

    class MockServiceRequestCallback implements ServiceRequestCallback
    {
        String                requestId;
        AbstractServiceClient serviceClient;
        ServiceResponse<?>    response;
        ServiceError          error;
        boolean               requestExecuted = false;

        MockServiceRequestCallback(String requestId, AbstractServiceClient serviceClient, ServiceResponse<?> response)
        {
            this.requestId = requestId;
            this.serviceClient = serviceClient;
            this.response = response;
        }

        MockServiceRequestCallback(String requestId, AbstractServiceClient serviceClient, ServiceError error)
        {
            this.requestId = requestId;
            this.serviceClient = serviceClient;
            this.error = error;
        }

        @Override
        public String getRequestId()
        {
            return requestId;
        }

        @Override
        public void executeRequest(final String requestId) throws Exception
        {
            if ("exception".equals(requestId))
            {
                throw new Exception("requestId is null");
            }
            ServiceTask task = serviceClient.getServiceTask(requestId);
            if (response != null)
                ((ServiceCallback<ServiceResponse<?>>) task.getServiceCallback()).handleServiceResponse(response);
            else if (error != null)
            {
                ((ServiceCallback<ServiceResponse<?>>) task.getServiceCallback()).handleServiceError(error);
            }
            else
            {
                throw new Exception("execute request error");
            }
            requestExecuted = true;
        }

        boolean isRequestExecurted()
        {
            return requestExecuted;
        }
    }
}