package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithPageOptions;

public class PageOptionsTest extends PageOptionsTest_Deprecated {

    @Override
    public PageWithPageOptions getPage() {
        return pageFactory.createPage(PageWithPageOptions.class);
    }
}
