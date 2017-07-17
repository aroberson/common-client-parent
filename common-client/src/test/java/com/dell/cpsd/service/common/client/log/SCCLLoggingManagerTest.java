/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.log;

import com.dell.cpsd.common.logging.DelegateLogger;
import com.dell.cpsd.common.logging.ILogger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO: Document usage.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class SCCLLoggingManagerTest
{
    @Test
    public void getLogger() throws Exception
    {
        ILogger logger = SCCLLoggingManager.getLogger(SCCLLoggingManagerTest.class);
        assertTrue(logger instanceof DelegateLogger);
    }

    @Test
    public void getLogger1() throws Exception
    {
        ILogger logger = SCCLLoggingManager.getLogger("test");
        assertTrue(logger instanceof DelegateLogger);
    }

}