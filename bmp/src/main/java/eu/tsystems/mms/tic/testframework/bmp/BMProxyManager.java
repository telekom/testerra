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

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pele on 30.04.2015.
 */
public final class BMProxyManager {

    private static List<Integer> portPool = new ArrayList<Integer>(1);
    private static Map<Integer, Boolean> usedPortPool = new HashMap<Integer, Boolean>(1);
    private static boolean portPoolLocked = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(BMProxyManager.class);
    private static final ThreadLocal<ProxyServer> PROXY_SERVERS = new ThreadLocal<ProxyServer>();

    private static HttpHost proxyProxyHost = null;
    private static UsernamePasswordCredentials proxyProxyCredentials = null;

    static {
        portPool.add(9999);
    }

    private BMProxyManager() {
    }

    private static void transferAndLockPortPool() {
        if (portPool == null) {
            throw new TesterraSystemException("Port pool in null");
        }

        String ports = "";
        for (Integer port : portPool) {
            ports += "\n" + port;
            usedPortPool.put(port, Boolean.FALSE);
        }

        LOGGER.info("Locking port pool with ports" + ports);

        portPoolLocked = true;
    }

    public static List<Integer> getPortPool() {
        if (portPoolLocked) {
            LOGGER.warn("PortPool is locked, your changes will have no effect");
        }
        return portPool;
    }

    public static synchronized ProxyServer getProxyServer() throws NoFreePortException {
        if (!portPoolLocked) {
            transferAndLockPortPool();
        }

        // return used proxy server
        if (PROXY_SERVERS.get() != null) {
            return PROXY_SERVERS.get();
        }

        // create new proxy server
        final Integer portFromPool = allocateFreePortFromPool();
        if (portFromPool == null) {
            throw new NoFreePortException("Could not get free port from port pool");
        }
        final ProxyServer proxyServer = new ProxyServer(portFromPool, proxyProxyHost, proxyProxyCredentials);
        PROXY_SERVERS.set(proxyServer);

        return proxyServer;
    }

    public static synchronized void shutDownProxyServer() {
        final ProxyServer proxyServer = PROXY_SERVERS.get();
        if (proxyServer == null) {
            LOGGER.warn("No managed BMP server active to shutdown");
            return;
        }

        final int port = proxyServer.getBmpProxyServer().getPort();
        proxyServer.stopProxy();

        removePortLock(port);
        PROXY_SERVERS.remove();
    }

    private static synchronized Integer allocateFreePortFromPool() {
        if (!usedPortPool.containsValue(Boolean.FALSE)) {
            // no free port
            return null;
        }

        // get free port
        Integer usedPort = null;
        for (Integer port : usedPortPool.keySet()) {
            if (usedPortPool.get(port) == Boolean.FALSE) {
                usedPort = port;
                break;
            }
        }

        if (usedPort == null) {
            return null; // should not happen
        } else {
            // set port as inUse and return it
            usedPortPool.remove(usedPort);
            usedPortPool.put(usedPort, Boolean.TRUE);

            LOGGER.info("Allocating port " + usedPort + " from port pool");
            return usedPort;
        }
    }

    private static synchronized void removePortLock(Integer port) {
        // set port as inUse and return it
        usedPortPool.remove(port);
        usedPortPool.put(port, Boolean.FALSE);

        LOGGER.info("Marking port " + port + " as free");
    }

    public static void setProxyProxyHost(HttpHost proxyProxyHost) {
        BMProxyManager.proxyProxyHost = proxyProxyHost;
    }

    public static void setProxyProxyCredentials(UsernamePasswordCredentials proxyProxyCredentials) {
        BMProxyManager.proxyProxyCredentials = proxyProxyCredentials;
    }

    public static void setPortPool(List<Integer> portPool) {
        BMProxyManager.portPool = portPool;
    }
}
