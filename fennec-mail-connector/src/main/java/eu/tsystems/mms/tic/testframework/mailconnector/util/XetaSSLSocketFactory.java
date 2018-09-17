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
 * Created on 27.06.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.mailconnector.util;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSL Socket Factory for fennec Mail Connector.
 * @author pele, mrgi
 */
public class fennecSSLSocketFactory extends SSLSocketFactory {
    
    /** The Logger. */
    public static final Logger LOGGER = LoggerFactory.getLogger(fennecSSLSocketFactory.class);
    
    /** The inner class SSLSocketFactory Object. */
    private SSLSocketFactory factory;
    
    /**
     * Creates a new SSL Factory Object. The Key Chains must set before.
     */
    public fennecSSLSocketFactory() {
        this.init(); 
    }
    
    /**
     * Called from constructor. Initializes the ImapMailConnector.
     */
    private void init() {
        try {
            LOGGER.info("Available security providers:");
            for (final Provider p : Security.getProviders()) {
                LOGGER.info(p.getName());
            }

            final KeyChain keyStoreKC = SSLConfig.getCurrentKeyStoreKeyChain();
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            if (keyStoreKC != null) {
                final String keyStorePath = keyStoreKC.getPath();
                final String keyStorePW = keyStoreKC.getPassWord();
                try {
                    keyStore.load(new FileInputStream(keyStorePath), keyStorePW.toCharArray());
                } catch (final FileNotFoundException fex) {
                    LOGGER.error("Incorrect Path or no keystore file: " + keyStorePath);
                }
                LOGGER.info("Using keystore " + keyStorePath);
                kmf.init(keyStore, keyStorePW.toCharArray());
            } else {
                LOGGER.info("No Keystore was set!");
                kmf.init(null, null);
            }
            
            final KeyChain trustStoreKC = SSLConfig.getCurrentTrustStoreKeyChain();
            final KeyStore trustStore = KeyStore.getInstance("JKS");
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            if (trustStoreKC != null) {
                final String trustStorePath = trustStoreKC.getPath();
                final String trustStorePW = trustStoreKC.getPassWord();
                try {
                    trustStore.load(new FileInputStream(trustStorePath), trustStorePW.toCharArray());
                } catch (final FileNotFoundException fex) {
                    LOGGER.error("Incorrect Path or no keystore file: " + trustStorePath);
                }
                LOGGER.info("Using truststore " + trustStorePath);
            } else {
                LOGGER.info("No Truststore was set!");
            }
            tmf.init(trustStore);
            
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
            factory = sslContext.getSocketFactory();
        } catch (final Exception ex) {
            LOGGER.error(ex.getMessage());
        }     
    }

    /**
     * Returns a new object of this class.
     * @return
     *      SocketFactory object.
     */
    public static SocketFactory getDefault() {
        return new fennecSSLSocketFactory();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */
    @Override
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) 
            throws IOException {
        return factory.createSocket(socket, host, port, autoClose);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.SSLSocketFactory#getDefaultCipherSuites()
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getDefaultCipherSuites();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.ssl.SSLSocketFactory#getSupportedCipherSuites()
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
     */
    @Override
    public Socket createSocket(final String arg0, final int arg1) throws IOException {
        return factory.createSocket(arg0, arg1);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
     */
    @Override
    public Socket createSocket(final InetAddress arg0, final int arg1) throws IOException {
        return factory.createSocket(arg0, arg1);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
     */
    @Override
    public Socket createSocket(final String arg0, final int arg1, final InetAddress arg2, final int arg3) 
            throws IOException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int, java.net.InetAddress, int)
     */
    @Override
    public Socket createSocket(final InetAddress arg0, final int arg1, final InetAddress arg2, final int arg3) 
            throws IOException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.net.SocketFactory#createSocket()
     */
    @Override
    public Socket createSocket() throws IOException {
        return factory.createSocket();
    }


}
