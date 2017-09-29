/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.service.common.client.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * JSch wrapper client.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class SSHClient
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String MD5_CHECKSUM = "md5sum %s";
    private static final String SCP_FILE_COMMAND = "scp -p -t %s";
    private static final String SCP_DOWNLOAD_COMMAND = "scp -f %s";
    private static final String TEMP_SUFFIX = ".part";
    private static final String MV_COMMAND = "mv -f %s %s";

    private static final int SSH_DEFAULT_PORT = 22;
    private long EXEC_MAX_DURATION_IN_MS = Long.MAX_VALUE;
    private final String hostname;
    private String username;
    private String password;
    private final int port;
    private Session session;
    private JSch jsch;

    public SSHClient(String hostname, String username, String password, long maxDurationInMs)
    {
        this(hostname, SSH_DEFAULT_PORT, username, password);
        EXEC_MAX_DURATION_IN_MS = maxDurationInMs;
    }

    public SSHClient(String hostname, int port, String username, String password)
    {
        logger.info("Creating SSHClient for {}", hostname);

        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;

        this.jsch = new JSch();
    }

    void setJsch(JSch jsch)
    {
        this.jsch = jsch;
    }

    public void openConnection() throws IOException
    {
        openConnection(0);
    }

    public void openConnection(int keepAliveInMili) throws IOException
    {
        try
        {
            session = jsch.getSession(username, hostname, port);
            session.setServerAliveInterval(keepAliveInMili);
        }
        catch (JSchException e)
        {
            throw new IOException(String.format("Could not getSession for %s", hostname), e);
        }

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //config.put("PreferredAuthentications", "password");
        session.setConfig(config);
        session.setPassword(password);

        try
        {
            session.connect();
        }
        catch (JSchException e)
        {
            throw new IOException(String.format("Could not connect session for %s", hostname), e);
        }
    }

    public void closeConnection()
    {
        if (session != null && session.isConnected())
        {
            session.disconnect();
        }
    }

    public boolean isConnected()
    {
        return (session != null && session.isConnected());
    }

    public void copyFile(String localPath, String remotePath) throws IOException
    {
        logger.debug("SSHClient - Copying file from {} to {}@{}:{}", localPath, username, hostname, remotePath);

        File sourceFile = new File(localPath);
        FileInputStream fis = null;
        InputStream in = null;
        OutputStream out = null;

        ChannelExec channel = null;
        try
        {
            channel = (ChannelExec) session.openChannel("exec");
            if (channel == null)
            {
                throw new IOException(String.format("Could not open SSH exec channel to %s", hostname));
            }

            channel.setCommand(String.format(SCP_FILE_COMMAND, remotePath));

            out = channel.getOutputStream();
            in = channel.getInputStream();

            channel.connect();
            verifyAck(in);

            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            long lastModified = sourceFile.lastModified() / 1000;
            out.write(String.format("T%d 0 %d 0\n", lastModified, lastModified).getBytes());
            out.flush();
            verifyAck(in);

            String tempFileName = sourceFile.getName() + TEMP_SUFFIX;
            // send "C0644 fileSize filename",
            out.write(String.format("C0644 %d %s\n", sourceFile.length(), tempFileName).getBytes());
            out.flush();
            verifyAck(in);

            fis = new FileInputStream(localPath);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            DigestInputStream dis = new DigestInputStream(fis, messageDigest);
            IOUtils.copy(dis, out);

            // send '\0'
            out.write(new byte[] {0}, 0, 1);
            out.flush();
            verifyAck(in);

            String localDigest = Hex.encodeHexString(messageDigest.digest());
            SSHCommandResponse response = executeCommand(String.format(MD5_CHECKSUM, remotePath + tempFileName));
            if (response.getExitCode() != 0)
            {
                throw new IOException(String.format("Failed to run md5sum to calculate checksum: %s", response.getError()));
            }
            String remoteDigest = response.getResponse().split(" ")[0];

            if (!localDigest.equalsIgnoreCase(remoteDigest))
            {
                throw new IOException("Error sending file using SCP: MD5 digest differs");
            }

            response = executeCommand(String.format(MV_COMMAND, remotePath + tempFileName, remotePath + sourceFile.getName()));

            if (response.getExitCode() != 0)
            {
                throw new IOException(String.format("Failed to rename file after upload: %s", response.getError()));
            }

            logger.debug("SSHClient - Successfully copied file from " + "{} to {}@{}:{}", localPath, username, hostname, remotePath);
        }
        catch (JSchException e)
        {
            throw new IOException(String.format("Error sending file using SCP: %s", e.getLocalizedMessage()), e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IOException(String.format("Error sending file using SCP. MD5 algorithm not found: %s", e.getLocalizedMessage()), e);
        }
        finally
        {
            if (channel != null && channel.isConnected())
            {
                channel.disconnect();
            }

            if (in != null)
            {
                in.close();
            }

            if (out != null)
            {
                out.close();
            }

            if (fis != null)
            {
                fis.close();
            }
        }
    }

    public void downloadFile(String remotePath, String localPath) throws IOException
    {
        logger.debug("SSHClient - Downloading file from {}@{}:{} to {}", username, hostname, remotePath, localPath);

        InputStream in = null;
        OutputStream out = null;
        FileOutputStream fos = null;

        ChannelExec channel = null;
        try
        {
            channel = (ChannelExec) session.openChannel("exec");
            if (channel == null)
            {
                throw new IOException(String.format("Could not open SSH exec channel to %s", hostname));
            }

            channel.setCommand(String.format(SCP_DOWNLOAD_COMMAND, remotePath));

            out = channel.getOutputStream();
            in = channel.getInputStream();

            channel.connect();
            // send '\0'
            out.write(new byte[] {0}, 0, 1);
            out.flush();

            byte[] buf = new byte[1024];
            verifyAck(in);

            // read '0644 '
            in.read(buf, 0, 5);

            // read the filesize
            long fileSize = 0L;
            while (true)
            {
                if (in.read(buf, 0, 1) < 0)
                {
                    // error
                    break;
                }
                if (buf[0] == ' ')
                {
                    break;
                }
                fileSize = fileSize * 10L + (long) (buf[0] - '0');
            }

            // read the filename
            while (in.read() != 0x0a)
            {
                ;
            }

            // send '\0'
            out.write(new byte[] {0}, 0, 1);
            out.flush();

            // read a content of the file
            fos = new FileOutputStream(localPath);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            DigestInputStream dis = new DigestInputStream(in, messageDigest);

            int length;
            while (true)
            {
                if (buf.length < fileSize)
                {
                    length = buf.length;
                }
                else
                {
                    length = (int) fileSize;
                }
                length = dis.read(buf, 0, length);
                if (length < 0)
                {
                    throw new IOException("Error while reading file in download using SCP");
                }
                fos.write(buf, 0, length);
                fileSize -= length;
                if (fileSize == 0L)
                {
                    break;
                }
            }
            verifyAck(in);

            // send '\0'
            out.write(new byte[] {0}, 0, 1);
            out.flush();

            String localDigest = Hex.encodeHexString(messageDigest.digest());
            SSHCommandResponse response = executeCommand(String.format(MD5_CHECKSUM, remotePath));
            if (response.getExitCode() != 0)
            {
                throw new IOException(String.format("Failed to run md5sum to calculate checksum: %s", response.getError()));
            }
            String remoteDigest = response.getResponse().split(" ")[0];

            if (!localDigest.equalsIgnoreCase(remoteDigest))
            {
                throw new IOException("Error downloading file using SCP: MD5 digest differs");
            }

            logger.debug("SSHClient - Successfully copied file from " + "{}@{}:{} to {}", username, hostname, remotePath, localPath);

        }
        catch (JSchException e)
        {
            throw new IOException(String.format("Error downloading file using SCP: %s", e.getMessage()), e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IOException(String.format("Error sending file using SCP. MD5 algorithm not found: %s", e.getLocalizedMessage()), e);
        }
        finally
        {
            if (channel != null && channel.isConnected())
            {
                channel.disconnect();
            }

            if (in != null)
            {
                in.close();
            }

            if (out != null)
            {
                out.close();
            }

            if (fos != null)
            {
                fos.close();
            }
        }
    }

    public String executeInteractiveCommand(List<String> commands, int execTime)
    {
        Channel channel = null;
        try
        {
            //Create interactive shell channel
            channel = session.openChannel("shell");//only shell
            //Get the output stream
            PrintStream shellStream = new PrintStream(channel.getOutputStream());
            //Get the input stream
            InputStream inputStream = channel.getInputStream();

            //Connect to channel
            channel.connect();

            for (String command : commands)
            {
                //Send command to SSH
                shellStream.print(command);
                shellStream.flush();
            }

            //Wait for command to execute
            Thread.sleep(execTime);

            StringBuilder input = new StringBuilder();
            byte[] tmp = new byte[1024];
            while (inputStream.available() > 0)
            {
                int i = inputStream.read(tmp, 0, 1024);
                if (i < 0)
                {
                    break;
                }

                input.append(new String(tmp, 0, i));
            }

            return input.toString();
        }
        catch (Throwable ex)
        {
            logger.error("Error executing shell interacive command", ex);
            return null;
        }
        finally
        {
            if (channel != null && channel.isConnected())
            {
                try
                {
                    channel.disconnect();
                }
                catch (Throwable ex)
                {
                    logger.error("Error disconnecting channel", ex);
                }
            }
        }
    }

    public SSHCommandResponse executeSensitiveCommand(String command) throws IOException
    {
        return executeCommand(command, true);
    }

    public SSHCommandResponse executeCommand(String command) throws IOException
    {
        return executeCommand(command, false);
    }

    public SSHCommandResponse executeCommand(String command, boolean sensitive) throws IOException
    {
        return executeCommand(command, sensitive, false);
    }

    public SSHCommandResponse executeCommand(String command, boolean sensitive, boolean trace) throws IOException
    {
        return executeCommand(command, sensitive, trace, true);
    }

    public SSHCommandResponse executeCommand(String command, boolean sensitive, boolean trace, boolean writeWarnForFail) throws IOException
    {
        try
        {
            if (sensitive)
            {
                logger.debug("SSHClient - Executing sensitive command on {}", hostname);
            }
            else
            {
                logger.debug("SSHClient - Executing command {} on {}", command, hostname);
            }

            command += "; echo \"exitCode=$?\"";

            SSHCommandResponse response = null;
            ChannelExec channel = null;

            long execStartTS = 0L;

            try
            {
                session.setTimeout(EXEC_MAX_DURATION_IN_MS <= (long) Integer.MAX_VALUE ? (int) EXEC_MAX_DURATION_IN_MS : Integer.MAX_VALUE);
                channel = (ChannelExec) session.openChannel("exec");
                if (channel == null)
                {
                    throw new IOException(String.format("Could not open SSH exec channel to %s", hostname));
                }

                channel.setCommand(command);
                InputStream inputStream = channel.getInputStream();
                InputStream errorStream = channel.getErrStream();

                channel.connect();

                StringBuilder input = new StringBuilder();
                StringBuilder error = new StringBuilder();
                byte[] tmp = new byte[1024];

                execStartTS = (new Date()).getTime();
                boolean activeExec = true;
                while (activeExec)
                {
                    activeExec = ((new Date()).getTime() - execStartTS) <= EXEC_MAX_DURATION_IN_MS;
                    while (inputStream.available() > 0)
                    {
                        int i = inputStream.read(tmp, 0, 1024);
                        if (i < 0)
                        {
                            execStartTS = 0L;
                            break;
                        }

                        input.append(new String(tmp, 0, i));
                    }

                    while (errorStream.available() > 0)
                    {
                        int i = errorStream.read(tmp, 0, 1024);
                        if (i < 0)
                        {
                            execStartTS = 0L;
                            break;
                        }

                        error.append(new String(tmp, 0, i));
                    }

                    if ((channel.isClosed()) || (channel.getExitStatus() != -1))
                    {
                        String stdOut = input.toString();
                        if (stdOut.contains("exitCode="))
                        {
                            List<String> stdOutLines = new LinkedList<String>(Arrays.asList(stdOut.split("\n")));
                            int exitCode;
                            try
                            {
                                exitCode = Integer.parseInt(stdOutLines.remove(stdOutLines.size() - 1).replace("exitCode=", ""));
                            }
                            catch (NumberFormatException e)
                            {
                                response = new SSHCommandResponse(-1, stdOut, error.toString());
                                execStartTS = 0L;
                                break;
                            }
                            String trimmedStdOut = StringUtils.join(stdOutLines, "\n");
                            response = new SSHCommandResponse(exitCode, trimmedStdOut, error.toString());
                        }
                        else
                        {
                            // If we didn't get the exitCode in the stdOut then
                            // the exit code we do have is just that of the echo
                            // so we can't tell if it was successful or not.
                            // Let's assume it wasn't and return -1
                            response = new SSHCommandResponse(-1, stdOut, error.toString());
                        }

                        execStartTS = 0L;
                        break;
                    }

                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        logger.warn("Exception occurred during sleep", e);
                    }
                }
            }
            catch (JSchException e)
            {
                throw new IOException(String.format("Error executing command: %s", e.getMessage()), e);
            }
            finally
            {
                if (channel != null && channel.isConnected())
                {
                    channel.disconnect();
                }
            }

            if (execStartTS != 0L)
            {
                throw new IOException("Exceeding time to execute command " + command);
            }

            if (sensitive)
            {
                logger.debug("SSHClient - Executing sensitive command on {} . " + "ExitCode={}, Response={}, Error={}", hostname,
                        response.getExitCode(), response.getResponse(), response.getError());
            }
            else if (!trace)
            {
                logger.debug("Executed command {} on {}. " + "ExitCode={}, Response={}, Error={}", command, hostname,
                        response.getExitCode(), response.getResponse(), response.getError());
            }
            else
            {
                logger.trace("Executed command {} on {}. " + "ExitCode={}, Response={}, Error={}", command, hostname,
                        response.getExitCode(), response.getResponse(), response.getError());
            }

            //Write the command to the regular log if failed
            if (response.getExitCode() != 0 && !sensitive && writeWarnForFail)
            {
                logger.warn(String.format("Failed running command: %s.\nError is: %s", command, response.getError()));
            }
            return response;
        }
        catch (IOException e)
        {
            //Write the command to the regular log if failed
            if (!sensitive)
            {
                logger.warn(String.format("Failed running command: %s.", command), e);
            }
            throw e;
        }
    }

    private static void verifyAck(InputStream in) throws IOException
    {
        int b = in.read();

        if (b == 0)
        {
            return;
        }

        // It seems that sometimes SCP returns 67, so I hope that's good as well
        if (b == 67)
        {
            return;
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        if (b == 1)
        {
            throw new IOException(String.format("Error occurred during SCP: %s", bufferedReader.readLine()));
        }

        if (b == 2)
        {
            throw new IOException(String.format("Fatal error occurred during SCP: %s", bufferedReader.readLine()));
        }

        throw new IOException(String.format("Unknown SCP ack value: %d", b));
    }
}
