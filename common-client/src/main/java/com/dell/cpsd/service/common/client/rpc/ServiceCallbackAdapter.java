/**
 * &copy; 2016 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;

/**
 * <p>
 * &copy; 2016 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 * </p>
 *
 * @since SINCE-TBD
 */
public interface ServiceCallbackAdapter<S, D>
{
    D transform(S source);

    void consume(IServiceCallback callback, D destination);

    IServiceCallback take(S source);

    Class<S> getSourceClass();
}
