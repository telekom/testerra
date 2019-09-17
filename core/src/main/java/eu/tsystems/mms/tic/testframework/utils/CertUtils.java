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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by pele on 07.06.2016.
 */
public final class CertUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertUtils.class);

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

    /**
     * The Constant ALL_TRUSTING_HOSTNAME_VERIFIER.
     */
    private static final HostnameVerifier ALL_TRUSTING_HOSTNAME_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Sets a new DefaultSSLSocketFactory which accepts all certs. ALso it sets a new DefaultHostnameVerifier which
     * accepts all host names.
     */
    public static void trustAllCerts() {
        // Install the all-trusting trust manager
        SSLContext sc = null;
        String msg = "Unable to create socket factory";
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
        } catch (Exception e) {
            LOGGER.error(msg, e);
        }
        if (sc != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        // set default hostname verifier for https
        HttpsURLConnection.setDefaultHostnameVerifier(ALL_TRUSTING_HOSTNAME_VERIFIER);
    }

    /**
     * Overrides the SSL TrustManager and HostnameVerifier to allow all certs and hostnames. WARNING: This should only
     * be used for testing, or in a "safe" (i.e. firewalled) environment.
     *
     * @param connection the new accept all verifier
     * @return HttpsURLConnection
     */
    public static HttpsURLConnection trustAllCerts(HttpsURLConnection connection, SSLSocketFactory sslSocketFactory) {

        // Create the socket factory.
        // Reusing the same socket factory allows sockets to be
        // reused, supporting persistent connections.
        if (null == sslSocketFactory) {
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
                sslSocketFactory = sc.getSocketFactory();
            } catch (NoSuchAlgorithmException e) {
                throw new TesterraSystemException("Error trusting all certificates.", e);
            } catch (KeyManagementException e) {
                throw new TesterraSystemException("Error trusting all certificates.", e);
            }
        }

        connection.setSSLSocketFactory(sslSocketFactory);

        // Since we may be using a cert with a different name, we need to ignore
        // the hostname as well.
        connection.setHostnameVerifier(ALL_TRUSTING_HOSTNAME_VERIFIER);

        return connection;
    }

}
