/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

/**
 * This class contains the information for a service timeout.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TDB
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
    private long timeout = -1;
    

    /**
     * ServiceTimeout constructor
     * 
     * @param   requestId       The request identifier.
     * @param   timeout         The configured timeout
     * @param   message         The timeout message.
     * 
     * @since   SINCE-TDB
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
     * @return  The request identifier.
     * 
     * @since   SINCE-TDB
     */
    public String getRequestId()
    {
        return this.requestId;
    }

    
    /**
     * This returns the configured timeout for the request.
     * 
     * @return  The configured timeout for the request.
     * 
     * @since   SINCE-TDB
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
