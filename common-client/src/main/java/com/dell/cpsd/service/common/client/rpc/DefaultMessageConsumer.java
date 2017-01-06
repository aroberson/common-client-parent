/**
 * &copy; 2017 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.logging.LoggingManager;

/**
 * <p>
 * &copy; 2017 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 * </p>
 *
 * @since SINCE-TBD
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
