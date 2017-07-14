package com.dell.cpsd.service.common.client.callback;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ServiceResponse
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ServiceResponseTest
{
    @Test
    public void constructor()
    {
        String requestId = "request id";
        String response = "my response";
        String message = "my message";

        ServiceResponse service = new ServiceResponse<String>(requestId, response, message);

        assertEquals(requestId, service.getRequestId());
        assertEquals(response, service.getResponse());
        assertEquals(message, service.getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorError()
    {
        String response = "my response";
        String message = "my message";

        new ServiceResponse<String>(null,response,  message);
    }

}