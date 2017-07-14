package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.DelegateLogger;
import com.dell.cpsd.common.logging.ILogger;
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
public class DefaultMessageConsumerTest
{
    @Test
    public void getLogger() throws Exception
    {
        DefaultMessageConsumer consumer = new DefaultMessageConsumer();
        ILogger logger = consumer.getLogger();

        assertTrue(logger instanceof DelegateLogger);
    }

}