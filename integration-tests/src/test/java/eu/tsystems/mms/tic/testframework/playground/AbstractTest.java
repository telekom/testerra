package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsDisplayed;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import org.testng.Assert;

/**
 * Created on 2022-10-04
 *
 * @author mgn
 */
public class AbstractTest extends TesterraTest implements WebDriverManagerProvider, PageFactoryProvider {

    public void doFail() {
        Assert.fail("A simple failed.");
    }

    public void doFailWithinAPage() {
        PAGE_FACTORY.createPage(PageWithCheckRuleIsDisplayed.class, WEB_DRIVER_MANAGER.getWebDriver());
    }

}
