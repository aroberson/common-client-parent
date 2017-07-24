/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

/**
 * This interface should be implemented by any class that acts as a callback for a service request.
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
public interface IServiceCallback<T extends ServiceResponse<?>>
{

    /**
     * This handles a service error. The result is contained in the <code>ServiceError</code>.
     * 
     * @param error
     *            Service error to be handled
     */
    void handleServiceError(ServiceError error);

    /**
     * This handles a service timeout. The result is contained in the <code>ServiceTimeout</code>.
     * 
     * @param timeout
     *            The <code>ServiceTimeout</code> with the timeout.
     * 
     * @since 1.0
     */
    void handleServiceTimeout(ServiceTimeout timeout);

    /**
     * This handles the service response.
     * 
     * @param serviceResponse
     *            The service response.
     * 
     * @since 1.0
     */
    void handleServiceResponse(T serviceResponse);
}
