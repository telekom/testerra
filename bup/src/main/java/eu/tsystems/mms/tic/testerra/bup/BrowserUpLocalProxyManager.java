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
 *     Eric Kubenka
 */
package eu.tsystems.mms.tic.testerra.bup;

import com.browserup.bup.BrowserUpProxyServer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This little Local Proxy manager allows you to define a port range which can be used for BrowserUp proxies.
 * Date: 26.05.2020
 * Time: 11:22
 *
 * @author Eric Kubenka
 */
public class BrowserUpLocalProxyManager {

    private final ConcurrentHashMap<Integer, Boolean> portPool = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, BrowserUpProxyServer> registeredProxies = new ConcurrentHashMap<>();

    public BrowserUpLocalProxyManager(List<Integer> portPool) {
        portPool.forEach(p -> this.portPool.put(p, Boolean.FALSE));
    }

    /**
     * Starts a local {@link BrowserUpRemoteProxyServer} on a free port, that will be allocated in a threadsafe way.
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     * @return BrowserUpRemoteProxyServer
     * @throws BrowserUpNoFreePortException When no port is free..
     */
    public BrowserUpProxyServer startServer(BrowserUpProxyServer proxyServer) throws BrowserUpNoFreePortException {

        final int allocatedPort = allocateFreePort();
        if (allocatedPort == -1) {
            // NO FREE PORT BUT THERE SHOULD BE ONE FREE..
            throw new BrowserUpNoFreePortException("Could not allocated pool in port: " + portPool.toString());
        }

        proxyServer.start(allocatedPort);
        registeredProxies.put(allocatedPort, proxyServer);

        return proxyServer;
    }

    /**
     * Stop the given instance and free port
     *
     * @param proxyServer {@link BrowserUpRemoteProxyServer}
     */
    public void stopServer(BrowserUpProxyServer proxyServer) {

        int port = proxyServer.getPort();
        proxyServer.stop();

        this.registeredProxies.remove(port);
        this.portPool.put(port, Boolean.FALSE);
    }

    /**
     * Stops all proxies.
     */
    public void stopAllServer() {

        for (final Integer port : registeredProxies.keySet()) {
            registeredProxies.get(port).stop();
            portPool.put(port, Boolean.TRUE);
        }

        registeredProxies.clear();
    }

    private synchronized int allocateFreePort() throws BrowserUpNoFreePortException {

        if (!portPool.containsValue(Boolean.FALSE)) {
            throw new BrowserUpNoFreePortException("Could not allocated pool in port: " + portPool.toString());
        }

        for (Integer port : portPool.keySet()) {
            if (!portPool.get(port)) {
                portPool.put(port, Boolean.TRUE);
                return port;
            }
        }

        return -1;
    }

}
