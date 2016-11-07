/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.task;

/**
 * This is a service task that is created and managed by the service manager.
 *
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TDB
 */
public class ServiceTask<T>
{
    /*
     * The callback for the task
     */
    private T callback = null;
   
    /*
     * The timestamp for this task.
     */
    private long timestamp = -1;
    
    /*
     * The timeout for this task.
     */
    private long timeout = -1;
    
    /*
     * The request identifier.
     */
    public String requestId = null;
    
    
    /**
     * ServiceTask constructor
     * 
     * @param   callback  The callback for this task.
     * @param   timeout   The timeout for this task.
     * 
     * @since   SINCE-TDB
     */
    public ServiceTask(String requestId, T callback, long timeout)
    {
        super();
        
        if (requestId == null)
        {
            throw new IllegalArgumentException("The request identifier is not set.");
        }
        
        this.requestId = requestId;
        
        this.callback = callback;
        
        this.timeout = timeout;
        
        this.timestamp = System.currentTimeMillis();
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
     * This returns the timestamp for this task.
     * 
     * @return  The timestamp for this task.
     * 
     * @since   SINCE-TDB
     */
    public long getTimestamp()
    {
        return this.timestamp;
    }
    
    
    /**
     * This returns the timeout for this task.
     * 
     * @return  The timeout for this task.
     * 
     * @since   SINCE-TDB
     */
    public long getTimeout()
    {
        return this.timeout;
    }

    
    /**
     * This returns the service callback for this task.
     * 
     * @return  The service callback for this task.
     * 
     * @since   SINCE-TDB
     */
    public T getServiceCallback()
    {
        return this.callback;
    }
    
    
    /**
     * This returns true if this task has timed out.
     * 
     * @param   currentTime  The current time.
     * 
     * @return  True if the task has timed out, else false.
     * 
     * @since   SINCE-TDB
     */
    public boolean hasTimedout(long currentTime)
    {
        // if the timeout value not greater than zero then it is infinite.
        // an infinite value is set by synchronous calls since they are 
        // managed separately. 
        if (this.timeout <= 0)
        {
            return false;
        }
        
        long elapsedTime = currentTime - this.timestamp;
        
        return (elapsedTime >= this.timeout);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        
        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        final ServiceTask<T> that = (ServiceTask<T>) o;

        return this.requestId.equals(that.requestId);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int result = this.requestId.hashCode();

        return result;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override 
    public String toString()
    {
        StringBuilder builder = new StringBuilder("ServiceTask{");

        builder.append("requestId=").append(this.getRequestId());
        builder.append(", timeout=").append(this.getTimeout());
        builder.append(", timestamp=").append(this.getTimestamp());
        builder.append("}");

        return builder.toString();
    }
}
