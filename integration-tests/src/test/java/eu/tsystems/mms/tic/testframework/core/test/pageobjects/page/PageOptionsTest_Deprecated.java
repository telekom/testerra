package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithPageOptions;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

public class PageOptionsTest_Deprecated extends AbstractTestSitesTest implements PageFactoryTest {

    @Test
    public void testT01_PageOptions_ElementTimeout() {
        PageWithPageOptions page = getPage();
        Assert.assertEquals(page.existingElement.getTimeoutInSeconds(), 3, "Timeout value from page options");
    }

    @Override
    public PageWithPageOptions getPage() {
        return PageFactory.create(PageWithPageOptions.class, WebDriverManager.getWebDriver());
    }
}
