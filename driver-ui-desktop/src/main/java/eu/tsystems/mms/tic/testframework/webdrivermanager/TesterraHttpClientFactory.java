/*
 * Created on 21.11.2019
 *
 * Copyright(c) 1995 - 2007 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;


import com.google.common.base.Strings;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import org.openqa.selenium.remote.http.HttpClient;

import java.net.URL;
import java.time.Duration;

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
class TesterraHttpClientFactory implements HttpClient.Factory {

    private static long WDM_TIMEOUT_STUCK_SELENIUM_COMMANDS = PropertyManager.getLongProperty(TesterraProperties.WEBDRIVER_TIMEOUT_SECONDS_STUCK_COMMAND, 300);

    private Duration factoryConnectionTimeout = Duration.ofSeconds(120); // Kill, when connect does not succeed in this timeout
    private Duration factoryReadTimeout = Duration.ofSeconds(WDM_TIMEOUT_STUCK_SELENIUM_COMMANDS); // Kill hanging / stuck selenium commands after this timeout.
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