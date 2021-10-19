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
package eu.tsystems.mms.tic.testframework.core.server;

import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import java.io.File;
import java.net.ServerSocket;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class StaticServer {
    private Server server;
    private File rootDir;
    private int port;

    public StaticServer() {
        this(new File(System.getProperty("user.dir")));
    }

    public StaticServer(File rootDir) {
        this.rootDir = rootDir;
    }

    public int getPort() {
        return port;
    }

    public void start(int port) throws Exception {
        this.port = port;
        server = new Server(port);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(rootDir.getAbsolutePath());
        ContextHandler contextHandler= new ContextHandler("/");
        contextHandler.setHandler(resourceHandler);
        server.setHandler(contextHandler);

        server.setStopAtShutdown(true);
        server.start();
    }

    public void start() throws Exception {
        int port;
        try (
            ServerSocket socket = new ServerSocket(0);
        ) {
            port = socket.getLocalPort();
            socket.close();
            start(port);
        }
    }

    public void stop() throws Exception {
        if (server.isRunning()) {
            server.stop();
        }
    }

    public static void main(String[] args) throws Exception {
        StaticServer server = new StaticServer(FileUtils.getResourceFile("testsites"));
        server.start();
        System.out.println("Hit ENTER to stop");
        System.in.read();
        server.stop();
    }

}
