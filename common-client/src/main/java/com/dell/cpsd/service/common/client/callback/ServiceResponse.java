/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

/**
 * This class contains the results of a service request.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TDB
 */
public class ServiceResponse<T>
{
    /*
     * The request identifier.
     */
    private String requestId = null;
    
    /*
     * The message associated with the service response.
     */
    private String message = null;
    
    /*
     * The data associated with the service response.
     */
    private T response = null;
   

    /**
     * ServiceResponse constructor
     * 
     * @param   requestId   The request identifier.
     * @param   response    The reponse data.
     * @param   message     The reponse message.
     * 
     * @since   SINCE-TDB
     */    
    public ServiceResponse(String requestId, T response, String message)
    {
        super();
        
        if (requestId == null)
        {
            throw new IllegalArgumentException("The request identifier is not set.");
        }
        
        this.requestId = requestId;
        
        this.response = response;
        
        this.message = message;
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
     * This returns any message associated with the service response.
     * 
     * @return  The message associated with the service response.
     * 
     * @since   SINCE-TDB
     */
    public String getMessage()
    {
        return this.message;
    }
    
    
    /**
     * This returns the data for the service response.
     * 
     * @return  The data for the service response.
     * 
     * @since   SINCE-TDB
     */
    public T getResponse()
    {
        return this.response;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("ServiceResponse{requestId=");
        builder.append(this.requestId);
        builder.append(", message=");
        builder.append(this.message);
        builder.append(", response=");
        builder.append(this.response);
        builder.append("}");
        
        return builder.toString();
    }
}
