package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportLogsPage;
import io.testerra.report.test.pages.utils.LogLevel;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ReportLogsPageTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForLogLevel")
    public void testT01_checkLogLevelFilter(LogLevel logLevel) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check whether the logLevel-select works correctly");
        reportLogsPage = reportLogsPage.selectLogLevel(logLevel);
        reportLogsPage.assertLogReportContainsCorrectLogLevel(logLevel);
    }

    @Test(enabled = false)
    @Fails(description = "filter does not work correct (issues with brackets, spaces and not even applied to whole log report)")
    public void testT02_filterForWholeLogLines() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check whether the search filter works correctly");
        //fails because filter does not work correct
        // -> filter does not work when there are leading or trailing spaces
        // -> filter does not work when there are brackets in the filter request
        // (-> filter is only applied on the right side of the logReport behind the "-"- sign in each line)

        //TODO: search constraints: bug or feature?
        // --> set issue on github (as bug)

        // reportLogsPage.assertLogReportIsCorrectWhenSearchingForDifferentLogLines();
    }

    @Test(dataProvider = "dataProviderForPreTestMethods_Classes_States")
    public void testT03_filterForMethodContent(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String methodeName = data.getMethod();
        String methodeClass = data.getMethodClass();
        Status methodStatus = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check methode name is contained in log report");
        reportLogsPage = reportLogsPage.search(methodeName);
        reportLogsPage.assertMarkedLogLinesContainText(methodeName);

        TestStep.begin("Check methode class is contained in log report");
        reportLogsPage =reportLogsPage.search(methodeClass);
        reportLogsPage.assertMarkedLogLinesContainText(methodeClass);

        TestStep.begin("Check methode name is contained in log report");
        reportLogsPage =reportLogsPage.search(methodStatus.title);
        reportLogsPage.assertMarkedLogLinesContainText(methodStatus.title);
    }

}
