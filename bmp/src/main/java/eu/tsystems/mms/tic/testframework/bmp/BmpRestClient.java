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
package eu.tsystems.mms.tic.testframework.bmp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public class BmpRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BmpRestClient.class);

    private final String host;
    private final int restPort;
    public String url;
    private Integer proxyPort = null;
    private URL upstreamProxy;

    /**
     * Hide Default constructor.
     *
     * @param restPort
     */
    public BmpRestClient(String host, int restPort) {
        this.host = host;
        this.restPort = restPort;
        this.url = "http://" + host + ":" + restPort + "/";
    }

    /**
     * Sets the upstream proxy to route requests through
     */
    public void setUpstreamProxy(URL url) {
        upstreamProxy = url;
    }

    public void setHeader(String key, String value) {
        JSONObject auth = new JSONObject();
        auth.put(key, value);
        try {
            sendPost("headers", auth.toString());
        } catch (Exception e) {
            LOGGER.error("Error setting basic auth", e);
        }
    }

    public void setBasicAuth(String domain, String username, String password) {
        LOGGER.info("Adding Basic Auth for " + username + ":*****@" + domain);
        JSONObject auth = new JSONObject();
        auth.put("username", username);
        auth.put("password", password);
        try {
            sendPost("auth/basic/" + domain, auth.toString());
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
     * @return portnumber of proxy server
     */
    public int startServer(Integer customPort) {
        String path = "proxy?trustAllServers=true";

        if (upstreamProxy != null) {
            path += String.format("&httpProxy=%s:%d", upstreamProxy.getHost(), upstreamProxy.getPort());
        }

        // Cannot create a new proxy server on an used port,
        // so I need to check it before
        if (customPort != null) {
            boolean runsAtPort = checkIfProxyRunsAtPort(customPort);
            if (runsAtPort) {
                proxyPort = customPort;
                url = url + "proxy/" + proxyPort + "/";
                LOGGER.info("BMP proxy port " + customPort + " already active.");
                LOGGER.info("Using proxy server at " + url);
                return proxyPort;
            }
            path += String.format("&port=%s", customPort);
        }

        try {
            String response = sendPost(path, "bindAddress=" + host);
            JsonElement jsonElement = new JsonParser().parse(response);

            proxyPort = jsonElement.getAsJsonObject().get("port").getAsInt();

            if (proxyPort == null) {
                throw new TesterraRuntimeException("Error executing http request");
            }
            url = url + "proxy/" + proxyPort + "/";
            LOGGER.info("Created new proxy server at " + url);
            return proxyPort;
        } catch (Exception e) {
            throw new RuntimeException("error starting proxy", e);
        }
    }

    /**
     * Stop the remote proxy
     */
    public void stopServer() {
        try {
            sendDelete("");
        } catch (Exception e) {
            throw new RuntimeException("error stopping proxy", e);
        }
    }

    /**
     * Start capturing the network traffic
     *
     * @param headers capture header.
     * @param content capture contents.
     */
    public void startCapture(boolean headers, boolean content) {
        try {
            sendPut(String.format("har?captureHeaders=%s&captureContent=%s", headers, content), null);
        } catch (Exception e) {
            throw new RuntimeException("error starting capture", e);
        }
    }

    public void setHostMapping(Map<String, String> hostNameToIPMapping) {
        JSONObject jso = new JSONObject();
        for (String hostname : hostNameToIPMapping.keySet()) {
            String ip = hostNameToIPMapping.get(hostname);

            jso.put(hostname, ip);
        }
        try {
            sendPost("hosts", jso.toString());
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
            JsonElement jsonElement = new JsonParser().parse(sendGet("har"));
            return jsonElement;
        } catch (Exception e) {
            throw new RuntimeException("error starting capture", e);
        }
    }

    /**
     * Checks if Port is already used
     *
     * @param portToCheck
     * @return bool if port is already used.
     */
    public boolean checkIfProxyRunsAtPort(int portToCheck) {
        String path = "proxy";
        try {
            String response = sendGet(path);
            JsonElement jsonElement = new JsonParser().parse(response);

            JsonArray portArray = jsonElement.getAsJsonObject().getAsJsonArray("proxyList");
            for (JsonElement element : portArray) {
                int actualPort = element.getAsJsonObject().get("port").getAsInt();
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
    private String sendPost(String path, String content) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        String uri = url + path;
        LOGGER.info("Sending bmp command " + uri + " with " + content);
        HttpPost httppost = new HttpPost(uri);

        if (content != null) {
            // Request parameters and other properties.
            StringEntity myEntity = new StringEntity(content, ContentType.create("text/plain", "UTF-8"));
            httppost.setEntity(myEntity);
        }
        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            String reasonPhrase = response.getStatusLine().getReasonPhrase();
            throw new TesterraRuntimeException("Error posting to BMPServer: " + statusCode + " > " + reasonPhrase + " R: " + response);
        }
        HttpEntity entity = response.getEntity();

        StringWriter writer = new StringWriter();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                IOUtils.copy(instream, writer, Charset.defaultCharset());
            } finally {
                instream.close();
            }
        }
        return writer.toString();
    }

    // HTTP PUT request
    private String sendPut(String path, String content) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(url + path);

        if (content != null) {
            // Request parameters and other properties.
            StringEntity myEntity = new StringEntity(content,
                    ContentType.create("text/plain", "UTF-8"));
            httpput.setEntity(myEntity);
        }
        //Execute and get the response.
        HttpResponse response = httpclient.execute(httpput);
        int statusCode = response.getStatusLine().getStatusCode();
        // Take 204 into consideration as well - successful status, no content update
        if (statusCode != 204 && statusCode != 200) {
            throw new WebApplicationException(statusCode);
        }
        HttpEntity entity = response.getEntity();

        StringWriter writer = new StringWriter();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                IOUtils.copy(instream, writer, Charset.defaultCharset());
            } finally {
                instream.close();
            }
        }
        return writer.toString();
    }

    // HTTP Get request
    private String sendGet(String path) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url + path);

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new WebApplicationException(response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();

        StringWriter writer = new StringWriter();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                IOUtils.copy(instream, writer, Charset.defaultCharset());
            } finally {
                instream.close();
            }
        }
        return writer.toString();
    }

    // HTTP DELETE request
    private String sendDelete(String path) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url + path);

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httpDelete);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new WebApplicationException(response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();

        StringWriter writer = new StringWriter();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                IOUtils.copy(instream, writer, Charset.defaultCharset());
            } finally {
                instream.close();
            }
        }
        return writer.toString();
    }

}
