/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.rpc;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public interface ServiceCallbackAdapter<S, D>
{
    D transform(S source);

    void consume(IServiceCallback callback, D destination);

    IServiceCallback take(S source);

    Class<S> getSourceClass();
}
