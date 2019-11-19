package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.pageobjects.PageWithPageOptions;

public class PageOptionsTest_IoC extends PageOptionsTest {

    @Override
    public PageWithPageOptions getPage() {
        return pageFactory.createPage(PageWithPageOptions.class);
    }
}
