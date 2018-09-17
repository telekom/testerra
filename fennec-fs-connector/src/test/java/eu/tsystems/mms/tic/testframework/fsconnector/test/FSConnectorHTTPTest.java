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

import eu.tsystems.mms.tic.testframework.exceptions.fennecRuntimeException;
import eu.tsystems.mms.tic.testframework.fsconnector.AbstractTest;
import eu.tsystems.mms.tic.testframework.fsconnector.FSConnector;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;
import org.apache.commons.net.ftp.FTPClient;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.InputStream;

/**
 * Test using the HTTP protocol with FSConnector.
 */
public class FSConnectorHTTPTest extends AbstractTest {

    /** File to download from http. */
    private static final String DOWNLOADFILE = "httptest.html";
    /** Path to directory where files are placed locally. */
    private static final String FILEDIR = System.getProperty("user.dir") + "/target/files/http/";

    /** Path to directory where temporary files are placed locally (e.g. for size comparations). */

    /**
     * Test successful file transfer from HTTP Server to local Filesystem.
     * 
     * @throws Exception Error handling Files.
     */
    @Test
    public void testT01_positiveTests() throws Exception {

        // Do copy File
        final Source source = new Source(Protocol.HTTP).setHost("http://" + "localhost").setPath(
                "/" + DOWNLOADFILE);

        final Destination destination = new Destination(Protocol.FILE).setPath(FILEDIR).setFilename(DOWNLOADFILE);

        FSConnector.copy(source, destination);

        Thread.sleep(2000);

        final File file = new File(FILEDIR + "/" + DOWNLOADFILE);
        Assert.assertTrue(file.exists());

        final FileReader reader = new FileReader(file);

        final BufferedReader br = new BufferedReader(reader);

        String text;
        final StringBuilder content = new StringBuilder();
        while ((text = br.readLine()) != null) {
            content.append(text);
        }
        br.close();
        Assert.assertTrue(content.toString().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"));

    }

    /**
     * Try to download a not existing file.
     */
    @Test
    public void testT02_negativeTests() {
        final Source source = new Source(Protocol.HTTP).setHost("http://" + "localhost").setPath(
                "/gibtsNicht.test");

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
     * Try to send files from HTTP to FTP or FTP to WebDav.
     * 
     * @throws Exception Error communicating with FTP Server.
     */
    @Test
    public void testT03_crossProtocolTests() throws Exception {
        Source source = new Source(Protocol.HTTP).setHost("http://" + "localhost").setPath(
                "/" + DOWNLOADFILE);
        Destination destination = new Destination(Protocol.FTP).setHost("localhost").setPath("/")
                .setFilename(DOWNLOADFILE);
        destination.setUsername(FSConnectorFTPTest.FTPUSER).setPassword(FSConnectorFTPTest.FTPPASSWD);

        FSConnector.copy(source, destination);

        final FTPClient ftpClient = new FTPClient();
        ftpClient.connect("localhost");
        ftpClient.login(FSConnectorFTPTest.FTPUSER, FSConnectorFTPTest.FTPPASSWD);
        final InputStream is = ftpClient.retrieveFileStream(DOWNLOADFILE);
        Assert.assertNotNull(is);
        ftpClient.deleteFile(DOWNLOADFILE);

        source = new Source(Protocol.FTP).setHost("localhost").setPath("/")
                .setFilename(FSConnectorFTPTest.DOWNLOADFILE);
        source.setUsername(FSConnectorFTPTest.FTPUSER).setPassword(FSConnectorFTPTest.FTPPASSWD);
        destination = new Destination(Protocol.WEBDAV).setHost("http://" + "localhost").setPath(
                "/webdav/");

        try {
            FSConnector.copy(source, destination);
        } catch (final fennecRuntimeException xre) {
            Assert.assertTrue(xre.getMessage().contains(
                    "WebDav can only be used together with the File protocol at the moment."));
        }
    }

}
