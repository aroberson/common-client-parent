/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

/**
 * This interface should be implemented by any class that acts as a callback
 * for a service request.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public interface IServiceCallback<T extends ServiceResponse<?>>
{
    /**
     * This handles a service error. The result is contained in the
     * <code>ServiceError</code>.
     * 
     * @param   result  The <code>ServiceError</code> with the error data.
     * 
     * @since   SINCE-TBD
     */
    public void handleServiceError(ServiceError error);
    
    
    /**
     * This handles a service timeout. The result is contained in the
     * <code>ServiceTimeout</code>.
     * 
     * @param   timeout  The <code>ServiceTimeout</code> with the timeout.
     * 
     * @since   SINCE-TBD
     */
    public void handleServiceTimeout(ServiceTimeout timeout);
    
    
    /**
     * This handles the service response.
     * 
     * @param   serviceResponse The service response.
     * 
     * @since   SINCE-TBD
     */
    public void handleServiceResponse(T serviceResponse);
}
