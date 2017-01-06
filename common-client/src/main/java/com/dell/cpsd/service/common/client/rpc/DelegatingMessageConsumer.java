package com.dell.cpsd.service.common.client.rpc;

/**
 * <p>
 * &copy; 2017 VCE Company, LLC. All rights reserved.
 * VCE Confidential/Proprietary Information
 * </p>
 *
 * @since SINCE-TBD
 */
public interface DelegatingMessageConsumer
{
    <S, D> void addAdapter(ServiceCallbackAdapter<S, D> callback);
}
