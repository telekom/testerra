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
 * Created on 27.08.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.fsconnector.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;
import org.testng.Assert;

/**
 * Class providing simple interface for transferring files between local filesystem and WebDav.
 * 
 * @author sepr
 */
public final class WebDavConnection {

    /** Sardine object handling file transfer. */
    private static Sardine sardine;
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDavConnection.class);

    /** String literal, that appears often in this class. */
    private static final String PATH = "path";

    /** Hide constructor. */
    private WebDavConnection() {
    }

    /**
     * Helper function initializing the connection.
     * 
     * @param username Username for WebDav login.
     * @param password Password for WebDav login.
     */
    private static void initConnection(final String username, final String password) {
        if (username == null) {
            LOGGER.info("No credentials given for WebDav. Trying login without username/password.");
            sardine = SardineFactory.begin();
        } else {
            LOGGER.info("Credentials given for WebDav. Trying login with username and password.");
            sardine = SardineFactory.begin(username, password);
        }
    }

    /**
     * Download a WebDav resource to local file system.
     * 
     * @param source FSLoaction representing WebDav file.
     * @param destination FSLocation representing local File.
     */
    public static void download(final Source source, final Destination destination) {
        pDownload(source, destination);
    }
    
    /**
     * Download a WebDav resource to local file system.
     * 
     * @param source FSLoaction representing WebDav file.
     * @param destination FSLocation representing local File.
     */
    private static void pDownload(final Source source, final Destination destination) {
        Assert.assertEquals(source.getProtocol(), Protocol.WEBDAV);
        Assert.assertEquals(destination.getProtocol(), Protocol.FILE);
        initConnection(source.getUsername(), source.getPassword());
        final File destFolder = new File(destination.getPath());
        destFolder.mkdirs();
        final File destFile = new File(destFolder, source.getFilename());

        InputStream inputStream;
        OutputStream out;
        try {
            inputStream = sardine.get(buildWebDavUrl(source) + source.getFilename());
            out = new FileOutputStream(destFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (final FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new FennecRuntimeException("Error saving to file: " + destFile.getName() + ". " + e.getMessage(), e);
        } catch (final IOException e) {
            LOGGER.error(e.getMessage());
            throw new FennecRuntimeException("Error getting file: " + source.getFilename() + ". " + e.getMessage(), e);
        }
    }

    /**
     * Upload a local file to WebDav.
     * 
     * @param source FSLoaction representing local file.
     * @param destination FSLocation representing WebDav.
     */
    public static void upload(final Source source, final Destination destination) {
        pUpload(source, destination);
    }
    
    /**
     * Upload a local file to WebDav.
     * 
     * @param source FSLoaction representing local file.
     * @param destination FSLocation representing WebDav.
     */
    private static void pUpload(final Source source, final Destination destination) {
        Assert.assertEquals(destination.getProtocol(), Protocol.WEBDAV);
        Assert.assertEquals(source.getProtocol(), Protocol.FILE);

        final File sourceFile = new File(source.getPath(), source.getFilename());
        if (!sourceFile.exists()) {
            throw new FennecRuntimeException("File to upload could not be found: " + sourceFile.getAbsolutePath());
        }

        initConnection(destination.getUsername(), destination.getPassword());
        try {
            sardine.delete(buildWebDavUrl(destination) + source.getFilename());
        } catch (final IOException e) {
            LOGGER.trace("No file on WebDav nothing to delete.");
        }

        final String destUrl = buildWebDavUrl(destination);
        try {
            final FileInputStream fis = new FileInputStream(sourceFile);
            LOGGER.info("Uploading: " + source.getFilename());
            sardine.exists(destUrl + source.getFilename());
            sardine.put(destUrl + source.getFilename(), fis);
        } catch (final IOException e) {
            LOGGER.trace(e.toString());
            throw new FennecRuntimeException("Error uploading File " + source.getFilename()
                    + " to webdav location " + destUrl + ". "
                    + e.getMessage(), e);
        }
    }

    /**
     * Delete a file on webDav.
     * 
     * @param user Login.
     * @param password Login.
     * @param url Url to file to delete.
     */
    public static void removeFileFromWebDav(final String user, final String password, final String url) {
        pRemoveFileFromWebDav(user, password, url);
    }
    
    /**
     * Delete a file on webDav.
     * 
     * @param user Login.
     * @param password Login.
     * @param url Url to file to delete.
     */
    private static void pRemoveFileFromWebDav(final String user, final String password, final String url) {
        initConnection(user, password);
        try {
            sardine.delete(url);
        } catch (final IOException e) {
            LOGGER.trace("No file on WebDav nothing to delete.");
            // existiert nicht
        }
    }

    /**
     * Build the webdav url out of host and path of FennecFSLocation.
     * 
     * @param location FSLocation holding path and host.
     * @return WebDav Url.
     */
    private static String buildWebDavUrl(final AbstractFileSystemLocation<?> location) {
        if (location.getPort() != null) {
            LOGGER.warn("Connections to webdav are only possible via the standard ports 80 and 443. "
                    + "Contact the fennec team for support.");
        }
        // Sardine homepage has a workaround for that.
        if (!location.getHost().endsWith("/") && !location.getPath().startsWith("/")) {
            location.setHost(location.getHost() + "/");
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(location.getHost() + location.getPath());
        if (!builder.toString().endsWith("/")) {
            builder.append("/");
        }
        return builder.toString();
    }
}
