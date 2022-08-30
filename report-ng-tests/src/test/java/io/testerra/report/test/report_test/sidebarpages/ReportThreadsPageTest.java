package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ReportThreadsPageTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForDifferentTestMethodForEachStatus")
    public void testT01_checkSearchForMethodsSelectionWorksCorrectly(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to threads page.");
        ReportThreadsPage reportThreadsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.THREADS, ReportThreadsPage.class);

        TestStep.begin("Check whether thread report contains all methods");

        reportThreadsPage = reportThreadsPage.clickSearchBar();
        reportThreadsPage = reportThreadsPage.selectMethod(method);
        reportThreadsPage.assertMethodBoxIsSelected(method);
    }

}
