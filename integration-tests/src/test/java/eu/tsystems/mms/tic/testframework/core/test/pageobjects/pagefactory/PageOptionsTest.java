package eu.tsystems.mms.tic.testframework.core.test.pageobjects.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithPageOptions;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PageOptionsTest extends AbstractTestSitesTest {

    @Test
    public void testT01_PageOptions_ElementTimeout() {
        PageWithPageOptions page = PageFactory.create(PageWithPageOptions.class, WebDriverManager.getWebDriver());

        Assert.assertEquals(page.existingElement.getTimeoutInSeconds(), 3, "Timeout value from page options");
    }

}
