package eu.tsystems.mms.tic.testframework.test.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.annotations.Test;

/**
 * Created on 04.05.2022
 *
 * @author mgn
 */
public class PageCommonTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test()
    public void testT01_pageIsDisplayed() {
        WebTestPage page = getPage();
        page.expect().displayed(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_PageIsDisplayed_fails() {
        WebTestPage page = getPage();
        page.expect().displayed(false);
    }

    @Test()
    public void testT03_pageIsPresent() {
        WebTestPage page = getPage();
        page.expect().present(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT04_PageIsPresent_fails() {
        WebTestPage page = getPage();
        page.expect().present(false);
    }
}
