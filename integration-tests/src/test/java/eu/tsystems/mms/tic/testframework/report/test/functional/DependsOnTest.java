package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTest;
import eu.tsystems.mms.tic.testframework.report.general.MethodDependency;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ReportConfigMethodStateHelper;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules.DashboardModuleMethodChart;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestDependsOn;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by fakr on 09.10.2017
 */
@FennecClassContext("Functional-DependsOn")
public class DependsOnTest extends AbstractTest {

    /**
     * Checks whether the timestamp of the methods on the classes page are fitting to the execution order.
     * Concerning DataProvider and DependsOn test cases
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT01_checkOrderOfDependsOnMethods() {
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()),
                ReportTestUnderTestDependsOn.class.getSimpleName());
        classesDetailsPage.assertMethodExecutionOrder();
    }

    /**
     * Checks whether @AfterMethod is executing in a correct manner
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT02_checkAfterMethodIsExecutedCorrectly() {
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()),
                ReportTestUnderTestDependsOn.class.getSimpleName());
        classesDetailsPage.changeConfigMethodDisplayStateTo(ReportConfigMethodStateHelper.ConfigMethodState.SHOWING);
        classesDetailsPage.assertMethodExecutionOrder();
    }

    /**
     * Checks whether the depends on test result is displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "dependsOnParallel")
    @Fails(ticketString = "XETA-658")
    public void testT03_checkDependsOnMethodChartDisplaysTheExpectedTestResults(String dependsOnClassName) {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        int barChartPosition = dashboardPage.dashboardModuleClassBarChart.getBarChartElementNumberByClassName(dependsOnClassName);
        AssertCollector.assertNotEquals(barChartPosition, -1, "The Class " + dependsOnClassName + " does not exists in bar chart class names");
        dashboardPage.dashboardModuleClassBarChart.getBarChartLegendElementByPosition(barChartPosition).click();
        DashboardModuleMethodChart dashboardModuleMethodChart = dashboardPage.getMethodChartModule();
        dashboardModuleMethodChart.assertMethodChartIsDisplayedForTestResult(TestResultHelper.TestResult.FAILED);
        dashboardModuleMethodChart.assertMethodChartIsDisplayedForTestResult(TestResultHelper.TestResult.PASSED);
        dashboardModuleMethodChart.assertMethodChartIsDisplayedForTestResult(TestResultHelper.TestResult.SKIPPED);
    }

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "dependsOnTestObjects")
    @Fails(ticketString = "XETA-658")
    public void testT04_checkDependsOnMethodChartEntryStates(MethodDependency dependsOnTestObject) {

        final String testClassName = "ReportTestUnderTestDependsOn__Report__TestsUnderTest__Parallel__2";
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString())).gotoClassesDetailsPageForClass(testClassName);

        String tagName = dependsOnTestObject.getTagName();

        if (tagName.equals("")) {
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(dependsOnTestObject.getMethodName(), dependsOnTestObject.getTestResult());
        } else {
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategoryWithTag(dependsOnTestObject.getMethodName(), dependsOnTestObject.getTestResult(), tagName);
        }
    }

    @DataProvider(name = "dependsOnTestObjects")
    public Object[][] createDependsOnTestObjects() {
        Object[][] objects = new Object[10][1];
        objects[0][0] = MethodDependency.FAILED_ALWAYS;
        objects[1][0] = MethodDependency.FAILED_DEPENDSON_PASSED;
        objects[2][0] = MethodDependency.FAILED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN2;
        objects[3][0] = MethodDependency.FAILED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN3;
        objects[4][0] = MethodDependency.PASSED_ALWAYS;
        objects[5][0] = MethodDependency.PASSED_DEPENDSON_FAILED_BUT_ALWAYS_RUN;
        objects[6][0] = MethodDependency.PASSED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN1;
        objects[7][0] = MethodDependency.SKIPPED_DEPENDSON_FAILED_WITH_DATAPROVIDER;
        objects[8][0] = MethodDependency.SKIPPED_DEPENDSON_FAILED;
        objects[9][0] = MethodDependency.SKIPPED_DEPENDSON_FAILED_WITH_SECOND_DATAPROVIDER;

        return objects;
    }

    @DataProvider(name = "dependsOnParallel")
    public Object[][] createDependsOnParallel() {
        Object[][] objects = new Object[2][1];
        objects[0][0] = "ReportTestUnderTestDependsOn__Report__TestsUnderTest__Sequence__1";
        objects[1][0] = "ReportTestUnderTestDependsOn__Report__TestsUnderTest__Parallel__2";
        return objects;

    }

}
