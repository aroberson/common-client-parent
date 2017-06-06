/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.task;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.service.common.client.log.SCCLLoggingManager;
import com.dell.cpsd.service.common.client.log.SCCLMessageCode;

/**
 * This task is used to trigger a periodic check of service tasks to determine 
 * if they have exceeded their timeout.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public class TimeoutTask implements Runnable
{
    /*
     * The logger for this class.
     */
    private static final ILogger LOGGER = 
                            SCCLLoggingManager.getLogger(TimeoutTask.class);
    
    /*
     * The <code>ITimeoutTaskManager</code> for the task
     */
    private ITimeoutTaskManager manager = null;
   
    
    /**
     * TimeoutTask constructor
     * 
     * @param   manager  The <code>ITimeoutTaskManager</code> that is called.
     * 
     * @since   SINCE-TBD
     */
    public TimeoutTask(ITimeoutTaskManager manager)
    {
        super();
        
        if (manager == null)
        {
            throw new IllegalArgumentException("The timeout task manager is null.");
        }
        
        this.manager = manager;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            this.manager.checkForTimedoutTasks();
        }
        catch (Exception exception)
        {
            // log the exception thrown by the check
            Object[] lparams = {exception.getMessage()};
            LOGGER.error(SCCLMessageCode.TIMEOUT_TASK_CHECK_E.getMessageCode(),
                        lparams, exception);            
        }
    }
}
