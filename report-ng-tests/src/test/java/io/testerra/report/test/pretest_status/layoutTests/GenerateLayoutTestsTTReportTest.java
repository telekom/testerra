package io.testerra.report.test.pretest_status.layoutTests;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;
import io.testerra.report.test.pages.pretest.UniversalPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class GenerateLayoutTestsTTReportTest extends AbstractTestSitesTest {

    @Test
    public void layoutTest01_layoutTestFailing() {
        TestStep.begin("get web driver");
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("visit test page");
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 0;

        TestStep.begin("create page object and take screenshot");
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
