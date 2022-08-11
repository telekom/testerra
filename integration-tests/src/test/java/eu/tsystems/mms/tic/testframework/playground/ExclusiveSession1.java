package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author mgn
 */
public class ExclusiveSession1 extends AbstractTestSitesTest implements PageFactoryProvider {

    private String exclusiveSessionId = "";

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getWebDriver();
        exclusiveSessionId = WEB_DRIVER_MANAGER.makeExclusive(driver);
    }

    @Test
    public void testT01SessionTest() {
        WEB_DRIVER_MANAGER.getWebDriver(exclusiveSessionId);
        TimerUtils.sleep(2_000, "Extend test duration");
        UITestUtils.takeScreenshots();
        TimerUtils.sleep(5_000, "Extend test duration");
    }

    @AfterClass
    public void afterClass() {
        WEB_DRIVER_MANAGER.shutdownSession(exclusiveSessionId);
    }

}
