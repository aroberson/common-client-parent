/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.context;

import org.springframework.core.env.Environment;

import java.util.UUID;

/**
 * The consumer context for a client.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public class ConsumerContext 
{
    public static final String CONSUMER_UUID = "dell.cpsd.client.consumer.uuid";
    
    /**
     * ConsumerContext constructor.
     * 
     * @since   SINCE-TBD
     */
    public ConsumerContext()
    {
        super();
    }
    
    
    /**
     * This returns the consumer uuid.
     * 
     * @return  The consumer uuid.
     * 
     * @since   SINCE-TBD
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
