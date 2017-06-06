package com.dell.cpsd.service.common.client.callback;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for ServiceCallback
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ServiceCallbackTest
{
    @Test
    public void handleServiceError() throws Exception
    {
        String requestId = "request id 1";
        String errorCode = "error code 1";
        String errorMessage = "error message 1";
        ServiceError serviceError = new ServiceError(requestId, errorCode, errorMessage);

        ServiceCallback serviceCallback = new ServiceCallback<ServiceResponse<String>>();
        serviceCallback.handleServiceError(serviceError);

        assertEquals(serviceError, serviceCallback.getServiceError());
        assertTrue(serviceCallback.isDone());
    }

    @Test
    public void handleServiceResponse() throws Exception
    {
        String requestId = "request id 1";
        String errorCode = "error code 1";
        String errorMessage = "error message 1";
        ServiceResponse serviceresponse = new ServiceResponse<>(requestId, errorCode, errorMessage);

        ServiceCallback serviceCallback = new ServiceCallback<ServiceResponse<String>>();
        serviceCallback.handleServiceResponse(serviceresponse);

        assertEquals(serviceresponse, serviceCallback.getServiceResponse());
        assertTrue(serviceCallback.isDone());
    }

}