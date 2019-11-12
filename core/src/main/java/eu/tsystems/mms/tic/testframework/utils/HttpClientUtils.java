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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by pele on 13.03.2015.
 */
public final class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    public abstract static class ResponseCallBack {
        public abstract String run(HttpResponse response);
    }

    public static String request(String requestUrl, ResponseCallBack callback) throws IOException {
        return request(requestUrl, true, null, 10000, callback, false);
    }

    public static String request(String requestUrl, boolean proxyAutoMode, HttpHost proxy, int connectionTimeoutMillis,
                                 ResponseCallBack responseCallBack, boolean suppressLogging) throws IOException {
            return pRequest(requestUrl, proxyAutoMode, proxy, connectionTimeoutMillis, responseCallBack, suppressLogging);
    }

    private static String pRequest(String requestUrl, boolean proxyAutoMode, HttpHost proxy, int connectionTimeoutMillis,
                                 ResponseCallBack responseCallBack, boolean suppressLogging) throws IOException {
        if (proxyAutoMode) {
            String proxyHost = PropertyManager.getProperty("http.proxyHost");
            int proxyPort = PropertyManager.getIntProperty("http.proxyPort", -1);
            if (proxyHost != null && proxyPort != -1) {
                proxy = new HttpHost(proxyHost, proxyPort, "http");
            }
        }

         /*
         * Build httpClient.
         */
        if (!suppressLogging) {
            LOGGER.info("Http request with proxy: " + proxy);
        }
        RequestConfig.Builder rcBuilder = RequestConfig.custom();
        rcBuilder.setProxy(proxy);
        rcBuilder.setConnectTimeout(connectionTimeoutMillis);
        rcBuilder.setSocketTimeout(connectionTimeoutMillis);
        RequestConfig requestConfig = rcBuilder.build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

        try {
            final HttpGet request = new HttpGet(requestUrl);
            final HttpResponse response = httpClient.execute(request);
            return responseCallBack.run(response);
        }
        finally {
            httpClient.close();
        }
    }
}
