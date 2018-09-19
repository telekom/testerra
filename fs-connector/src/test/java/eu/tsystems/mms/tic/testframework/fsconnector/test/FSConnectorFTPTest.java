/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/* 
 * Created on 13.06.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.fsconnector.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.tsystems.mms.tic.testframework.fsconnector.AbstractTest;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.tsystems.mms.tic.testframework.fsconnector.FSConnector;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;
import eu.tsystems.mms.tic.testframework.test.util.Constants;
import eu.tsystems.mms.tic.testframework.test.util.TestFileUtils;

/**
 * Tests using the FTP protocol with FSConnector.
 */
public class FSConnectorFTPTest extends AbstractTest {
    private static final String FTPSERVERIP = "127.0.0.1";
    private static final int FTPSERVERPORT = 2221;

    /** root directory of the local ftp server */
    private static final String FTPROOTDIR = "target\\ftpserver";

    /** File to download from ftp. */
    public static final String DOWNLOADFILE = "testFileToDownload.txt";

    /** File to upload to ftp. */
    private static final String UPLOADFILE = "testFileToUpload.txt";

    /** Path to directory where files are placed locally. */
    private static final String FILEDIR = System.getProperty("user.dir") + "/target/test-classes";
    /** Path to directory where temporary files are placed locally (e.g. for size comparations). */
    private static final String TEMPDIR = FILEDIR + "\\tmp";
    /** FTP User name. */
    public static final String FTPUSER = "ftp-user";
    /** FTP Password. */
    public static final String FTPPASSWD = "ftp-test";
    /** Name of provate key for SSH authentication. */
    private static final String SSHKEY = "fennec-sftp-openssh.ppk";

    Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Save an input stream in a local file under the given path.
     * 
     * @param is Input Stream
     * @param path Path to local file.
     * @return File object.
     */
    private File saveInputStream(final InputStream is, final String path) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());

        final File tempFile = new File(path);
        new File(tempFile.getParent()).mkdirs();
        OutputStream out;
        try {
            out = new FileOutputStream(tempFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            is.close();
        } catch (final FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return tempFile;
    }

    @BeforeClass
    public void setUpFTPServer() throws Exception {
        File downLoadFile = new File("src\\test\\resources\\" + DOWNLOADFILE);
        File rootFolder = new File(FTPROOTDIR);

        // delete ftpserver rootFolder if already existing
        FileUtils.deleteDirectory(rootFolder);
        // copy the needed download file into the serverfolder
        FileUtils.copyFileToDirectory(downLoadFile,rootFolder);

        // start the server
        SFTPServer.main();

        logger.info("FTP Server Setup done.");
    }

    @AfterClass
    public void tearDown() throws FtpException {
        SFTPServer.stopFTP();
        logger.info("FTP Server Shutdown done.");
    }

    /**
     * Try to download a not existing file.
     */
    @Test
    public void testT01_DownloadNotExistingFileFTP() {
        final int maxTrys = 2;
        final Source source = new Source(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/")
                .setFilename("gibtsNicht.test");
        source.setUsername(FTPUSER).setPassword(FTPPASSWD);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);
        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }

        Assert.assertTrue(excThrown);
        Assert.assertTrue(TestFileUtils.hasFileEntry(TestFileUtils.LOGFILE, "Tried getting the file " + maxTrys
                + " times without success. Cancelled transfer."));
    }

    /**
     * Try anonymous login to server.
     *
     * @throws Exception .
     */
    @Test
    public void testT02_LoginAnonymousFTP() throws Exception {
        final Source source = new Source(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername("Anonymous").setPassword("");

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        // Delete local File
        new File(FILEDIR, DOWNLOADFILE).delete();
    }

    /**
     * Try login with MMSDOM user and local FTP User.
     *
     * @throws Exception .
     */
    @Test
    public void testT03_LoginCorrectUserFTP() throws Exception {
        final Source source = new Source(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername(FTPUSER).setPassword(FTPPASSWD);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        // Delete local File
        new File(FILEDIR, DOWNLOADFILE).delete();
    }

    /**
     * Test errorhandling with non/wrong password.
     */
    @Test
    public void testT04_LoginWrongPasswordFTP() {
        Source source = new Source(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername(FTPUSER);

        Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);

        // Put resource file to upload in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Put file on FTP
        source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        destination = new Destination(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/");
        destination.setUsername(FTPUSER).setPassword("falsch");

        excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }

    /**
     * test errorhandling at non (=anonymous) or wrong username.
     */
    @Test
    public void testT05a_LoginWrongUserFTP_Source() {
        Source source = new Source(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername("noUser").setPassword("egal");

        Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }

    @Test
    public void testT05b_LoginWrongUserFTP_Destination() {
        // Put resource file to upload in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Put file on FTP
        Source source = new Source(Protocol.FILE).setPath("./").setFilename("pom.xml");
//        Source source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        Destination destination = new Destination(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/");
        destination.setPassword("falsch");

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            logger.info("Catched: ", e);
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }


        /**
         * Try to transfer files via SFTP and FTPS (TODO - no trusted certficate on test Server).
         *
         * @throws Exception .
         */
    @Test
    public void testT06_SecureConnections() throws Exception {
        // SFTP Download
        final File privateKey = saveInputStream(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(SSHKEY), FILEDIR + SSHKEY);

        final Source source = new Source(Protocol.SFTP).setHost(Constants.TESTSERVERIP).setPort("222").setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername("ta").setPassword("").setKeyPassword("your_passwd")
                .setKeyStoreFile(privateKey.getAbsolutePath());

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        final File localFile = new File(FILEDIR, DOWNLOADFILE);
        Assert.assertTrue(localFile.exists());

        // delete for other tests
        localFile.delete();
    }

    /**
     * Test successful file transfer from FTP Server to local Filesystem.
     * 
     * @throws IOException Error communicating with FTP Server.
     * @throws Exception .
     */
    @Test
    public void testT07_SuccessfulDownloadFTP() throws Exception {
        // Do copy File
        final Source source = new Source(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername(FTPUSER).setPassword(FTPPASSWD);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        // Put file from ftp in temporary directory
        final FTPClient ftpClient = new FTPClient();
        ftpClient.connect(FTPSERVERIP,FTPSERVERPORT);
        ftpClient.login(FTPUSER, FTPPASSWD);
        final InputStream is = ftpClient.retrieveFileStream(DOWNLOADFILE);
        Assert.assertNotNull(is);
        final File tempFile = saveInputStream(is, TEMPDIR + DOWNLOADFILE);
        final File localFile = new File(FILEDIR, DOWNLOADFILE);
        // Compare file sizes of downloaded and temporary file.
        Assert.assertEquals(tempFile.length(), localFile.length());

        // Delete local File
        localFile.delete();
    }

    /**
     * Test successful file transfer from local Filesystem to FTP Server.
     * 
     * @throws Exception Error communicating with FTP Server.
     */
    @Test
    public void testT08_SuccessfulUploadFTP() throws Exception {
        // Put resource file in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Remove file on ftp if exists.
        final FTPClient ftpClient = new FTPClient();
        ftpClient.connect(FTPSERVERIP,FTPSERVERPORT);

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setDataTimeout(10000);
        ftpClient.setConnectTimeout(10000);
        ftpClient.setDefaultTimeout(10000);

        ftpClient.login(FTPUSER, FTPPASSWD);
        InputStream is = ftpClient.retrieveFileStream(UPLOADFILE);
        if (is != null) {
            ftpClient.deleteFile(UPLOADFILE);
        }
        ftpClient.logout();
        ftpClient.disconnect();

        // Put file on FTP
        final Source source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE)
                .setOptions("noop=true");

        final Destination destination = new Destination(Protocol.FTP).setHost(FTPSERVERIP).setPort(String.valueOf(FTPSERVERPORT)).setPath("/");
        destination.setUsername(FTPUSER).setPassword(FTPPASSWD);

        FSConnector.copy(source, destination);

        // Get file back from ftp and compare with local file.
        ftpClient.connect(FTPSERVERIP,FTPSERVERPORT/*Constants.TESTSERVERIP*/);
        ftpClient.login(FTPUSER, FTPPASSWD);
        is = ftpClient.retrieveFileStream(UPLOADFILE);
        Assert.assertNotNull(is);
        final File tempFile = saveInputStream(is, TEMPDIR + UPLOADFILE);
        final File localFile = new File(FILEDIR, UPLOADFILE);

        Assert.assertEquals(tempFile.length(), localFile.length());
        ftpClient.deleteFile(UPLOADFILE);
        ftpClient.disconnect();

    }

    /**
     * Try to upload a not existing file.
     */
    @Test
    public void testT09_UploadNotExistingFileFTP() {
        final Source source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename("blaBlub")
                .setOptions("noop=true");

        final Destination destination = new Destination(Protocol.FTP).setHost(Constants.TESTSERVERIP).setPath("/");
        destination.setUsername(FTPUSER).setPassword(FTPPASSWD);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }

    /**
     * Try to connect to a not existing server / a wrong port.
     * 
     * Bug: Using a wrong port causes an endless loop.
     */
    @Test
    public void testT10_WrongFTPServer() {

        Source source = new Source(Protocol.FTP).setHost(Constants.TESTSERVERIP).setPort("8080").setPath("/")
                .setFilename(DOWNLOADFILE);
        source.setUsername(FTPUSER).setPassword(FTPPASSWD);

        Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        boolean excThrown = false;

        // The following lines would cause the test ending up in an endless loop.

        // try {
        // FSConnector.copy(source, destination);
        // } catch (FennecRuntimeException e) {
        // excThrown = true;
        // }
        // Assert.assertTrue(excThrown);

        // Put resource file to upload in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Put file on FTP
        source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        destination = new Destination(Protocol.FTP).setHost("localhost").setPath("/");
        destination.setUsername(FTPUSER).setPassword(FTPPASSWD);

        excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }
}
