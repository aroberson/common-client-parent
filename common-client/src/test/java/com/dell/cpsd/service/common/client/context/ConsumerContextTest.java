package com.dell.cpsd.service.common.client.context;

import org.junit.Test;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import static org.junit.Assert.*;

/**
 * Unit tests for ConsumerContext.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ConsumerContextTest
{
    @Test
    public void getConsumerUuid() throws Exception
    {
        String expectedUuid = "123456";
        Environment env = new AbstractEnvironment()
        {
            @Override
            public String getProperty(final String key)
            {
                if (ConsumerContext.CONSUMER_UUID.equals(key))
                {
                    return expectedUuid;
                }
                throw new RuntimeException();
            }
        };
        String uuid = ConsumerContext.getConsumerUuid(env);

        assertEquals(expectedUuid, uuid);
    }

    @Test
    public void getConsumerUuidNullEnvParam() throws Exception
    {
        String expectedUuid = null;
        Environment env = null;
        String uuid = ConsumerContext.getConsumerUuid(env);

        assertEquals(expectedUuid, uuid);
    }

    @Test
    public void getConsumerUuidNullEnvUuid() throws Exception
    {
        Environment env = new AbstractEnvironment()
        {
            @Override
            public String getProperty(final String key)
            {
                if (ConsumerContext.CONSUMER_UUID.equals(key))
                {
                    return null;
                }
                throw new RuntimeException();
            }
        };
        String uuid = ConsumerContext.getConsumerUuid(env);

        assertNotNull(uuid);
    }
}