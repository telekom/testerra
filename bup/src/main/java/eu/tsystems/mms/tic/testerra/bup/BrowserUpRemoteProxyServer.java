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

import java.net.URL;

/**
 * Date: 25.05.2020
 * Time: 11:58
 *
 * @author Eric Kubenka
 */
public class BrowserUpRemoteProxyServer {

    private Integer port;
    private URL upstreamProxy;

    private String upstreamNonProxy;

    private String bindAddress;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public URL getUpstreamProxy() {
        return upstreamProxy;
    }

    public void setUpstreamProxy(URL upstreamProxy) {
        this.upstreamProxy = upstreamProxy;
    }

    public String getUpstreamNonProxy() {
        return upstreamNonProxy;
    }

    public void setUpstreamNonProxy(String upstreamNonProxy) {
        this.upstreamNonProxy = upstreamNonProxy;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
}
