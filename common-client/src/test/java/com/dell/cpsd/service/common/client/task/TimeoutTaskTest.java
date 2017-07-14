package com.dell.cpsd.service.common.client.task;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO: Document usage.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class TimeoutTaskTest
{
    @Test
    public void testRun() throws Exception
    {
        MockTimeoutTaskManager manager = new MockTimeoutTaskManager();
        TimeoutTask task = new TimeoutTask(manager);

        task.run();

        assertTrue(manager.isCheckForTimedoutTasksInvoked());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullManager() throws Exception
    {
        MockTimeoutTaskManager manager = null;
        TimeoutTask task = new TimeoutTask(manager);

        task.run();
    }

    class MockTimeoutTaskManager implements ITimeoutTaskManager
    {

        boolean checkForTimedoutTasksInvoked;

        @Override
        public void checkForTimedoutTasks()
        {
            checkForTimedoutTasksInvoked = true;
        }

        boolean isCheckForTimedoutTasksInvoked()
        {
            return checkForTimedoutTasksInvoked;
        }

    }
}