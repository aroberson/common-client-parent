/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.context;

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
public interface IConsumerContextConfig
{
    /**
     * This returns the consumer application name.
     * 
     * @return  The consumer application name.
     * 
     * @since   1.0
     */
    public String consumerName();
    
    
    /**
     * This returns the consumer uuid.
     * 
     * @return  The consumer application name.
     * 
     * @since   1.0
     */
    public String consumerUuid();
    
    
    /**
     * This returns true if the consumer is stateful, else false.
     * 
     * @return  True if the consumer is stateful, else false.
     * 
     * @since   1.0
     */
    public boolean stateful();
}
