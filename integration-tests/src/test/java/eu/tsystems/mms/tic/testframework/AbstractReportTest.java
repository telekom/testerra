package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.core.test.Server;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.File;

/**
 * Abstract test classes which using generated local report directories
 */
public abstract class AbstractReportTest extends AbstractWebDriverTest {
    private Server server = new Server(new File(System.getProperty("user.dir")));

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        int port = server.start();
        WebDriverManager.setBaseURL(String.format("http://localhost:%d", port));
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() throws Exception {
        server.stop();
    }
}
