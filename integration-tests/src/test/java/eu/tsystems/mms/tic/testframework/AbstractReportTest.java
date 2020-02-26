package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.core.test.Server;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * Abstract test classes which using generated local report directories
 */
public abstract class AbstractReportTest extends AbstractWebDriverTest {
    private Server server = new Server();

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        server.start();
        WebDriverManager.setBaseURL(String.format("http://localhost:%d", server.getPort()));
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() throws Exception {
        server.stop();
    }
}
