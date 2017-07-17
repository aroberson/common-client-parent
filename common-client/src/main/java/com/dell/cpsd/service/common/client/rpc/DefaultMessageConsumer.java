/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.logging.LoggingManager;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DefaultMessageConsumer extends AbstractMessageConsumer
{
    private ILogger logger = new LoggingManager().getLogger(DefaultMessageConsumer.class);

    @Override
    protected ILogger getLogger()
    {
        return logger;
    }
}
