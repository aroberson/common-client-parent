package com.dell.cpsd.service.common.client.log;

import com.dell.cpsd.service.common.client.i18n.SCCLMessageBundle;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;

import static org.junit.Assert.*;

/**
 * TODO: Document usage.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class SCCLMessageCodeTest
{
    SCCLMessageBundle bundle;
    @Before
    public void setUp() throws Exception
    {
        //SCCLMessageCode.valueOf("2001");
        SCCLMessageCode[] values = SCCLMessageCode.values();
        bundle = new SCCLMessageBundle();
    }

    @Test
    public void getMessageCode() throws Exception
    {
        String expected = "SCCL2001E";
        String code = SCCLMessageCode.TIMEOUT_TASK_CHECK_E.getMessageCode();

        assertEquals(expected, code);
    }

    @Test
    public void getErrorCode() throws Exception
    {
        int expected = 2001;
        int code =  SCCLMessageCode.TIMEOUT_TASK_CHECK_E.getErrorCode();

        assertEquals(expected, code);
    }

    @Test
    public void getErrorText() throws Exception
    {
        String expectedText = bundle.getString(SCCLMessageCode.EXECUTOR_SHUTDOWN_I.getMessageCode());
        String errorText = SCCLMessageCode.EXECUTOR_SHUTDOWN_I.getErrorText();

        assertEquals(expectedText, errorText);
    }

    @Test
    public void getMessageText() throws Exception
    {
        String expectedText = bundle.getString(SCCLMessageCode.EXECUTOR_SHUTDOWN_I.getMessageCode());
        String errorText = SCCLMessageCode.EXECUTOR_SHUTDOWN_I.getMessageText(null);

        assertEquals(expectedText, errorText);
    }

    @Test
    public void getMessageTextWithParam() throws Exception
    {
        String expectedText = bundle.getString(SCCLMessageCode.TIMEOUT_TASK_CHECK_E.getMessageCode());
        expectedText = MessageFormat.format(expectedText, new String[]{"param1"});
        String errorText = SCCLMessageCode.TIMEOUT_TASK_CHECK_E.getMessageText(new String[]{"param1"});

        assertEquals(expectedText, errorText);
    }

}