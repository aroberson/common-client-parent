/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.task;

/**
 * This interface should be implemented by any class that checks for timed out tasks.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 * </p>
 * 
 * @version 1.0
 * 
 * @since 1.0
 */
public interface ITimeoutTaskManager
{
    /**
     * This checks for services requests that have timed out and should be discarded by the manager.
     * 
     * @since 1.0
     */
    void checkForTimedoutTasks();
}
