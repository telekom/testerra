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
 * Created on 07.04.2014
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import eu.tsystems.mms.tic.testframework.exceptions.NotYetImplementedException;
import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class SSHConnector {

    private String host;
    private String username;
    private String password;
    private File keyFile;
    private int port = 22;
    private int timeoutInMS = 20000;

    private Integer stdExit;

    private AuthMethod authMethod = AuthMethod.USERNAME_PASSWORD;

    /**
     * SSH Connection with PASSWORD auth.
     *
     * @param host .
     * @param username .
     * @param password .
     */
    public SSHConnector(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.authMethod = AuthMethod.USERNAME_PASSWORD;
    }

    /**
     * SSH Connection with AuthMethod.
     * The PEM Files need to have "-----BEGIN DSA PRIVATE KEY-----" or "-----BEGIN RSA PRIVATE KEY-----"
     * at the beginning! (set vie setKeyFile() or setKeyString())
     * @param host .
     * @param authMethod .
     */
    public SSHConnector(String host, AuthMethod authMethod) {
        this.host = host;
        this.authMethod = authMethod;
    }

    public enum AuthMethod {
        USERNAME_PASSWORD,
        KEY_FILE,
    }

    private enum Type {
        session,
        shell,
        exec,
        x11,
        sftp,
        subsystem
    }

    private String readIS(InputStream is, Channel channel) throws IOException {
        String out = "";
        byte[] tmp=new byte[1024];
        while (true) {
            while (is.available() > 0) {
                int i = is.read(tmp, 0, 1024);
                if (i < 0) break;
                out += new String(tmp, 0, i);
            }
            if (channel.isClosed()) {
                if (is.available() > 0) {
                    continue;
                }
                stdExit = channel.getExitStatus();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }

        return out;
    }

    public String execute(final String command) throws JSchException, IOException {
        final JSch jSch = new JSch();
        final String type = Type.exec.name();

        final Session session;
        final Channel channel;
        final InputStream is;

        switch (authMethod) {
            case USERNAME_PASSWORD:
                session = jSch.getSession(username, host, port);
                session.setPassword(password);
                break;
            case KEY_FILE:
                if (keyFile == null) {
                    throw new fennecSystemException("No keyfile specified. keyfile must be absolute file path to private key.");
                }
                jSch.addIdentity(keyFile.getAbsolutePath(), password);

                session = jSch.getSession(username, host, port);
                session.setPassword(password);
                break;
            default:
                throw new fennecSystemException("No valid auth method: " + authMethod);
        }

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(timeoutInMS);
        channel = session.openChannel(type);
        ((ChannelExec) channel).setCommand(command);

        is = channel.getInputStream();

        // execute
        channel.connect(timeoutInMS);

        String out = readIS(is, channel);
        channel.disconnect();
        session.disconnect();
        return out;
    }

    public void copyFile(String remoteFile, String localPath) throws IOException {
        throw new NotYetImplementedException();
    }

    public void storeFile(String localFile, String remoteTargetDirectory) throws IOException {
        throw new NotYetImplementedException();
    }

    public void storeFile(byte[] data, String remoteFileName, String remoteTargetDirectory) throws IOException {
        throw new NotYetImplementedException();
    }

    public Integer getStdExit() {
        return this.stdExit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setKeyFile(File keyFile) {
        this.keyFile = keyFile;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeoutInMS() {
        return timeoutInMS;
    }

    public void setTimeoutInMS(int timeoutInMS) {
        this.timeoutInMS = timeoutInMS;
    }
}
