/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.service.common.client.i18n;

import java.util.ListResourceBundle;

/**
 * This is the resource bundle for the service common client library.
 * 
 * <p/>
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * <p/>
 * 
 * @version 1.0
 * 
 * @since   SINCE-TBD
 */
public class SCCLMessageBundle extends ListResourceBundle
{
    /*
     * The content of this message resource bundle.
     */
    private static final Object[][] CONTENTS = 
    {
        {"SCCL2001E", "SCCL2001E Unexpected error on request timeout checking. Reason [{0}]"},
        {"SCCL2002U", "SCCL2002U "},
        {"SCCL2003U", "SCCL2003U "},
        {"SCCL2004U", "SCCL2004U "},
        {"SCCL2005U", "SCCL2005U "},
        {"SCCL2006U", "SCCL2006U "},  
        {"SCCL2007U", "SCCL2007U "},
        {"SCCL2008U", "SCCL2008U "},
        {"SCCL2009U", "SCCL2009U "},
        {"SCCL2010U", "SCCL2010U "}
    };
    
    
    /**
     * SCCLMessageBundle constructor.
     * 
     * @since   SINCE-TBD
     */
    public SCCLMessageBundle()
    {
        super();
    }

    
    /**
     * This returns the messages for this resource bundle.
     * 
     * @return  The messages for this resource bundle.
     * 
     * @since   SINCE-TBD
     */
    @Override
    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
