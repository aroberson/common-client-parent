/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ServiceError
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ServiceErrorTest
{
    @Test
    public void constructor()
    {
        String requestId = "request id";
        String errorCode = "my response";
        String message = "my message";

        ServiceError service = new ServiceError(requestId, errorCode, message);

        assertEquals(requestId, service.getRequestId());
        assertEquals(errorCode, service.getErrorCode());
        assertEquals(message, service.getErrorMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorError()
    {
        String errorCode = "my errorCode";
        String message = "my message";

        new ServiceError(null, errorCode, message);
    }

}