/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.context;

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
public interface IConsumerContextConfig
{
    /**
     * This returns the consumer application name.
     * 
     * @return The consumer application name.
     * 
     * @since 1.0
     */
    String consumerName();

    /**
     * This returns the consumer uuid.
     * 
     * @return The consumer application name.
     * 
     * @since 1.0
     */
    String consumerUuid();

    /**
     * This returns true if the consumer is stateful, else false.
     * 
     * @return True if the consumer is stateful, else false.
     * 
     * @since 1.0
     */
    boolean stateful();
}
