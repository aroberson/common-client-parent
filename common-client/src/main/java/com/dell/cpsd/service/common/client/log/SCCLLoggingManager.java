/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.log;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.common.logging.LoggingManager;
import com.dell.cpsd.service.common.client.i18n.SCCLMessageBundle;

import java.util.ResourceBundle;

/**
 * This is the logging manager for the service common client library.
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
public class SCCLLoggingManager
{
    /*
     * The message resource bundle used for logging.
     */
    private static ResourceBundle MESSAGE_BUNDLE  = ResourceBundle.getBundle(SCCLMessageBundle.class.getName());

    private static LoggingManager LOGGING_MANAGER = new LoggingManager();

    /**
     * SCCLLoggingManager constructor.
     * 
     * @since 1.0
     */
    public SCCLLoggingManager()
    {
        super();
    }

    /**
     * This returns an <code>ILogger</code> using the name of the specified class as the logger name.
     * 
     * @param clazz
     *            The <code>Class</code> used for the logger.
     * 
     * @return The <code>ILogger</code> with the name of the class.
     * 
     * @throws IllegalArgumentException
     *             Thrown if the class is null.
     * 
     * @since 1.0
     */
    public static ILogger getLogger(Class clazz)
    {
        return LOGGING_MANAGER.getLogger(clazz, MESSAGE_BUNDLE);
    }

    /**
     * This returns an <code>ILogger</code> using the specified name as the logger name.
     * 
     * @param name
     *            The name of the logger.
     * 
     * @return The <code>ILogger</code> with the specified name.
     * 
     * @since 1.0
     */
    public static ILogger getLogger(String name)
    {
        return LOGGING_MANAGER.getLogger(name, MESSAGE_BUNDLE);
    }
}
