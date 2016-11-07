/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.exception;

/**
 * This is the timeout exception for a service request.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public final class ServiceTimeoutException extends Exception
{
    /*
     * serial version id
     */
    private static final long serialVersionUID = 4119241334520852220L;

    
    /**
     * ServiceTimeoutException constructor.
     * 
     * @since   SINCE-TBD
     */
    public ServiceTimeoutException()
    {
        super();
    }
    
    
    /**
     * ServiceTimeoutException constructor.
     * 
     * @param   cause  The cause of the exception.
     * 
     * @since   SINCE-TBD
     */
    public ServiceTimeoutException(Throwable cause)
    {
        super(cause);
    }
    
    
    /**
     * ServiceTimeoutException constructor.
     * 
     * @param   message  The exception message.
     * 
     * @since   SINCE-TBD
     */
    public ServiceTimeoutException(String message)
    {
        super(message);
    }
    
    
    /**
     * ServiceTimeoutException constructor.
     * 
     * @param   message  The exception message.
     * @param   cause    The cause of the exception.
     * 
     * @since   SINCE-TBD
     */
    public ServiceTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
