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
package eu.tsystems.mms.tic.testerra.bup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP API Client for https://github.com/browserup/browserup-proxy#rest-api
 * <p>
 * Date: 25.05.2020
 * Time: 10:12
 *
 * @author Eric Kubenka
 */
public class BrowserUpRemoteProxyManager implements Loggable {

    private final URL baseUrl;

    /**
     * Create REST client for browser mob proxy.
     *
     * @param apiUrl {@link URL} API endpoint.
     */
    public BrowserUpRemoteProxyManager(final URL apiUrl) {

        this.baseUrl = apiUrl;
    }

    /**
     * Calls GET /proxy
     *
     * @return List of ports already running an instance.
     */
    public List<Integer> getProxies() {

        final List<Integer> portsInUse = new ArrayList<>();

        // prepare url
        final URIBuilder getStartedProxyServersUriBuilder = url().setPath("/proxy");

        final URI uri = buildUri(getStartedProxyServersUriBuilder, "Error parsing URL for GET /proxy for BrowserUp proxy server.");
        final HttpGet httpGet = new HttpGet(uri);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpGet);

        // json string conversion to array.
        final JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        final JsonArray portArray = jsonElement.getAsJsonObject().getAsJsonArray("proxyList");

        // fill ports
        for (final JsonElement element : portArray) {
            final int actualPort = element.getAsJsonObject().get("port").getAsInt();
            portsInUse.add(actualPort);
        }

