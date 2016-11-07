/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */
 
package com.dell.cpsd.service.common.client.callback;

/**
 * This is an implementation of <code>IServiceCallback</code> that is used
 * for synchronous processing of a service request.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TDB
 */
public class ServiceCallback<T extends ServiceResponse<?>> implements IServiceCallback<T>
{   
    /*
     * The flag to indicate that the callback is complete
     */
    private volatile boolean done = false;

    /*
     * The service error.
     */
    private ServiceError error = null;
    
    /*
     * The service response
     */
    private T serviceResponse = null;
    
    
    
    /**
     * ServiceCallback constructor.
     * 
     * @since   SINCE-TDB
     */
    public ServiceCallback()
    {
        super();
    }
    
    
    /**
     * This returns a <code>ServiceError</code>, or null.
     * 
     * @return  The <code>ServiceError</code>, or null.
     * 
     * @since   SINCE-TDB
     */
    public ServiceError getServiceError()
    {
        return this.error;
    }
    
    
    /**
     * This returns the <code>ServiceResponse</code>.
     * 
     * @return  The response to the service request.
     * 
     * @since   SINCE-TDB
     */
    public T getServiceResponse()
    {
        return this.serviceResponse;
    }
    
    
    /**
     * Tihs returns true if this service callback has completed processing.
     * 
     * @return  True if this service callback is done, else false.
     * 
     * @since   SINCE-TDB
     */
    public boolean isDone()
    {
        return this.done;
    }
    
    
    /**
     * This sets the value of the done flag.
     * 
     * @param   done    The value of the done flag.
     * 
     * @since   SINCE-TDB
     */
    public void setDone(final boolean done)
    {
        this.done = done;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleServiceError(ServiceError error)
    {
        this.error = error;
        
        this.setDone(true);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleServiceTimeout(ServiceTimeout timeout)
    {
        // do nothing
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleServiceResponse(final T serviceResponse)
    {
        this.serviceResponse = serviceResponse;
        
        this.setDone(true);        
    }
}
