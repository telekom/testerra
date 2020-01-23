package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.core.test.Server;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.BindException;

public class ServerTest {

    private Server server;

    @Test
    public void test_StartServer() throws Exception {
        server = new Server();
        server.start(1234);
    }

    @Test(dependsOnMethods = "test_StartServer")
    public void test_AlreadyStartedServer() throws Exception{
        Server newServer = new Server();
        try {
            newServer.start(1234);
        } catch (BindException e) {
            Assert.assertEquals(newServer.getPort(), 1234);
        }
    }

    @Test
    public void test_NewRandomPort() throws Exception {
        Server newServer = new Server();
        newServer.start();
        Assert.assertTrue(newServer.getPort() > 0);
    }
}