        return portsInUse;
    }

    /**
     * Calls POST /proxy with parameters
     *
     * @return BrowserUpRemoteProxyServer object.
     */
    public BrowserUpRemoteProxyServer startServer() {
        return startServer(new BrowserUpRemoteProxyServer());
    }

    /**
     * Calls POST /proxy with parameters and desired port, upstream proxy and other configurational data
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     * @return BrowserUpRemoteProxyServer
     */
    public BrowserUpRemoteProxyServer startServer(BrowserUpRemoteProxyServer proxyServer) {

        final URIBuilder startServerUriBuilder = url().setPath("/proxy");

        if (proxyServer.getPort() != null) {

            // check if port already in use...
            if (this.isRunning(proxyServer)) {
                log().info("Remote proxy session already running on this port.");
                return proxyServer;
            }

            // set port to start proxyserver on.
            startServerUriBuilder.setParameter("port", String.valueOf(proxyServer.getPort()));
        }

        // always trust them.
        startServerUriBuilder.setParameter("trustAllServers", "true");

        // always set bindAddress
        startServerUriBuilder.setParameter("bindAddress", this.baseUrl.getHost());

        // set upstream proxy.
        if (proxyServer.getUpstreamProxy() != null) {
            startServerUriBuilder.setParameter("httpProxy", String.format("%s:%d", proxyServer.getUpstreamProxy().getHost(), proxyServer.getUpstreamProxy().getPort()));
        }

        final URI uri = buildUri(startServerUriBuilder, "Error parsing URL for POST /proxy for BrowserUp proxy server.");
        final HttpPost httpPost = new HttpPost(uri);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpPost);

        final JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        final int port = jsonElement.getAsJsonObject().get("port").getAsInt();
        proxyServer.setPort(port);
        return proxyServer;
    }

    /**
     * Calls DELETE /proxy/[port] of proxy to stop.
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     * @return true, when successfully stopped.
     */
    public boolean stopServer(BrowserUpRemoteProxyServer proxyServer) {

        final URIBuilder deleteProxyServerUriBuilder = url().setPath("/proxy/" + proxyServer.getPort());

        final URI uri = buildUri(deleteProxyServerUriBuilder, "Error parsing URL for DELETE /proxy/[port] BrowserUp proxy server.");
        final HttpDelete httpDelete = new HttpDelete(uri);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpDelete);
        return jsonResponse != null && jsonResponse.equals("");
    }

    /**
     * Determines if {@link BrowserUpRemoteProxyServer} on port is already running
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     * @return true, when port in use.
     */
    public boolean isRunning(final BrowserUpRemoteProxyServer proxyServer) {

        final List<Integer> proxies = this.getProxies();
        return proxies.contains(proxyServer.getPort());
    }

    /**
     * Calls POST /proxy/[port]/auth/basic/[domain] with parameter
     *
     * @param proxyServer
     * @param domain
     * @param username
     * @param password
     */
    public boolean setBasicAuth(final BrowserUpRemoteProxyServer proxyServer, final String domain, final String username, final String password) {

        final URIBuilder basicAuthUriBuilder = url().setPath("/proxy/" + proxyServer.getPort() + "/auth/basic/" + domain);

        log().info("Adding Basic Auth for " + username + ":*****@" + domain);
        final JsonObject authObject = new JsonObject();
        authObject.add("username", new JsonPrimitive(username));
        authObject.add("password", new JsonPrimitive(password));

        final URI uri = buildUri(basicAuthUriBuilder, "Error parsing URL for POST /proxy/[port]/auth/basic/[domain] BrowserUp proxy server.");
        final HttpPost httpPost = new HttpPost(uri);

        final StringEntity postPayloadEntity = new StringEntity(authObject.toString(), ContentType.create("text/plain", "UTF-8"));
        httpPost.setEntity(postPayloadEntity);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpPost);

        return jsonResponse != null && jsonResponse.equals("");
    }


    /**
     * Calls POST /proxy/[port]/headers with parameter.
     * Sets header for all outgoing requests.
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     * @param key         {@link String}
     * @param value       {@link String}
     * @implNote can only be called after a BMP already started.
     */
    public boolean addHeader(final BrowserUpRemoteProxyServer proxyServer, final String key, final String value) {

        final URIBuilder setHeaderUriBuilder = url().setPath("/proxy/" + proxyServer.getPort() + "/headers");

        final JsonObject jsonHeaderMap = new JsonObject();
        jsonHeaderMap.add(key, new JsonPrimitive(value));

        final URI uri = buildUri(setHeaderUriBuilder, "Error parsing URL for POST /proxy/[port]/headers BrowserUp proxy server.");
        final HttpPost httpPost = new HttpPost(uri);

        final StringEntity postPayloadEntity = new StringEntity(jsonHeaderMap.toString(), ContentType.create("text/plain", "UTF-8"));
        httpPost.setEntity(postPayloadEntity);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpPost);

        return jsonResponse != null && jsonResponse.equals("");
    }

    /**
     * Calls PUT /proxy/[port]/har with parameters
     * Start capturing the network traffic
     *
     * @param proxyServer      {@link BrowserUpRemoteProxyServer} proxyserver to start capturing on
     * @param isCaptureHeaders Enables capture of headers.
     * @param isCaptureContent Enables capture of content
     * @param initialPageRef   Set page reference for first page, defaults to "Page 1"
     */
    public boolean startCapture(BrowserUpRemoteProxyServer proxyServer, String initialPageRef, boolean isCaptureHeaders, boolean isCaptureContent) {

        final URIBuilder startCaptureUriBuilder = url().setPath("/proxy/" + proxyServer.getPort() + "/har");

        if (isCaptureHeaders) {
            startCaptureUriBuilder.setParameter("captureHeaders", "true");
        }

        if (isCaptureContent) {
            startCaptureUriBuilder.setParameter("captureContent", "true");
        }

        if (StringUtils.isNotBlank(initialPageRef)) {
            startCaptureUriBuilder.setParameter("initialPageRef ", initialPageRef);
        }

        final URI uri = buildUri(startCaptureUriBuilder, "Error parsing URL for PUT /proxy/[port]/har BrowserUp proxy server.");
        final HttpPut httpPut = new HttpPut(uri);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpPut);
        return jsonResponse != null && jsonResponse.equals("");
    }

    /**
     * Calls GET /proxy/[port]/har
     * Stop capturing and returns captured hars.
     *
     * @return Captured traffic in JsonFormat (serialized Har)
     */
    public JsonElement stopCapture(BrowserUpRemoteProxyServer proxyServer) {

        final URIBuilder captureUriBuilder = url().setPath("/proxy/" + proxyServer.getPort() + "/har");

        final URI uri = buildUri(captureUriBuilder, "Error parsing URL for GET /proxy/[port]/har BrowserUp proxy server.");
        final HttpGet httpGet = new HttpGet(uri);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpGet);
        return JsonParser.parseString(jsonResponse);
    }

    /**
     * Calls PUT /proxy/[port]/har/pageRef with parameters.
     * Starts a new page in recording.
     * Please ensure you called {@link #startCapture(BrowserUpRemoteProxyServer, String, boolean, boolean)} before.
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     * @param pageRef     {@link String} pageRef
     */
    public boolean addNewPage(final BrowserUpRemoteProxyServer proxyServer, final String pageRef) {

        final URIBuilder pageRefUriBuilder = url().setPath("/proxy/" + proxyServer.getPort() + "/har/pageRef");

        if (StringUtils.isNotBlank(pageRef)) {
            pageRefUriBuilder.setParameter("pageRef", pageRef);
        }

        final URI uri = buildUri(pageRefUriBuilder, "Error parsing URL for PUT /proxy/[port]/har/pageRef BrowserUp proxy server.");
        final HttpPut httpPut = new HttpPut(uri);
        final String jsonResponse = sendRequestAndConvertResponseToString(httpPut);
        return jsonResponse != null && jsonResponse.equals("");
    }

    /**
     * Calls POST /proxy/[port]/hosts
     *
     * @param hostnameIpMap Map
     */
    public boolean setHostMapping(BrowserUpRemoteProxyServer proxyServer, Map<String, String> hostnameIpMap) {

        final URIBuilder hostUriBuilder = url().setPath("/proxy/" + proxyServer.getPort() + "/hosts");

        final JSONObject jsonHostnameIpMap = new JSONObject();
        for (final String hostname : hostnameIpMap.keySet()) {
            final String ip = hostnameIpMap.get(hostname);
            jsonHostnameIpMap.put(hostname, ip);
        }

        final URI uri = buildUri(hostUriBuilder, "Error parsing URL for POST /proxy/[port]/har BrowserUp proxy server.");
        final HttpPost httpPost = new HttpPost(uri);

        final StringEntity postPayloadEntity = new StringEntity(jsonHostnameIpMap.toString(), ContentType.create("text/plain", "UTF-8"));
        httpPost.setEntity(postPayloadEntity);

        final String jsonResponse = sendRequestAndConvertResponseToString(httpPost);
        return jsonResponse != null && jsonResponse.equals("");
    }

    private URI buildUri(URIBuilder uriBuilder, String errorMessage) {
        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new BrowserUpHttpApiException(errorMessage, e);
        }
    }

    private String sendRequestAndConvertResponseToString(HttpUriRequest httpRequest) {

        HttpResponse response;

        try {
            // response conversion
            final HttpClient httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpRequest);
        } catch (IOException e) {
            throw new BrowserUpHttpApiException(
                    String.format("Error executing %s for URL %s against HTTP API of BrowserUp proxy server.",
                            httpRequest.getClass().getSimpleName(),
                            httpRequest.getURI().toString()),
                    e);
        }

        if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 204) {
            throw new BrowserUpHttpApiException(
                    String.format("Error executing %s for URL %s against HTTP API of BrowserUp proxy server. Response code was: %s",
                            httpRequest.getClass().getSimpleName(),
                            httpRequest.getURI().toString(),
                            response.getStatusLine().getStatusCode()));
        }

        try {

            final HttpEntity entity = response.getEntity();
            final StringWriter writer = new StringWriter();

            if (entity != null) {
                try (final InputStream inputStream = entity.getContent()) {
                    IOUtils.copy(inputStream, writer, Charset.defaultCharset());
                }
            }

            // json string conversion to array.
            return writer.toString();
        } catch (IOException e) {
            throw new BrowserUpHttpApiException(
                    String.format("Error converting response %s for URL %s from BrowserUp proxy server.",
                            httpRequest.getClass().getSimpleName(),
                            httpRequest.getURI().toString()),
                    e);
        }
    }

    private URIBuilder url() {

        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(this.baseUrl.getProtocol());
        uriBuilder.setHost(this.baseUrl.getHost());
        uriBuilder.setPort(this.baseUrl.getPort());

        return uriBuilder;
    }
}
