package com.dell.cpsd.service.common.client.callback;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ServiceTimeout
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ServiceTimeoutTest
{
    @Test
    public void constructor()
    {
        String requestId = "request id";
        long timeout = 1;

        ServiceTimeout service = new ServiceTimeout(requestId, timeout);

        assertEquals(requestId, service.getRequestId());
        assertEquals(timeout, service.getTimeout());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorError()
    {
        long timeout = 1;

        new ServiceTimeout(null, timeout);
    }

}