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
 * Created on 07.05.2014
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.fsconnector.ftps.playground;

import eu.tsystems.mms.tic.testframework.fsconnector.AbstractTest;
import eu.tsystems.mms.tic.testframework.fsconnector.FSConnector;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class FTPSTest extends AbstractTest {

    /**
     * T01FTPSConnectionTest
     * <p/>
     * Description: T01FTPSConnectionTest
     *
     * @throws Exception .
     */
    @Test
    public void testT01_FTP_ConnectionTest() throws Exception {
        Source source = new Source(Protocol.FTP);

        source.setHost("localhost");
        source.setUsername("test");
        source.setUsername("test");
        source.setPassword("test");
        source.setFilename("sendMessage.txt");

        Destination destination = new Destination(Protocol.FILE);
        destination.setPath("f:\\test");

        FSConnector.copy(source, destination);
    }

    /**
     * T02FTPSConnectionTest
     * @throws Exception .
     */
    @Test
    public void testT02_FTPS_ConnectionTest() throws Exception {
        Source source = new Source(Protocol.FTPS);

        source.setHost("localhost");
        source.setUsername("test");
        source.setUsername("test");
        source.setPassword("test");
        source.setFilename("sendMessage.txt");
        source.setKeyStoreFile("f:\\CrushFTP7_PC\\certs\\www_domain_com.jks");
        source.setKeyStorePassword("test");
        source.setKeyPassword("test");

        Destination destination = new Destination(Protocol.FILE);
        destination.setPath("f:\\test");

        FSConnector.copy(source, destination);
    }

    /**
     * T03SFTPConnectionTest
     *
     * @throws Exception .
     */
    @Test
    public void testT03_SFTP_ConnectionTest_Download() throws Exception {
        Source source = new Source(Protocol.SFTP);
        source.setHost("localhost");
        source.setPort("2222");
        source.setUsername("test");
        source.setPassword("test");
        source.setFilename("sendMessage.txt");
        source.setPrivateKeyFile("f:\\cygwin\\home\\pele\\.ssh\\id_rsa");
        source.setPrivateKeyPassphrase("testtest");

        Destination destination = new Destination(Protocol.FILE);
        destination.setPath("f:\\test");

        FSConnector.copy(source, destination);
    }

    /**
     * T04SFTPConnectionTest
     *
     * @throws Exception .
     */
    @Test
    public void testT04_SFTP_ConnectionTest_Upload() throws Exception {
        Source source = new Source(Protocol.FILE);
        source.setPath("f:\\test");
        source.setFilename("sendMessage.txt");

        Destination destination = new Destination(Protocol.SFTP);
        destination.setHost("localhost");
        destination.setPort("2222");
        destination.setUsername("test");
        destination.setPassword("test");
        destination.setPath("transfer");
        destination.setFilename("sendMessage2.txt");
        destination.setPrivateKeyFile("f:\\cygwin\\home\\pele\\.ssh\\id_rsa");
        destination.setPrivateKeyPassphrase("testtest");

        FSConnector.copy(source, destination);
    }
}
