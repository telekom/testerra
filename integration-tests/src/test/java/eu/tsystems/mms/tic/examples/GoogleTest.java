package eu.tsystems.mms.tic.examples;

import eu.tsystems.mms.tic.testframework.pageobjects.google.ResultPage;
import eu.tsystems.mms.tic.testframework.pageobjects.google.StartPage;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.Test;

public class GoogleTest extends TesterraTest {

    private StartPage prepare() {
        StartPage page = pageFactory.createPage(StartPage.class);
        page.call("https://google.de");
        return page;
    }

    @Test
    public void test_Search() {
        StartPage startPage = prepare();
        ResultPage resultPage = startPage.search("testerra");
        resultPage.result(1).headline().text().contains("Testerra");
    }

}
