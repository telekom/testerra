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
 * Created on 01.08.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.fsconnector.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Protocol;
import eu.tsystems.mms.tic.testframework.fsconnector.endpoint.Source;

/**
 * Abstract class defining the properties of a location that is used as source or destination of a camel routing. Each
 * Location must have a Protocol which defines other required attributes.
 * 
 * @param <T> Generic value that states if instances are Source or Destination.
 * 
 * @author pele
 */
public abstract class AbstractFileSystemLocation<T> {

    /** Protocol of the Location. */
    private Protocol protocol;

    /** Host of location (used in urls). */
    private String host;
    /** Port of location (used together with host). */
    private String port;
    /** Path to the location (e.g. in filesystem or on a server - appended to host). */
    private String path;
    /** Name of a file to get or store. */
    private String filename;
    /** Username used for authentication. */
    private String username;
    /** Password used for authentication. */
    private String password;

    /** File containing the Keystore for SSL connections or the private key for SSH connections. */
    private String keyStoreFile;
    /** Keystore setting for secure connections. */
    private String keyStorePassword;
    /** Password of key for secure connections. */
    private String keyPassword;

    private Integer timeoutInMS = null;

    private String privateKeyFile;
    private String privateKeyPassphrase;

    /** Arbitary options that can be appended to camel urls. */
    private String options;

    /** String literal, to clear PMD warning. */
    private static final String UNCHECKED = "unchecked";

    private List<String> additionalParameters = new ArrayList<String>();

    /**
     * Constructor to use by subclasses.
     * 
     * @param protocol Protocol of this location.
     */
    protected AbstractFileSystemLocation(final Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Look up a Field in class hierarchy.
     * 
     * @param clazz The class to find a field in.
     * @param fieldName The name of the field to find.
     * @return reflective Field object
     * @throws NoSuchFieldException thrown if clazz has no field with the specific name.
     */
    private Field getField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException e) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    /**
     * Method that builds the camel url for this location according to its properties.
     * 
     * @return Camel Url
     */
    public String getCamelUrl() {
        return this.pGetCamelUrl();
    }

    /**
     * Method that builds the camel url for this location according to its properties.
     * 
     * @return Camel Url
     */
    private String pGetCamelUrl() {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        final StringBuilder builder = new StringBuilder();

        /*
        Set URL
         */
        builder.append(getUrlPart());

        /*
        Create complete url
         */
        switch (protocol) {
        /*
         * FTP group
         */
        case SFTP:
            builder.append(getFTPGetParameters());

            if (timeoutInMS != null) {
                builder.append("&connectTimeout=").append(timeoutInMS);
            }

            if (username != null) {
                builder.append("&username=RAW(").append(username).append(")");
            }

            if (privateKeyFile != null) {
                builder.append("&privateKeyFile=").append(privateKeyFile);
            }
            if (privateKeyPassphrase != null) {
                builder.append("&privateKeyPassphrase=RAW(").append(privateKeyPassphrase).append(")");
                builder.append("&privateKeyFilePassphrase=RAW(").append(privateKeyPassphrase).append(")");
            }

            break;
        case FTPS:
            builder.append(getFTPGetParameters());
            builder.append(getFTPClientTruststoreGetParamaters(keyStoreFile, keyStorePassword, keyPassword));

            if (timeoutInMS != null) {
                builder.append("&soTimeout=").append(timeoutInMS);
            }

            break;
        case FTP:
            builder.append(getFTPGetParameters());

            if (timeoutInMS != null) {
                builder.append("&soTimeout=").append(timeoutInMS);
            }

            break;

        /*
         * local FILEsystem
         */
        case FILE:
            if (path == null) {
                throw new FennecSystemException("Path must not be empty. Please set a local file system path.");
            }
            if (this instanceof Source) {
                if (filename == null) {
                    throw new FennecSystemException("Filename must not be empty. Please set a local filename.");
                }
                if (!(new File(getPath(), filename).exists())) {
                    throw new FennecRuntimeException(String.format("The file to upload doesn't extist: %s/%s",
                            getPath(), filename));
                }
                logger.info(getProtocol().name() + " : FileFilter = " + filename);
            }

            if (this instanceof Destination) {
                builder.append("?autoCreate=true");
            }
            else if (this instanceof Source) {
                builder.append("?noop=true");
            }

            break;

        /*
         * Get HTTP source
         */
        case HTTP:
            if (options != null) {
                builder.append(options);
            }
            break;

        default:
            // We should never get here.
            throw new RuntimeException("Unknown protocol");
        }

        if (password != null) {
            builder.append("&password=");
            builder.append(password);
        }

        for (String additionalParameter : additionalParameters) {
            builder.append("&");
            builder.append(additionalParameter);
        }

        logger.info(getProtocol().name() + " : " + builder.toString());
        return builder.toString();
    }

