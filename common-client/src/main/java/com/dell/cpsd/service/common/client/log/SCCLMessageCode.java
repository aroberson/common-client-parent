/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.log;

import com.dell.cpsd.service.common.client.i18n.SCCLMessageBundle;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is the message code enum for the service common client library.
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
public enum SCCLMessageCode
{
    TIMEOUT_TASK_CHECK_E(2001, "SCCL2001E"),
    ERROR_CALLBACK_FAIL_E(2002, "SCCL2002E"),
    EXECUTOR_SHUTDOWN_I(2003, "SCCL2003I"),
    WAIT_ON_REQUESTS_I(2004, "SCCL2004I"),
    MESSAGE_TIMEOUT_E(2005, "SCCL2005E"),
    PUBLISH_MESSAGE_FAIL_E(2006, "SCCL2006E"),
    MANAGER_SHUTDOWN_E(2007, "SCCL2007E"),
    REUSE_2008_U(2008, "SCCL2008U"),
    REUSE_2009_U(2009, "SCCL2009U"),
    REUSE_2010_U(2010, "SCCL2010U");

    /*
     * The path to the resource bundle
     */
    private static ResourceBundle BUNDLE = ResourceBundle.getBundle(SCCLMessageBundle.class.getName());

    /*
     * The error code.
     */
    private final int             errorCode;

    /*
     * The message code.
     */
    private final String          messageCode;

    /**
     * SCCLMessageCode constructor
     * 
     * @param errorCode
     *            The error code.
     * @param messageCode
     *            The message code.
     * 
     * @since 1.0
     */
    SCCLMessageCode(int errorCode, String messageCode)
    {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
    }

    /**
     * This returns the message code.
     * 
     * @return The message code.
     * 
     * @since 1.0
     */
    public String getMessageCode()
    {
        return this.messageCode;
    }

    /**
     * This returns the error code.
     * 
     * @return The error code.
     * 
     * @since 1.0
     */
    public int getErrorCode()
    {
        return this.errorCode;
    }

    /**
     * This returns the error text.
     * 
     * @return The error text.
     * 
     * @since 1.0
     */
    public String getErrorText()
    {
        try
        {
            return BUNDLE.getString(this.messageCode);

        }
        catch (MissingResourceException exception)
        {
            return this.messageCode;
        }
    }

    /**
     * This formats the message using the array of parameters.
     * 
     * @param params
     *            The message parameters.
     * 
     * @return The localized message populated with the parameters.
     * 
     * @since 1.0
     */
    public String getMessageText(Object[] params)
    {
        String message = null;

        try
        {
            message = BUNDLE.getString(this.messageCode);

        }
        catch (MissingResourceException exception)
        {
            return this.messageCode;
        }

        if ((params == null) || (params.length == 0))
        {
            return message;
        }

        return MessageFormat.format(message, params);
    }

}
