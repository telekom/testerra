package io.testerra.report.test.ReportMethodPages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.report.ReportConcreteMethodPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.DateTimeUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ReportConcreteMethodPageTest extends AbstractReportTest {

    @Test
    public void testT01_checkPrimeCardIsCorrect() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Get content of all methods");
        reportTestsPage.clickConfigurationMethodsSwitch();
        List<String[]> methodsContentRows = reportTestsPage.getTable();

        TestStep.begin("Navigate to method detail page and check for correct content");
        ReportConcreteMethodPage reportConcreteMethodPage;
        for (String[] row : methodsContentRows) {
            reportConcreteMethodPage = reportTestsPage.navigateToMethodReport(Integer.parseInt(row[2])-1);
            reportConcreteMethodPage.assertMethodePrimeCardContainsCorrectContent(row);
            ReportThreadsPage reportThreadsPage = reportConcreteMethodPage.clickThreadLink();
            reportThreadsPage.assertMethodBoxIsSelected(row[3]);
            reportTestsPage = reportThreadsPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.clickConfigurationMethodsSwitch();
        }
    }

    @Test
    public void testT02_checkDurationFormat() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to method page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
        ReportConcreteMethodPage reportConcreteMethodPage = reportTestsPage.navigateToMethodReport(0);

        TestStep.begin("Check whether the duration is displayed and correct");
        final String testDuration = reportConcreteMethodPage.getTestDuration();
        final boolean dateFormatIsCorrect = DateTimeUtils.verifyDateTimeString(testDuration);
        Assert.assertTrue(dateFormatIsCorrect, String.format("Test Duration '%s' has correct format", testDuration));
    }

    @Test
    public void testT03_checkLastScreenShotCard(){

    }

}
