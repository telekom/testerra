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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pele on 17.04.2015.
 */
public class RESTUtils {

    private static final int DEFAULT_TIMEOUT = 5 * 60 * 1000;

    /**
     * REST POST with parameters.
     *
     * @param url the complete url.
     *
     * @return response cast to string.
     */
    public static String requestPOST(final String url) {
        return requestPOST(url, null);
    }

    /**
     * REST POST with parameters.
     *
     * @param url        the complete url.
     * @param parameters the parameters or null.
     *
     * @return response cast to string.
     */
    public static String requestPOST(final String url, final String parameters) {
        return requestPOST(url, parameters, MediaType.TEXT_PLAIN_TYPE, String.class);
    }

    /**
     * REST POST with parameters
     *
     * @param url                {@link String}
     * @param parameters         {@link String}
     * @param mediaType          {@link MediaType} ContentType
     * @param responseObjectType response
     * @param <T>                response object
     *
     * @return Response casted to given object
     */
    public static <T> T requestPOST(final String url, final String parameters, final MediaType mediaType, Class<T> responseObjectType) {
        return requestPOST(url, parameters, mediaType, DEFAULT_TIMEOUT, responseObjectType);
    }

    /**
     * REST POST with parameters
     *
     * @param url                {@link String}
     * @param parameters         {@link String}
     * @param mediaType          {@link MediaType} ContentType
     * @param responseObjectType response
     * @param <T>                response object
     *
     * @return Response casted to given object
     */
    public static <T> T requestPOST(final String url, final String parameters, final MediaType mediaType, final int timeout, Class<T> responseObjectType) {
        WebResource resource = getClient(url, timeout).resource(url);

        if (parameters != null) {
            return resource.type(mediaType).post(responseObjectType, parameters);
        } else {
            return resource.type(mediaType).post(responseObjectType);
        }
    }

    /**
     * REST GET
     *
     * @param url the complete url.
     *
     * @return response cast to string.
     */
    public static String requestGET(final String url) {
        return requestGET(url, String.class);
    }

    /**
     * REST GET
     *
     * @param url                {@link String}
     * @param responseObjectType Response Object Class
     * @param <T>                T
     *
     * @return Response casted to given class
     */
    public static <T> T requestGET(final String url, Class<T> responseObjectType) {
        return requestGET(url, DEFAULT_TIMEOUT, responseObjectType);
    }

    public static <T> T requestGET(final String url, final int timeout, Class<T> responseObjectType) {
        Client client = getClient(url, timeout);
        WebResource resource = client.resource(url);
        return resource.get(responseObjectType);
    }

    private static Client getClient(final String url, final int timeout) {
        Client client = Client.create();
        client.setConnectTimeout(timeout);
        client.setReadTimeout(timeout);

        /*
        check for basic auth
         */
        try {
            URL u = new URL(url);
            String authority = u.getAuthority();

            if (authority != null && authority.contains("@")) {
                authority = authority.split("@")[0];

                if (authority.contains(":")) {
                    String[] split = authority.split(":");
                    String username = split[0];
                    String password = split[1];
                    client.addFilter(new HTTPBasicAuthFilter(username, password));
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Not a valid url", e);
        }

        return client;
    }

    public static void requestDELETE(final String url) {
        Client client = getClient(url, DEFAULT_TIMEOUT);
        WebResource resource = client.resource(url);
        resource.delete();
    }

}
