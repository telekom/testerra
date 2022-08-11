package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/*
 *
 * @author mgn
 */
public class ExclusiveSession2 extends AbstractTestSitesTest implements PageFactoryProvider {

    private String exclusiveSessionId = "";

    protected TestPage getTestPage() {
        return TestPage.LAYOUT;
    }

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getWebDriver();
        exclusiveSessionId = WEB_DRIVER_MANAGER.makeExclusive(driver);
    }

    @Test
    public void testT02SessionTest() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(exclusiveSessionId);
        PAGE_FACTORY.createPage(PageWithNotExistingElement.class, driver);
    }

    @AfterClass
    public void afterClass() {
        WEB_DRIVER_MANAGER.shutdownSession(exclusiveSessionId);
    }

}
