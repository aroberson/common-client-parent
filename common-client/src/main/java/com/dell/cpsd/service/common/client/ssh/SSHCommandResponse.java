/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.service.common.client.ssh;

/**
 * JSch command response.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class SSHCommandResponse
{
    private int exitCode;

    private String response;

    private String error;

    public SSHCommandResponse(int exitCode, String response, String error)
    {
        this.exitCode = exitCode;
        this.response = response;
        this.error = error;
    }

    public int getExitCode()
    {
        return exitCode;
    }

    public String getResponse()
    {
        return response;
    }

    public String getError()
    {
        return error;
    }

    @Override
    public String toString()
    {
        return "SSHCommandResponse{" + "exitCode=" + exitCode + ", response='" + response + '\'' + ", error='" + error + '\'' + '}';
    }
}
