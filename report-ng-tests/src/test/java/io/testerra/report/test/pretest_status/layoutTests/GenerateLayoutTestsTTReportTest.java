package io.testerra.report.test.pretest_status.layoutTests;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;
import io.testerra.report.test.pages.pretest.UniversalPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class GenerateLayoutTestsTTReportTest extends AbstractTestSitesTest {

    @Test(groups = {Groups.EXT})
    public void layoutTest01_layoutTestFailing() {
        TestStep.begin("get web driver");
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("visit test page");
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 0;

        TestStep.begin("create page object and take screenshot");
        PAGE_FACTORY.createPage(UniversalPage.class).expect()
                .screenshot().pixelDistance("inputHtml_image2").isLowerThan(screenshotPixelDistance);
    }

    @Test(groups = {Groups.EXT})
    public void layoutTest02_layoutTestPassing() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 5.0;

        PAGE_FACTORY.createPage(UniversalPage.class).expect()
                .screenshot().pixelDistance("inputHtml_image2").isLowerThan(screenshotPixelDistance);
    }

    @Test(groups = {Groups.EXT})
    public void layoutTest03_layoutTestPassingWithMinor() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 5.0;

        PAGE_FACTORY.createPage(UniversalPage.class).expect()
                .screenshot().pixelDistance("inputHtml_image1").isLowerThan(screenshotPixelDistance);
    }

    @Test(groups = {Groups.EXT})
    public void layoutTest04_layoutTestFailing_MultiChecks() {
        TestStep.begin("get web driver");
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("visit test page");
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        double screenshotPixelDistance = 0;

        CONTROL.collectAssertions(() -> {
            TestStep.begin("create page object and take element screenshots");
            UniversalPage page = PAGE_FACTORY.createPage(UniversalPage.class);
            page.getFinder().findById("box1").assertThat()
                    .screenshot().pixelDistance("inputHtml_box1").isLowerThan(screenshotPixelDistance);
            page.getFinder().findById("box2").assertThat()
                    .screenshot().pixelDistance("inputHtml_box2").isLowerThan(screenshotPixelDistance);
            ASSERT.fail("Just a simple error message");
        });
    }

}
