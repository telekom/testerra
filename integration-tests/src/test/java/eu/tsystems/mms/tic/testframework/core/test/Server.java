package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.seleniumhq.jetty9.server.handler.ContextHandler;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

public class Server {
    private org.seleniumhq.jetty9.server.Server server;
    private File rootDir;

    public Server(File rootDir) {
        this.rootDir = rootDir;
    }

    public void start(int port) throws Exception {
        server = new org.seleniumhq.jetty9.server.Server(port);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(rootDir.getAbsolutePath());
        ContextHandler contextHandler= new ContextHandler("/");
        contextHandler.setHandler(resourceHandler);
        server.setHandler(contextHandler);

        server.setStopAtShutdown(true);
        server.start();
    }

    public int start() throws Exception {
        int port;
        try (
            ServerSocket socket = new ServerSocket(0);
        ) {
            port = socket.getLocalPort();
        }
        start(port);
        return port;
    }

    public void stop() throws Exception {
        if (server.isRunning()) {
            server.stop();
        }
    }

    public static void main(String[] args) throws Exception {
        TesterraCommons.init();
        Server server = new Server(FileUtils.getResourceFile("testsites"));
        server.start();
        System.out.println("Hit ENTER to stop");
        System.in.read();
        server.stop();
    }

}
