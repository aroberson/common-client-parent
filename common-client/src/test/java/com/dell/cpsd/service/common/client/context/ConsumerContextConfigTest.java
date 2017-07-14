package com.dell.cpsd.service.common.client.context;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * TODO: Document usage.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class ConsumerContextConfigTest
{
    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void consumerName() throws Exception
    {
        String name = "test name";
        ConsumerContextConfig configToTest = new ConsumerContextConfig(name, true);

        assertEquals(name, configToTest.consumerName());
    }

    @Test
    public void consumerUuid()
    {
        System.setProperty("container.id", "CONTAINER_NAME");
        String name = "test name";
        ConsumerContextConfig configToTest = new ConsumerContextConfig(name, true);

        String containerId = configToTest.consumerUuid();

        String[] containerParts = containerId.split("\\.");
        String uuid = containerParts[2];
        assertTrue(containerParts.length == 3);
        assertEquals(containerParts[0], name);
        try
        {
            UUID uuid1 = UUID.fromString(uuid);
        }
        catch (IllegalArgumentException ex)
        {
            fail("Returned value does not contain a valid UUID");
        }

    }

    @Test
    public void stateful() throws Exception
    {
        String name = "test name";
        boolean stateful = false;
        ConsumerContextConfig configToTest = new ConsumerContextConfig(name, stateful);

        assertFalse(configToTest.stateful());
    }

}