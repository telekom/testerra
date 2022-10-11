package io.testerra.report.test.pretest_status.pageTests;

import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;
import io.testerra.report.test.pages.pretest.CheckPages.CollectCheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.PrioritizedErrorMessageCheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.TimeoutCheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_DISPLAYED_CheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_NOT_DISPLAYED_CheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_NOT_PRESENT_CheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_PRESENT_CheckPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class GenerateFailedCheckTestsTTReportTest extends AbstractTestSitesTest {

    private final static TestPage testPage = TestPage.DUMMY_TEST_PAGE_2;

    @Test
    public void preTest01_collectCheck() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CollectCheckPage.class);

    }

    @Test
    public void preTest03_prioritizedErrorMessageCheck() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(PrioritizedErrorMessageCheckPage.class);
    }

    @Test
    public void preTest04_timeoutCheck() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(TimeoutCheckPage.class);
    }


    @Test
    public void preTest05_checkRule_IS_DISPLAYED_check() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_DISPLAYED_CheckPage.class);
    }

    @Test
    public void preTest06_checkRule_IS_NOT_DISPLAYED_check() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_NOT_DISPLAYED_CheckPage.class);
    }

    @Test
    public void preTest07_checkRule_IS_PRESENT_check() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_PRESENT_CheckPage.class);
    }

    @Test
    public void preTest08_checkRule_IS_NOT_PRESENT_check() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_NOT_PRESENT_CheckPage.class);
    }

}
