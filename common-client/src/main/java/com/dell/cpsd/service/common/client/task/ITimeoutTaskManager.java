/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.task;

/**
 * This interface should be implemented by any class that checks for timed out
 * tasks.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public interface ITimeoutTaskManager
{
    /**
     * This checks for services requests that have timed out and should be
     * discarded by the manager.
     * 
     * @since   SINCE-TBD
     */
    void checkForTimedoutTasks();
}
