package io.testerra.report.test.pretest_status.layoutTests;

import eu.tsystems.mms.tic.testframework.annotations.Retry;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;
import io.testerra.report.test.pages.pretest.CheckPages.CollectCheckPage;
import io.testerra.report.test.pages.pretest.NonExistingPage;
import io.testerra.report.test.pages.pretest.UniversalPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Timer;

public class GenerateLayoutTestsTTReportTest extends AbstractTestSitesTest {

    @Test
    public void layoutTest01_layoutTestFailing(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 0;

        PAGE_FACTORY.createPage(UniversalPage.class).expect()
                .screenshot().pixelDistance("inputHtml_chromeHeadless").isLowerThan(screenshotPixelDistance);
    }

    @Test
    public void layoutTest02_layoutTestPassing(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 5.0;

        PAGE_FACTORY.createPage(UniversalPage.class).expect()
                .screenshot().pixelDistance("inputHtml_chromeHeadless").isLowerThan(screenshotPixelDistance);
    }

    @Test
    public void layoutTest03_layoutTestPassingWithMinor(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 5.0;

        PAGE_FACTORY.createPage(UniversalPage.class).expect()
                .screenshot().pixelDistance("inputHtml_chrome").isLowerThan(screenshotPixelDistance);
    }

}
