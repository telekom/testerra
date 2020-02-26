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
package eu.tsystems.mms.tic.testframework.bmp;

import org.apache.http.HttpHost;

import java.io.IOException;

/**
 * Created by pele on 31.08.2015.
 */
public class Proxy {

    public static void main(String[] args) throws NoFreePortException, IOException {
        System.out.println("Parameters: proxyProxyHostname proxyProxyPort");
        /*
        [0] hostname
        [1] port
         */

        String hostname = null;
        int port = -1;
        if (args != null) {
            if (args.length >= 1) {
                hostname = args[0];
            }
            if (args.length >= 2) {
                port = Integer.valueOf(args[1]);
            }
        }

        if (hostname != null && port != -1) {
            HttpHost proxyProxyHost = new HttpHost(hostname, port);
            BMProxyManager.setProxyProxyHost(proxyProxyHost);
        }

        // start
        ProxyServer proxyServer = BMProxyManager.getProxyServer();

        System.out.println("\nHit ENTER to stop proxy.\n");
        System.in.read();

        // stop
        proxyServer.stopProxy();
    }
}
