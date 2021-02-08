package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithPageOptions;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;

public class PageOptionsTest extends AbstractTestSitesTest implements PageFactoryProvider, PageFactoryTest {

    @Override
    public PageWithPageOptions getPage() {
        return pageFactory.createPage(PageWithPageOptions.class, getWebDriver());
    }
}
