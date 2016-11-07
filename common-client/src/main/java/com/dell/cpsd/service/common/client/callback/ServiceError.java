/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;


/**
 * This class contains the error code and message information from a service 
 * error.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TDB
 */
public class ServiceError
{
    /*
     * The request identifier.
     */
    public String requestId = null;
    
    /*
     * The error code.
     */
    private String errorCode = null;
    
    /*
     * The error message
     */
    private String errorMessage = null;
    

    /**
     * ServiceError constructor
     * 
     * @param   requestId       The request identifier.
     * @param   errorCode       The error code.
     * @param   errorMessage    The error message.
     * 
     * @since   SINCE-TDB
     */
    public ServiceError(String requestId, String errorCode, String errorMessage)
    {
        super();
        
        if (requestId == null)
        {
            throw new IllegalArgumentException("The request identifier is not set.");
        }
        
        this.requestId = requestId;
        
        this.errorCode = errorCode;
        
        this.errorMessage = errorMessage;
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
     * This returns the error code.
     * 
     * @return  The error code.
     * 
     * @since   SINCE-TDB
     */
    public String getErrorCode()
    {
        return this.errorCode;
    }
    
    
    /**
     * This returns the error message
     * 
     * @return  The error message.
     * 
     * @since   SINCE-TDB
     */
    public String getErrorMessage()
    {
        return this.errorMessage;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("ServiceError{requestId=");
        builder.append(this.requestId);
        builder.append(", errorCode=");
        builder.append(this.errorCode);
        builder.append(", errorMessage=");
        builder.append(this.errorMessage);
        builder.append("}");
        
        return builder.toString();
    }
}
