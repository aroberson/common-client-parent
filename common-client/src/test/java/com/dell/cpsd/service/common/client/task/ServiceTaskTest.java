/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.task;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO: Document usage.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ServiceTaskTest
{
    @Test
    public void equalsTest() throws Exception
    {
        String requestId = "my request";
        Object callback1 = new Object();
        Object callback2 = new Object();

        ServiceTask task1 = new ServiceTask(requestId, callback1, 1);
        ServiceTask task2 = new ServiceTask(requestId, callback2, 2);

        assertEquals(task1, task2);
    }

    @Test
    public void notEqualsTest() throws Exception
    {
        String requestId1 = "my request";
        String requestId2 = "another request";
        int timeout = 1;
        Object callback1 = new Object();

        ServiceTask task1 = new ServiceTask(requestId1, callback1, timeout);
        ServiceTask task2 = new ServiceTask(requestId2, callback1, timeout);

        assertNotEquals(task1, task2);
    }

    @Test
    public void hashCodeTest() throws Exception
    {
        String requestId = "my request";
        Object callback = new Object();

        ServiceTask task = new ServiceTask(requestId, callback, 1);

        assertEquals(requestId.hashCode(), task.hashCode());
    }

}