/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public final class CertUtils {

    private static CertUtils instance;

    public static final String TRUSTED_HOSTS = "tt.cert.trusted.hosts";
    private String[] trustedHosts = new String[0];
    private boolean trustAllHosts = false;

    public CertUtils() {
        String trustHostsProperty = PropertyManager.getProperty(TRUSTED_HOSTS,"").trim();
        if (!trustHostsProperty.isEmpty()) {
            if (trustHostsProperty.equals("*")) {
                setTrustAllHosts(true);
            } else {
                setTrustedHosts(trustHostsProperty.split("\\s+"));
            }
        }
    }

    public static CertUtils getInstance() {
        if (instance == null) {
            instance = new CertUtils();
        }
        return instance;
    }

    /**
     * The Constant ALL_TRUSTING_TRUST_MANAGER.
     */
    private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER = new TrustManager[]{
            new X509ExtendedTrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            }
    };

    public HostnameVerifier getHostnameVerifier() {
        if (trustAllHosts) {
            return (hostname, sslSession) -> true;
        } else {
            return (hostname, sslSession) -> Arrays.asList(trustedHosts).contains(hostname);
        }
    }

    /**
     * Sets a new DefaultSSLSocketFactory which accepts all certs. ALso it sets a new DefaultHostnameVerifier which
     * accepts all host names.
     * @deprecated Use {@link #setDefault(CertUtils)} instead
     */
    public static void trustAllCerts() {
        CertUtils certUtils = getInstance();
        certUtils.setTrustAllHosts(true);
        certUtils.makeDefault();
    }

    /**
     * @deprecated Use {@link #makeDefault()} instead
     * @param certUtils
     */
    public void setDefault(CertUtils certUtils) {
        HttpsURLConnection.setDefaultSSLSocketFactory(certUtils.createTrustingSslSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(certUtils.getHostnameVerifier());
    }

    public void makeDefault() {
        setDefault(this);
    }

    /**
     * @deprecated
     * Overrides the SSL TrustManager and HostnameVerifier to allow all certs and hostnames. WARNING: This should only
     * be used for testing, or in a "safe" (i.e. firewalled) environment.
     *
     * @param connection the new accept all verifier
     * @return HttpsURLConnection
     */
    public static HttpsURLConnection trustAllCerts(HttpsURLConnection connection, SSLSocketFactory sslSocketFactory) {

        CertUtils certUtils = new CertUtils();

        // Create the socket factory.
        // Reusing the same socket factory allows sockets to be
        // reused, supporting persistent connections.
        if (null == sslSocketFactory) {
            sslSocketFactory = certUtils.createTrustingSslSocketFactory();
        }
        connection.setSSLSocketFactory(sslSocketFactory);
        certUtils.setTrustAllHosts(true);

        // Since we may be using a cert with a different name, we need to ignore
        // the hostname as well.
        connection.setHostnameVerifier(certUtils.getHostnameVerifier());

        return connection;
    }

    public SSLSocketFactory createTrustingSslSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException("Error trusting all certificates.", e);
        } catch (KeyManagementException e) {
            throw new SystemException("Error trusting all certificates.", e);
        }
    }

    public String[] getTrustedHosts() {
        return this.trustedHosts;
    }

    public boolean isTrustAllHosts() {
        return this.trustAllHosts;
    }

    public CertUtils setTrustedHosts(String[] hosts) {
        this.trustedHosts = hosts;
        return this;
    }

    public CertUtils setTrustAllHosts(boolean trustAll) {
        this.trustAllHosts = trustAll;
        return this;
    }

}
