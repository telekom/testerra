package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.TesterraClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFailurePointsPage;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractResultTableFailureEntry;
import eu.tsystems.mms.tic.testframework.report.general.AbstractReportFailuresTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.BeforeMethod;

import java.util.List;

/**
 * Created by riwa on 04.04.2017.
 */
@TesterraClassContext("View-ExitPoints")
public class ExitPointsPageTest extends AbstractReportFailuresTest {
    //TODO restructure tests: find exit points with the help of method names and not with index

    @BeforeMethod(alwaysRun = true)
    @Override
    public void initTestObjects() {
        this.failurePointType = ResultTableFailureType.EXIT_POINT;
        this.reportFilter = SystemTestsGroup.SYSTEMTESTSFILTER2;
        this.failurePointEntryTestObjects = getExpectedFailurePointEntries();
    }

    @Override
    protected int getNumberOfExpectedFailurePointsForReport() {
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                return new TestReportTwoNumbers().getExitPoints();
            default:
                throw new TesterraRuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected int getNumberOfExpectedFailurePointsForTestResult(TestResultHelper.TestResultFailurePointEntryType entryType) {
        int counter = 0;
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                for (ExitPointEntry exitPoint:TestReportTwoExitPoints.getAllExitPointEntryTestObjects()) {
                    if (exitPoint.getFailurePointEntryType().equals(entryType)) {
                        counter++;
                    }
                }
                return counter;
            default:
                throw new TesterraRuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected List<? extends AbstractResultTableFailureEntry> getExpectedFailurePointEntries() {
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                return TestReportTwoExitPoints.getAllExitPointEntryTestObjects();
            default:
                throw new TesterraRuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected void checkExpectedFailedMarkWorkflow(boolean intoReport) {

        AbstractResultTableFailureEntry failedEntry;

        if(intoReport) {
            failedEntry = TestReportTwoExitPoints.FailedIntoReport;
        } else {
            failedEntry = TestReportTwoExitPoints.FailedNotIntoReport;
        }

        AbstractFailurePointsPage failurePointsPage = openFailuresPointsPage(ReportDirectory.REPORT_DIRECTORY_2);
        failurePointsPage.assertExpectedFailsReportMark(failedEntry, intoReport);
    }

    @Override
    protected AbstractFailurePointsPage openFailuresPointsPage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportExitPointsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(reportDirectory.getReportDirectory()));
    }

}
