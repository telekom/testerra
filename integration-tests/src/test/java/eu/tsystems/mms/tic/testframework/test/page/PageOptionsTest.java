package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithPageOptions;

public class PageOptionsTest extends PageOptionsTest_Deprecated {

    @Override
    public PageWithPageOptions getPage() {
        return pageFactory.createPage(PageWithPageOptions.class);
    }
}
