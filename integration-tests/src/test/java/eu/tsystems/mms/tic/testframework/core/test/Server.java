package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.seleniumhq.jetty9.server.handler.ContextHandler;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.BindException;

public class Server {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final org.seleniumhq.jetty9.server.Server server = new org.seleniumhq.jetty9.server.Server(80);

    public static void start() throws Exception {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);

        File testsites = FileUtils.getResourceFile("testsites");
        resourceHandler.setResourceBase(testsites.getAbsolutePath());

        ContextHandler contextHandler= new ContextHandler("/testsites");
        contextHandler.setHandler(resourceHandler);

        server.setHandler(contextHandler);

        server.setStopAtShutdown(true);
        try {
            server.start();
        } catch (BindException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public static void stop() throws Exception {
        if (server.isRunning()) {
            server.stop();
        }
    }

    public static void main(String[] args) throws Exception {
        TesterraCommons.init();
        start();
        System.out.println("Hit ENTER to stop");
        System.in.read();
        stop();
    }

}
