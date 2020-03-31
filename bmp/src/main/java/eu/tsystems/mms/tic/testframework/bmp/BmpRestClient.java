/*
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
package eu.tsystems.mms.tic.testframework.bmp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public class BmpRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BmpRestClient.class);

    private URL upstreamProxy;

    private final URL baseUrl;
    private Integer proxyPort = null;

    /**
     * Create REST client for browser mob proxy
     *
     * @param host     {@link String} host
     * @param restPort {@link Integer} API port.
     *
     * @throws MalformedURLException
     * @deprecated Please use other constructors instead.
     */
    @Deprecated
    public BmpRestClient(final String host, final int restPort) throws MalformedURLException {

        this(new URL("http://" + host + ":" + restPort));
    }

    /**
     * Create REST client for browser mob proxy.
     *
     * @param apiUrl {@link URL} API endpoint.
     */
    public BmpRestClient(final URL apiUrl) {

        this(apiUrl, null);
    }

    /**
     * Create REST client for browser mob proxy.
     *
     * @param apiUrl           {@link URL} API endpoint.
     * @param upstreamProxyUrl {@link URL} Upstream proxy url.
     */
    public BmpRestClient(final URL apiUrl, final URL upstreamProxyUrl) {

        this.baseUrl = apiUrl;
        this.setUpstreamProxy(upstreamProxyUrl);
    }

    private URIBuilder url() {

        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(this.baseUrl.getProtocol());
        uriBuilder.setHost(this.baseUrl.getHost());
        uriBuilder.setPort(this.baseUrl.getPort());

        return uriBuilder;
    }

    /**
     * Sets the upstream proxy to route requests through
     *
     * @param url {@link URL}
     */
    public void setUpstreamProxy(final URL url) {

        this.upstreamProxy = url;
    }

    /**
     * Set header for all outgoing requests.
     *
     * @param key   {@link String}
     * @param value {@link String}
     *
     * @implNote can only be called after a BMP already started.
     */
    public void setHeader(final String key, final String value) {

        if (this.proxyPort == null) {
            throw new TesterraRuntimeException("No proxy started yet. Not possible to set a header.");
        }

        final JsonObject jsonHeaderMap = new JsonObject();
        jsonHeaderMap.add(key, new JsonPrimitive(value));

        try {
            final String urlToCall = url().setPath("/proxy/" + this.proxyPort + "/headers").toString();
            this.sendPost(urlToCall, jsonHeaderMap.toString());
        } catch (Exception e) {
            LOGGER.error("Error setting header.", e);
        }
    }

    /**
     * Add basic auth for domain.
     *
     * @param domain   {@link String}
     * @param username {@link String}
     * @param password {@link String}
     *
     * @implNote Can only be called after a BMP already started.
     */
    public void setBasicAuth(final String domain, final String username, final String password) {

        if (this.proxyPort == null) {
            throw new TesterraRuntimeException("No proxy started yet. Not possible to set auth credentials.");
        }

        LOGGER.info("Adding Basic Auth for " + username + ":*****@" + domain);

        final JsonObject auth = new JsonObject();
        auth.add("username", new JsonPrimitive(username));
        auth.add("password", new JsonPrimitive(password));

        try {
            final String urlToCall = url().setPath("/proxy/" + this.proxyPort + "/auth/basic/" + domain).toString();
            this.sendPost(urlToCall, auth.toString());
        } catch (Exception e) {
            LOGGER.error("Error setting basic auth", e);
        }
    }

    /**
     * Start the proxy server on the remote machine.
     *
     * @return portnumber of proxy server
     */
    public int startServer() {

        return this.startServer(null);
    }

    /**
     * Start the proxy server on the remote machine wiht a custom port.
     *
     * @param customPort
     *
     * @return portnumber of proxy server
     */
    public int startServer(final Integer customPort) {

        final URIBuilder startServerUriBuilder = url().setPath("/proxy").setParameter("trustAllServers", "true");

        // set upstream proxy.
        if (this.upstreamProxy != null) {
            startServerUriBuilder.setParameter("httpProxy", String.format("%s:%d", upstreamProxy.getHost(), upstreamProxy.getPort()));
        }

        // Cannot create a new proxy server on an used port,
        // so I need to check it before
        if (customPort != null) {

            if (this.isProxyRunningAtPort(customPort)) {

                this.proxyPort = customPort;
                final String proxyUrl = url().setPath("/proxy/" + proxyPort + "/").toString();

                LOGGER.info("BMP proxy port " + customPort + " already active.");
                LOGGER.info("Using proxy server at " + proxyUrl);
                return this.proxyPort;
            }

            startServerUriBuilder.setParameter("port", String.valueOf(customPort));
        }

        try {

            final String response = sendPost(startServerUriBuilder.toString(), "bindAddress=" + this.baseUrl.getHost());
            final JsonElement jsonElement = new JsonParser().parse(response);
            this.proxyPort = jsonElement.getAsJsonObject().get("port").getAsInt();

            final String proxyUrl = url().setPath("/proxy/" + proxyPort + "/").toString();
            LOGGER.info("Created new proxy server at " + proxyUrl);
            return this.proxyPort;

        } catch (Exception e) {
            throw new TesterraRuntimeException("Error starting browser mob proxy", e);
        }
    }

    /**
     * Stop the remote proxy
     */
    public void stopServer() {

        try {
            final String deleteProxyUrl = url().setPath("/proxy/" + this.proxyPort + "/").toString();
            sendDelete(deleteProxyUrl);

        } catch (Exception e) {
            throw new TesterraRuntimeException("Error stopping proxy", e);
        }
    }

    /**
     * Start capturing the network traffic
     *
     * @param headers capture header.
     * @param content capture contents.
     */
    public void startCapture(boolean headers, boolean content) {

        if (this.proxyPort == null) {
            throw new TesterraRuntimeException("No proxy started yet. Not possible to start capture.");
        }

        try {
            final String urlToCall = url()
                    .setPath("/proxy/" + this.proxyPort + "/har")
                    .setParameter("captureHeaders", String.valueOf(headers))
                    .setParameter("captureContent", String.valueOf(content))
                    .toString();

            this.sendPut(urlToCall, null);
        } catch (Exception e) {
            throw new RuntimeException("error starting capture", e);
        }
    }

    public void setHostMapping(Map<String, String> hostNameToIpMapping) {

        if (this.proxyPort == null) {
            throw new TesterraRuntimeException("No proxy started yet. Not possible to set host mapping.");
        }

        final JsonObject jso = new JsonObject();

        for (final String hostname : hostNameToIpMapping.keySet()) {
            final String ip = hostNameToIpMapping.get(hostname);
            jso.add(hostname, new JsonPrimitive(ip));
        }

        try {
            final String urlToCall = url().setPath("/proxy/" + this.proxyPort + "/hosts").toString();
            this.sendPost(urlToCall, jso.toString());
        } catch (Exception e) {
            LOGGER.error("Error setting host mapping", e);
        }
    }

    /**
     * Stop capturing
     *
     * @return Captured traffic in JsonFormat (serialized Har)
     */
    public JsonElement stopCapture() {

        try {
            final String urlToCall = url().setPath("/proxy/" + this.proxyPort + "/har").toString();
            return new JsonParser().parse(sendGet(urlToCall));
        } catch (Exception e) {
            throw new RuntimeException("error starting capture", e);
        }
    }

    /**
     * Checks if Port is already used
     *
     * @param portToCheck int
     *
     * @return bool if port is already used.
     */
    public boolean isProxyRunningAtPort(int portToCheck) {

        try {
            final String urlToCall = url().setPath("/proxy").toString();
            final String response = sendGet(urlToCall);
            final JsonElement jsonElement = new JsonParser().parse(response);
            final JsonArray portArray = jsonElement.getAsJsonObject().getAsJsonArray("proxyList");

            for (final JsonElement element : portArray) {

                final int actualPort = element.getAsJsonObject().get("port").getAsInt();
                if (actualPort == portToCheck) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing http get request to get proxy information.", e);
        }
        return false;
    }

    // HTTP POST request
    private String sendPost(final String url, String content) throws Exception {

        final HttpClient httpclient = HttpClients.createDefault();
        final HttpPost httppost = new HttpPost(url);

        if (content != null) {
            // Request parameters and other properties.
            StringEntity myEntity = new StringEntity(content, ContentType.create("text/plain", "UTF-8"));
            httppost.setEntity(myEntity);
        }

        LOGGER.info("Sending bmp command (post) " + url + " with " + content);
        return this.executeRequest(httpclient, httppost);
    }

    // HTTP PUT request
    private String sendPut(String url, String content) throws Exception {

        final HttpClient httpclient = HttpClients.createDefault();
        final HttpPut httpput = new HttpPut(url);

        if (content != null) {
            // Request parameters and other properties.
            final StringEntity myEntity = new StringEntity(content, ContentType.create("text/plain", "UTF-8"));
            httpput.setEntity(myEntity);
        }

        LOGGER.info("Sending bmp command (put) " + url + " with " + content);
        return this.executeRequest(httpclient, httpput);
    }

    // HTTP Get request
    private String sendGet(final String url) throws Exception {

        final HttpClient httpclient = HttpClients.createDefault();
        final HttpGet httpget = new HttpGet(url);

        //Execute and get the response.
        LOGGER.info("Sending bmp command (get): " + url);
        return this.executeRequest(httpclient, httpget);
    }

    // HTTP DELETE request
    private String sendDelete(final String url) throws Exception {

        final HttpClient httpclient = HttpClients.createDefault();
        final HttpDelete httpDelete = new HttpDelete(url);

        //Execute and get the response.
        LOGGER.info("Sending bmp command (delete): " + url);
        return this.executeRequest(httpclient, httpDelete);
    }

    private String executeRequest(final HttpClient httpclient, final HttpRequestBase httpRequest) throws IOException {

        final HttpResponse response = httpclient.execute(httpRequest);
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200 && statusCode != 204) {
            throw new WebApplicationException(response.getStatusLine().getStatusCode());
        }

        final HttpEntity entity = response.getEntity();
        final StringWriter writer = new StringWriter();

        if (entity != null) {
            try (final InputStream inputStream = entity.getContent()) {
                IOUtils.copy(inputStream, writer, Charset.defaultCharset());
            }
        }

        return writer.toString();
    }
}
