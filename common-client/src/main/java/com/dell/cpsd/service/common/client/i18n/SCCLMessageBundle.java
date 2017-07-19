/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.i18n;

import java.util.ListResourceBundle;

/**
 * This is the resource bundle for the service common client library.
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
public class SCCLMessageBundle extends ListResourceBundle
{
    /*
     * The content of this message resource bundle.
     */
    private static final Object[][] CONTENTS = {{"SCCL2001E", "SCCL2001E Unexpected error on request timeout checking. Reason [{0}]"},
            {"SCCL2002E", "SCCL2002E Unexpected error on callback [{0}]. Reason [{1}]"},
            {"SCCL2003I", "SCCL2003I Shutting down the scheduled executor service."},
            {"SCCL2004I", "SCCL2004I Waiting for pending requests to complete."},
            {"SCCL2005E", "SCCL2005E The service request [{0}] has timed out after [{1}] msec."}, {"SCCL2006U", "SCCL2006U "},
            {"SCCL2007U", "SCCL2007U "}, {"SCCL2008U", "SCCL2008U "}, {"SCCL2009U", "SCCL2009U "}, {"SCCL2010U", "SCCL2010U "}};

    /**
     * SCCLMessageBundle constructor.
     * 
     * @since 1.0
     */
    public SCCLMessageBundle()
    {
        super();
    }

    /**
     * This returns the messages for this resource bundle.
     * 
     * @return The messages for this resource bundle.
     * 
     * @since 1.0
     */
    @Override
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
