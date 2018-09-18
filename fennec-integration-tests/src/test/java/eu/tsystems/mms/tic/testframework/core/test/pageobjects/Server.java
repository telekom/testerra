package eu.tsystems.mms.tic.testframework.core.test.pageobjects;

import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.seleniumhq.jetty9.server.handler.ContextHandler;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;

import java.io.File;

public class Server {

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
        server.start();
    }

    public static void stop() throws Exception {
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        FennecCommons.init();
        start();
        System.out.println("Hit ENTER to stop");
        System.in.read();
        stop();
    }

}
