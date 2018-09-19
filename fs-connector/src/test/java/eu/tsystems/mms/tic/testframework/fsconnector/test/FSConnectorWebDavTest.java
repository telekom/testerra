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

import eu.tsystems.mms.tic.testframework.fsconnector.AbstractTest;
import eu.tsystems.mms.tic.testframework.fsconnector.FSConnector;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;
import eu.tsystems.mms.tic.testframework.fsconnector.internal.WebDavConnection;
import eu.tsystems.mms.tic.testframework.test.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Tests using WebDav protocol with FSConnector.
 */
public class FSConnectorWebDavTest extends AbstractTest {

    /** File to upload to ftp. */
    private static final String UPLOADFILE = "testFileToUpload.txt";
    /** File to download from ftp. */
    private static final String DOWNLOADFILE = "testFileToDownload.txt";
    /** Path to directory where files are placed locally. */
    private static final String FILEDIR = System.getProperty("user.dir") + "/target/files/webdav/";
    /** Path to directory where temporary files are placed locally (e.g. for size comparations). */
    private static final String TEMPDIR = FILEDIR + "tmp/";
    /** FTP User name. */
    private static final String WEBDAVUSER = "WUM60239\\ta";
    /** FTP Password. */
    private static final String WEBDAVPASSWD = "your_passwd";
    /** HOST of webdav server. */
    private static final String WEBDAVHOST = "http://" + Constants.TESTSERVERIP;

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

    /**
     * Try to download a not existing file.
     */
    @Test
    public void testT01_DownloadNotExistingFileWebDav() {
        final Source source = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/")
                .setFilename("gibtsNicht.test");
        source.setUsername(WEBDAVUSER).setPassword(WEBDAVPASSWD);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);
        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }

    /**
     * Try anonymous login to server.
     *
     * @throws Exception .
     */
    @Test
    public void testT02_LoginAnonymousWebDav() throws Exception {
        final Source source = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/")
                .setFilename(DOWNLOADFILE);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        // Delete local File
        new File(FILEDIR, DOWNLOADFILE).delete();
    }

    /**
     * Try login with MMSDOM user and local webdav User.
     *
     * @throws Exception .
     */
    @Test
    public void testT03_LoginCorrectUserWebDav() throws Exception {
        final Source source = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/")
                .setFilename(DOWNLOADFILE);
        source.setUsername(WEBDAVUSER).setPassword(WEBDAVPASSWD);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        // Delete local File
        new File(FILEDIR, DOWNLOADFILE).delete();
    }

    /**
     * Test errorhandling with wrong password.
     */
    @Test
    public void testT04_LoginWrongPasswordWebDav() {
        Source source = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/").setFilename(DOWNLOADFILE);
        source.setUsername(WEBDAVUSER);

        Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        // Using no password, doesn't give an Exception, anonymous user is used instead.
        Assert.assertFalse(excThrown);

        // Put resource file to upload in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Put file on FTP
        source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        destination = new Destination(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/");
        destination.setUsername(WEBDAVUSER).setPassword("falsch");

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
    public void testT05_LoginWrongUserWebDav() {
        Source source = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/").setFilename(DOWNLOADFILE);
        source.setUsername("noUser").setPassword("egal");

        Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        // Using wrong user, doesn't give an Exception, anonymous user is used instead.
        Assert.assertFalse(excThrown);

        // Put resource file to upload in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Put file on FTP
        source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        destination = new Destination(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/");
        destination.setPassword("falsch");

        excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);

    }

    /**
     * Test successful file transfer from FTP Server to local Filesystem.
     *
     * @throws Exception .
     */
    @Test
    public void testT06_SuccessfulDownloadWebDav() throws Exception {
        final File localFile = new File(FILEDIR, DOWNLOADFILE);
        if (localFile.exists()) {
            localFile.delete();
        }

        // Do copy File
        final Source source = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/")
                .setFilename(DOWNLOADFILE);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR);

        FSConnector.copy(source, destination);

        // Compare file sizes of downloaded and temporary file.
        Assert.assertTrue(localFile.exists());

        // Delete local File
        localFile.delete();
    }

    /**
     * Test successful file transfer from local Filesystem to FTP Server.
     *
     * @throws Exception .
     */
    @Test(enabled = false)
    public void testT07_SuccessfulUploadWebDav() throws Exception {
        // Put resource file in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Remove file from webDav if exists
        WebDavConnection.removeFileFromWebDav(WEBDAVUSER, WEBDAVPASSWD, WEBDAVHOST + "/webdav/" + UPLOADFILE);

        // Put file on webDav
        final Source source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        final Destination destination = new Destination(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/");

        FSConnector.copy(source, destination);

        // Get file from webDav and compare.
        final Source s2 = new Source(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/").setFilename(UPLOADFILE);
        s2.setUsername(WEBDAVUSER).setPassword(WEBDAVPASSWD);
        final Destination d2 = new Destination(Protocol.FILE).setPath(TEMPDIR);
        FSConnector.copy(s2, d2);

        final File localFile = new File(FILEDIR, UPLOADFILE);
        final File tempFile = new File(TEMPDIR, UPLOADFILE);

        Assert.assertEquals(tempFile.length(), localFile.length());
        // Delete local Files
        tempFile.delete();
        localFile.delete();
    }

    /**
     * Try to upload a not existing file.
     */
    @Test
    public void testT08_UploadNotExistingFileWebDav() {
        final Source source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename("blaBlub")
                .setOptions("noop=true");

        final Destination destination = new Destination(Protocol.WEBDAV).setHost(WEBDAVHOST).setPath("/webdav/");
        destination.setUsername(WEBDAVUSER).setPassword(WEBDAVPASSWD);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }

    /**
     * Try to connect to a not existing server.
     */
    @Test
    public void testT09_WrongWebDavServer() {

        // Put resource file to upload in local filesystem.
        final InputStream origStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(UPLOADFILE);
        saveInputStream(origStream, FILEDIR + UPLOADFILE);

        // Put file on FTP
        final Source source = new Source(Protocol.FILE).setPath(FILEDIR).setFilename(UPLOADFILE);

        final Destination destination = new Destination(Protocol.WEBDAV).setHost("http://localhost").setPath("/");
        destination.setUsername(WEBDAVUSER).setPassword(WEBDAVPASSWD);

        boolean excThrown = false;
        try {
            FSConnector.copy(source, destination);
        } catch (final Exception e) {
            excThrown = true;
        }
        Assert.assertTrue(excThrown);
    }
}
