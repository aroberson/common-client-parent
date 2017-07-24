/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.context;

import org.springframework.core.env.Environment;

import java.util.UUID;

/**
 * The consumer context for a client.
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
public class ConsumerContext
{
    public static final String CONSUMER_UUID = "dell.cpsd.client.consumer.uuid";

    /**
     * ConsumerContext constructor.
     * 
     * @since 1.0
     */
    public ConsumerContext()
    {
        super();
    }

    /**
     * This returns the consumer uuid.
     * @param environment
     *            Environment context from which consumer uuid has to be got
     * @return consumeruuid string from the environment
     */
    public static String getConsumerUuid(final Environment environment)
    {
        if (environment == null)
        {
            return null;
        }

        String uuid = environment.getProperty(CONSUMER_UUID);

        if (uuid == null)
        {
            uuid = UUID.randomUUID().toString();
        }

        return uuid;
    }
}
