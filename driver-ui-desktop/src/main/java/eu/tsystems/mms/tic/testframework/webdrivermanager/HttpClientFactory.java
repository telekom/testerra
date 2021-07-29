
package eu.tsystems.mms.tic.testframework.webdrivermanager;

/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import com.google.common.base.Strings;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import java.net.URL;
import java.time.Duration;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import org.openqa.selenium.remote.http.HttpClient;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * This Client Factory allows us to reduces timeouts of 3 HOURS to our custom timeouts.
 * This will help, to avoid blocking selenium commands.
 * <p>
 * Date: 21.11.2019
 * Time: 09:43
 *
 * @author Eric Kubenka
 */
class HttpClientFactory implements HttpClient.Factory {
    private final Duration factoryConnectionTimeout = Duration.ofSeconds(120); // Kill, when connect does not succeed in this timeout
    private final Duration factoryReadTimeout = factoryConnectionTimeout; // Kill hanging / stuck selenium commands after this timeout.
    private final ConnectionPool pool = new ConnectionPool();

    @Override
    public HttpClient.Builder builder() {

        // this code is copied from OkHttpClient.Factory .. and modified in timeout configuration.
        return new HttpClient.Builder() {

            @Override
            public HttpClient createClient(URL url) {

                connectionTimeout = factoryConnectionTimeout;
                readTimeout = factoryReadTimeout;

                okhttp3.OkHttpClient.Builder client = new okhttp3.OkHttpClient.Builder()
                        .connectionPool(pool)
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .proxy(proxy)
                        .readTimeout(readTimeout.toMillis(), MILLISECONDS)
                        .connectTimeout(connectionTimeout.toMillis(), MILLISECONDS);

                String info = url.getUserInfo();
                if (!Strings.isNullOrEmpty(info)) {
                    String[] parts = info.split(":", 2);
                    String user = parts[0];
                    String pass = parts.length > 1 ? parts[1] : null;

                    String credentials = Credentials.basic(user, pass);

                    client.authenticator((route, response) -> {
                        if (response.request().header("Authorization") != null) {
                            return null; // Give up, we've already attempted to authenticate.
                        }

                        return response.request().newBuilder()
                                .header("Authorization", credentials)
                                .build();
                    });
                }

                client.addNetworkInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    return response.code() == 408
                            ? response.newBuilder().code(500).message("Server-Side Timeout").build()
                            : response;
                });

                return new org.openqa.selenium.remote.internal.OkHttpClient(client.build(), url);
            }
        };
    }

    @Override
    public HttpClient createClient(URL url) {

        return builder().createClient(url);
    }

    @Override
    public void cleanupIdleClients() {

        pool.evictAll();
    }
}
