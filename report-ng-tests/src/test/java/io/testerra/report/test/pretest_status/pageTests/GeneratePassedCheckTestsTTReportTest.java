package io.testerra.report.test.pretest_status.pageTests;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;
import io.testerra.report.test.pages.pretest.CheckPages.OptionalCheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.TimeoutCheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_DISPLAYED_CheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_NOT_DISPLAYED_CheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_NOT_PRESENT_CheckPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_PRESENT_CheckPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class GeneratePassedCheckTestsTTReportTest extends AbstractTestSitesTest {

    private final static TestPage testPage = TestPage.DUMMY_TEST_PAGE;

    @Test(groups = {Groups.EXT})
    public void preTest02_optionalCheck_passed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(OptionalCheckPage.class);
    }

    @Test(groups = {Groups.EXT})
    public void preTest03_timeoutCheck_passed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(TimeoutCheckPage.class);
    }


    @Test(groups = {Groups.EXT})
    public void preTest04_checkRule_IS_DISPLAYED_check_passed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_DISPLAYED_CheckPage.class);
    }

    @Test(groups = {Groups.EXT})
    public void preTest05_checkRule_IS_NOT_DISPLAYED_check_passed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_NOT_DISPLAYED_CheckPage.class);
    }

    @Test(groups = {Groups.EXT})
    public void preTest06_checkRule_IS_PRESENT_check_passed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_PRESENT_CheckPage.class);
    }

    @Test(groups = {Groups.EXT})
    public void preTest07_checkRule_IS_NOT_PRESENT_check_passed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        PAGE_FACTORY.createPage(CheckRule_IS_NOT_PRESENT_CheckPage.class);
    }

    @Test(groups = {Groups.EXT})
    public void preTest08_priorityMessagesTest(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        log().info("It is gonna be ok.", Loggable.prompt);

        // some test activities ...
        log().warn("Warn me!", Loggable.prompt);

        // some test activities ...
        log().error("Tell me more!", Loggable.prompt);
    }

    @Test(groups = {Groups.EXT})
    public void preTest09_priorityMessagesGlobalContentTest() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, testPage);

        new Thread(() -> log().info("It is gonna be ok.", Loggable.prompt)).start();

        new Thread(() -> log().warn("Warn me!", Loggable.prompt)).start();

        new Thread(() -> log().error("Tell me more!", Loggable.prompt)).start();
    }

}
