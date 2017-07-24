/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

/**
 * This class contains the information for a service timeout.
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
public class ServiceTimeout
{
    /*
     * The request identifier.
     */
    private String requestId = null;

    /*
     * The timeout that was configured.
     */
    private long   timeout   = -1;

    /**
     * ServiceTimeout constructor
     * 
     * @param requestId
     *            The request identifier.
     * @param timeout
     *            The configured timeout
     * 
     * @since 1.0
     */
    public ServiceTimeout(String requestId, long timeout)
    {
        super();

        if (requestId == null)
        {
            throw new IllegalArgumentException("The request identifier is not set.");
        }

        this.requestId = requestId;

        this.timeout = timeout;
    }

    /**
     * This returns the request identifier.
     * 
     * @return The request identifier.
     * 
     * @since 1.0
     */
    public String getRequestId()
    {
        return this.requestId;
    }

    /**
     * This returns the configured timeout for the request.
     * 
     * @return The configured timeout for the request.
     * 
     * @since 1.0
     */
    public long getTimeout()
    {
        return this.timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("ServiceTimeout{requestId=");
        builder.append(this.requestId);
        builder.append(", timeout=");
        builder.append(this.timeout);
        builder.append("}");

        return builder.toString();
    }
}
