package eu.tsystems.mms.tic.testframework.core.test.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.IPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithPageOptions;

public class PageOptionsTest_IoC extends PageOptionsTest {

    private static final IPageFactory pageFactory = Testerra.injector.getInstance(IPageFactory.class);

    @Override
    public PageWithPageOptions getPage() {
        return pageFactory.createPage(PageWithPageOptions.class);
    }
}
