/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.exception;

/**
 * This is the timeout exception for a service request.
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
public final class ServiceExecutionException extends Exception
{
    /*
     * serial version id
     */
    private static final long serialVersionUID = 4119241334520852220L;

    /**
     * ServiceTimeoutException constructor.
     *
     * @since 1.0
     */
    public ServiceExecutionException()
    {
        super();
    }

    /**
     * ServiceTimeoutException constructor.
     *
     * @param cause
     *            The cause of the exception.
     *
     * @since 1.0
     */
    public ServiceExecutionException(Throwable cause)
    {
        super(cause);
    }

    /**
     * ServiceTimeoutException constructor.
     *
     * @param message
     *            The exception message.
     *
     * @since 1.0
     */
    public ServiceExecutionException(String message)
    {
        super(message);
    }

    /**
     * ServiceTimeoutException constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The cause of the exception.
     *
     * @since 1.0
     */
    public ServiceExecutionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