    private String getUrlPart() {
        if (protocol == null) {
            throw new FennecSystemException("No protocol set.");
        }

        if (protocol != Protocol.FILE) {
            if (host == null) {
                throw new FennecSystemException("No host set. Please set a host!");
            }
        }

        StringBuilder urlPart = new StringBuilder();

        urlPart.append(protocol.name().toLowerCase());

        urlPart.append("://");

        if (username != null) {
            urlPart.append(username).append("@");
        }

        if (host != null) {
            urlPart.append(host);
        }
        if (port != null) {
            urlPart.append(":").append(port);
        }

        if (path != null) {
            if (!path.startsWith("/")) {
                urlPart.append("/");
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            urlPart.append(path);
        }

        /*
        !!!Do not add the file into the path on type FILE and Source. This is
        managed by a file filter!!!
         */

        return urlPart.toString();
    }

    /* Getter */

    /**
     * Getter.
     * 
     * @return Filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Getter.
     * 
     * @return Password.
     */
    public String getKeyPassword() {
        return keyPassword;
    }

    /**
     * Getter.
     * 
     * @return KeyStorefile.
     */
    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    /**
     * Getter.
     * 
     * @return KeyStorePassword.
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    /* Setter */
    /**
     * Setter for this field.
     * 
     * @param filenameToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setFilename(final String filenameToSet) {
        this.filename = filenameToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param hostToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setHost(final String hostToSet) {
        this.host = hostToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param keyPasswordToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setKeyPassword(final String keyPasswordToSet) {
        this.keyPassword = keyPasswordToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param keyStoreFileToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setKeyStoreFile(final String keyStoreFileToSet) {
        this.keyStoreFile = keyStoreFileToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param keyStorePasswordToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setKeyStorePassword(final String keyStorePasswordToSet) {
        this.keyStorePassword = keyStorePasswordToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param optionsToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setOptions(final String optionsToSet) {
        this.options = optionsToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param passwordToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setPassword(final String passwordToSet) {
        this.password = passwordToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param pathToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setPath(final String pathToSet) {
        this.path = pathToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param portToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setPort(final String portToSet) {
        this.port = portToSet;
        return (T) this;
    }

    /**
     * Setter for this field.
     * 
     * @param usernameToSet Value.
     * @return Location.
     */
    @SuppressWarnings(UNCHECKED)
    public T setUsername(final String usernameToSet) {
        this.username = usernameToSet;
        return (T) this;
    }

    /**
     * gets the protocol
     *
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * sets the protocol
     *
     * @param protocol the protocol to set
     */
    public void setProtocol(final Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * gets the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * gets the password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * gets the path
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * gets the port
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * gets the host
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    public Integer getTimeoutInMS() {
        return timeoutInMS;
    }

    public void setTimeoutInMS(Integer timeoutInMS) {
        this.timeoutInMS = timeoutInMS;
    }

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public void setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    public String getPrivateKeyPassphrase() {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }

    public String getOptions() {
        return options;
    }

    /**
     * Class containing methods to build a valid FTP camel url.
     * 
     * @author pele
     * 
     */
    private String getFTPClientTruststoreGetParamaters(final String keyStoreFl, final String keyStorePwd,
                                                         final String keyPwd) {
            return "&ftpClient.keyStore.file=" + keyStoreFl + "&ftpClient.keyStore.password="
                    + keyStorePwd
                    + "&ftpClient.keyStore.keyPassword=" + keyPwd;
        }

    /**
     * Build a ftp url.
     *
     * @return FTP camel url.
     */
    private String getFTPGetParameters() {
        String out = "?" + "throwExceptionOnConnectFailed=true&pollStrategy=#FennecFSSinglePoller&disconnect=true";
        if (filename != null) {
            out += "&fileName=" + filename;
        }
        if (protocol == Protocol.FTPS) {
            out += "&ftpClient.dataTimeout=10000";
        }
        return out;
    }

    public void addAdditionalParameter(String param) {
        additionalParameters.add(param);
    }
}
