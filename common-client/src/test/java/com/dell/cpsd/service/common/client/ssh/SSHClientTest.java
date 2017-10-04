/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.service.common.client.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 * <p>
 * Unit test for PropertySplitter.
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SSHClientTest
{
    @Mock
    private JSch jsch;

    @Mock
    private Session session;

    @Mock
    private ChannelExec channelExec;

    @Mock
    private InputStream errorStream;

    @Mock
    private SSHCommandResponse sshCommandResponse;

    private SSHClient client;
    private String hostname    = "the_hostname";
    private String username    = "the_username";
    private String password    = "the_password";
    private String localPath   = "src/test/resources/test.txt";
    private String remotePath  = "/remote/file/path/test.txt";
    private String myRemotePath  = "path/";
    private int    maxDuration = 0;
    private String sshCommandResponseString;

    private ByteArrayInputStream  inputStream;
    private ByteArrayOutputStream outputStream;
    private String fileContents = "hello world!";

    @Before
    public void setUp() throws Exception
    {
        this.client = spy(new SSHClient(this.hostname, this.username, this.password, this.maxDuration));
        this.client.setJsch(this.jsch);

        this.inputStream = spy(new ByteArrayInputStream(this.fileContents.getBytes()));
        this.outputStream = spy(new ByteArrayOutputStream(this.fileContents.length()));

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        DigestInputStream dis = new DigestInputStream(this.inputStream, messageDigest);
        IOUtils.copy(dis, this.outputStream);
        this.sshCommandResponseString = Hex.encodeHexString(messageDigest.digest());
    }

    @Test
    public void openConnection_should_successfully_open_a_connection() throws IOException, JSchException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());

        this.client.openConnection();

        verify(this.session).setPassword(this.password);
        verify(this.session).connect();
    }

    @Test
    public void openConnection_should_throw_an_exception_if_a_session_cannot_be_created() throws JSchException
    {
        doThrow(new JSchException("some-io-error")).when(this.jsch).getSession(anyString(), anyString(), anyInt());

        try
        {
            this.client.openConnection();
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("could not getsession"));
        }
    }

    @Test
    public void openConnection_should_throw_an_exception_if_a_session_cannot_be_connected() throws JSchException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doThrow(new JSchException("some-io-error")).when(this.session).connect();

        try
        {
            this.client.openConnection();
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("could not connect session"));
        }
    }

    @Test
    public void closeConnection_should_close_an_open_session_connection() throws IOException, JSchException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(true).when(this.session).isConnected();
        this.client.openConnection();

        this.client.closeConnection();

        verify(this.session).disconnect();
    }

    @Test
    public void closeConnection_should_not_close_an_unopened_session_connection() throws IOException, JSchException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(false).when(this.session).isConnected();
        this.client.openConnection();

        this.client.closeConnection();

        verify(this.session, never()).disconnect();
    }

    @Test
    public void isConnected_should_return_true_if_a_session_connection_is_open() throws IOException, JSchException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(true).when(this.session).isConnected();
        this.client.openConnection();

        boolean result = this.client.isConnected();

        assertThat(result, is(true));
    }

    @Test
    public void isConnected_should_return_false_if_a_session_connection_is_not_open() throws IOException, JSchException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(false).when(this.session).isConnected();
        this.client.openConnection();

        boolean result = this.client.isConnected();

        assertThat(result, is(false));
    }

    @Test
    public void copyFile_should_successfully_copy_a_file_to_the_remote_host() throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(this.inputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 67).when(this.inputStream).read();
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(0).when(this.sshCommandResponse).getExitCode();
        doReturn(this.sshCommandResponseString).when(this.sshCommandResponse).getResponse();
        this.client.openConnection();

        this.client.copyFile(this.localPath, this.remotePath);

        verify(this.client, times(2)).executeCommand(anyString());
    }

    @Test
    public void copyFile_should_throw_an_exception_if_a_session_channel_cannot_be_opened()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(null).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.copyFile(this.localPath, this.remotePath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("could not open ssh exec channel"));
        }
    }

    @Test
    public void copyFile_should_throw_an_exception_if_the_md5sum_remote_command_failed()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(this.inputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 67).when(this.inputStream).read();// verifyAck...
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(1).when(this.sshCommandResponse).getExitCode();
        this.client.openConnection();

        try
        {
            this.client.copyFile(this.localPath, this.remotePath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("failed to run md5sum"));
        }
    }

    @Test
    public void copyFile_should_throw_an_exception_if_there_was_an_error_sending_the_file_over_scp()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(this.inputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 67).when(this.inputStream).read();// verifyAck...
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(0).when(this.sshCommandResponse).getExitCode();
        doReturn("bogus-digest").when(this.sshCommandResponse).getResponse();
        this.client.openConnection();

        try
        {
            this.client.copyFile(this.localPath, this.remotePath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("error sending file using scp"));
        }
    }

    @Test
    public void copyFile_should_throw_an_exception_if_the_mv_remote_command_failed()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(this.inputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 67).when(this.inputStream).read();// verifyAck...
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(0, 1).when(this.sshCommandResponse).getExitCode();
        doReturn(this.sshCommandResponseString).when(this.sshCommandResponse).getResponse();
        this.client.openConnection();

        try
        {
            this.client.copyFile(this.localPath, this.remotePath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("failed to rename file after upload"));
        }
    }

    @Test
    public void copyFile_should_throw_an_exception_if_there_was_an_error_opening_a_channel()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doThrow(new JSchException("some-error")).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.copyFile(this.localPath, this.remotePath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("error sending file using scp"));
        }
    }

    @Test
    public void createRemoteDirectory()  throws IOException, JSchException {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doThrow(new JSchException("some-error")).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.createRemoteDirectory(this.myRemotePath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage(), containsString("Failed to create create remote directory"));
        }
    }

    @Test
    public void downloadFile_should_successfully_download_a_file_from_the_remote_host()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        String downloadString = "0644 " + this.fileContents.length() + " " + this.fileContents;
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream(downloadString.getBytes()));
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 0x0a, 67).when(byteArrayInputStream).read();
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(0).when(this.sshCommandResponse).getExitCode();
        doReturn(this.sshCommandResponseString).when(this.sshCommandResponse).getResponse();
        this.client.openConnection();

        this.client.downloadFile(this.remotePath, this.localPath);

        verify(this.client).executeCommand(anyString());
    }

    @Test
    public void downloadFile_should_throw_an_exception_if_a_session_channel_cannot_be_opened()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(null).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.downloadFile(this.remotePath, this.localPath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("could not open ssh exec channel"));
        }
    }

    @Test
    public void downloadFile_should_throw_an_exception_if_the_md5sum_remote_command_failed()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        String downloadString = "0644 " + this.fileContents.length() + " " + this.fileContents;
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream(downloadString.getBytes()));
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 0x0a, 67).when(byteArrayInputStream).read();
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(1).when(this.sshCommandResponse).getExitCode();
        this.client.openConnection();

        try
        {
            this.client.downloadFile(this.remotePath, this.localPath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("failed to run md5sum"));
        }
    }

    @Test
    public void downloadFile_should_throw_an_exception_if_there_was_an_error_sending_the_file_over_scp()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        String downloadString = "0644 " + this.fileContents.length() + " " + this.fileContents;
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream(downloadString.getBytes()));
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(0, 0x0a, 67).when(byteArrayInputStream).read();
        doReturn(this.sshCommandResponse).when(this.client).executeCommand(anyString());
        doReturn(true).when(this.channelExec).isConnected();
        doReturn(0).when(this.sshCommandResponse).getExitCode();
        doReturn("bogus-digest").when(this.sshCommandResponse).getResponse();
        this.client.openConnection();

        try
        {
            this.client.downloadFile(this.remotePath, this.localPath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("error downloading file using scp"));
        }
    }

    @Test
    public void downloadFile_should_throw_an_exception_if_there_was_an_error_downloading_the_remote_file()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doThrow(new JSchException("some-error")).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.downloadFile(this.remotePath, this.localPath);
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("error downloading file using scp"));
        }
    }

    @Test
    public void executeCommand_should_successfully_execute_the_command_on_the_remote_host()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream("hello\nexitCode=0".getBytes()));

        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.errorStream).when(this.channelExec).getErrStream();
        doReturn(1, 1).when(byteArrayInputStream).available();
        doReturn(1, 1).when(this.errorStream).available();
        doReturn(0, -1).when(this.errorStream).read(any(), anyInt(), anyInt());
        doReturn(true).when(this.channelExec).isConnected();
        this.client.openConnection();

        SSHCommandResponse result = this.client.executeCommand("ls -latr");

        assertNotNull(result);
        assertThat(result.getExitCode(), is(0));
    }

    @Test
    public void executeCommand_should_handle_a_bad_exit_code_from_the_remote_command()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream("hello\nexitCode=aaaaa".getBytes()));

        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.errorStream).when(this.channelExec).getErrStream();
        doReturn(1, 1).when(byteArrayInputStream).available();
        doReturn(1, 1).when(this.errorStream).available();
        doReturn(0, -1).when(this.errorStream).read(any(), anyInt(), anyInt());
        doReturn(true).when(this.channelExec).isConnected();
        this.client.openConnection();

        SSHCommandResponse result = this.client.executeCommand("ls -latr");

        assertNotNull(result);
        assertThat(result.getExitCode(), is(-1));
    }

    @Test
    public void executeCommand_should_handle_no_exit_code_from_the_remote_command()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream("hello\n".getBytes()));

        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.errorStream).when(this.channelExec).getErrStream();
        doReturn(1, 1).when(byteArrayInputStream).available();
        doReturn(1, 1).when(this.errorStream).available();
        doReturn(0, -1).when(this.errorStream).read(any(), anyInt(), anyInt());
        doReturn(true).when(this.channelExec).isConnected();
        this.client.openConnection();

        SSHCommandResponse result = this.client.executeCommand("ls -latr");

        assertNotNull(result);
        assertThat(result.getExitCode(), is(-1));
    }

    @Test
    public void executeCommand_should_throw_an_exception_if_there_was_an_error_executing_the_remote_command()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doThrow(new JSchException("some-error")).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.executeCommand("ls -latr");
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("some-error"));
        }
    }

    @Test
    public void executeCommand_should_throw_an_exception_if_there_was_an_error_opening_a_channel()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(null).when(this.session).openChannel(anyString());
        this.client.openConnection();

        try
        {
            this.client.executeCommand("ls -latr");
            fail("Expected exception to be thrown here but was not");
        }
        catch (IOException ex)
        {
            assertThat(ex.getMessage().toLowerCase(), containsString("could not open ssh exec channel"));
        }
    }

    @Test
    public void executeSensitiveCommand_should_successfully_execute_the_command_on_the_remote_host()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        ByteArrayInputStream byteArrayInputStream = spy(new ByteArrayInputStream("hello\nexitCode=0".getBytes()));

        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(byteArrayInputStream).when(this.channelExec).getInputStream();
        doReturn(this.errorStream).when(this.channelExec).getErrStream();
        doReturn(1, 1).when(byteArrayInputStream).available();
        doReturn(1, 1).when(this.errorStream).available();
        doReturn(0, -1).when(this.errorStream).read(any(), anyInt(), anyInt());
        doReturn(true).when(this.channelExec).isConnected();
        this.client.openConnection();

        SSHCommandResponse result = this.client.executeSensitiveCommand("ls -latr");

        assertNotNull(result);
        assertThat(result.getExitCode(), is(0));
    }

    @Test
    public void executeInteractiveCommand_should_successfully_execute_the_commands_on_the_remote_host()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doReturn(this.channelExec).when(this.session).openChannel(anyString());
        doReturn(this.inputStream).when(this.channelExec).getInputStream();
        doReturn(this.outputStream).when(this.channelExec).getOutputStream();
        doReturn(1, 1).when(this.inputStream).available();
        doReturn(0, -1).when(this.inputStream).read(any(), anyInt(), anyInt());
        doReturn(true).when(this.channelExec).isConnected();
        this.client.openConnection();

        final String result = this.client.executeInteractiveCommand(Arrays.asList("", ""), this.maxDuration);

        assertNotNull(result);
    }

    @Test
    public void executeInteractiveCommand_should_return_null_if_there_is_an_error_executing_the_remote_commands()
            throws IOException, JSchException, NoSuchAlgorithmException
    {
        doReturn(this.session).when(this.jsch).getSession(anyString(), anyString(), anyInt());
        doThrow(new JSchException("some-error")).when(this.session).openChannel(anyString());
        this.client.openConnection();

        final String result = this.client.executeInteractiveCommand(Arrays.asList("", ""), this.maxDuration);

        assertNull(result);
    }
}