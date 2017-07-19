/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.callback;

/**
 * This class contains the results of a service request.
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
public class ServiceResponse<T>
{
    /*
     * The request identifier.
     */
    private String requestId = null;

    /*
     * The message associated with the service response.
     */
    private String message   = null;

    /*
     * The data associated with the service response.
     */
    private T      response  = null;

    /**
     * ServiceResponse constructor
     * 
     * @param requestId
     *            The request identifier.
     * @param response
     *            The reponse data.
     * @param message
     *            The reponse message.
     * 
     * @since 1.0
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
     * @return The request identifier.
     * 
     * @since 1.0
     */
    public String getRequestId()
    {
        return this.requestId;
    }

    /**
     * This returns any message associated with the service response.
     * 
     * @return The message associated with the service response.
     * 
     * @since 1.0
     */
    public String getMessage()
    {
        return this.message;
    }

    /**
     * This returns the data for the service response.
     * 
     * @return The data for the service response.
     * 
     * @since 1.0
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
