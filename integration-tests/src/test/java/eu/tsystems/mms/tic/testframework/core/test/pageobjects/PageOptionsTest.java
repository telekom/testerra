package eu.tsystems.mms.tic.testframework.core.test.pageobjects;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithPageOptions;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PageOptionsTest extends AbstractTestSitesTest implements IPageFactoryTest {

    @Test
    public void testT01_PageOptions_ElementTimeout() {
        WebDriver driver = WebDriverManager.getWebDriver();

        driver.get(TestPage.INPUT_TEST_PAGE.getUrl());

        PageWithPageOptions page = getPage();

        Assert.assertEquals(page.existingElement.getTimeoutInSeconds(), 3, "Timeout value from page options");
    }

    @Override
    public PageWithPageOptions getPage() {
        return PageFactory.create(PageWithPageOptions.class, WebDriverManager.getWebDriver());
    }
}
