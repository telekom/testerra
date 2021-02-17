/*
 * Testerra
 *
 * (C) 2020, Alex Rockstroh, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.AbstractReportFailuresTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.FailureAspectEntry;
import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoFailureAspects;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractFailurePointsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractResultTableFailureEntry;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import java.util.List;
import org.testng.annotations.BeforeMethod;

@TestClassContext(name = "View-FailureAspects")
public class FailureAspectsPageTest extends AbstractReportFailuresTest {

    @BeforeMethod(alwaysRun = true)
    @Override
    public void initTestObjects() {
        this.failurePointType = ResultTableFailureType.FAILURE_ASPECT;
        this.reportFilter = SystemTestsGroup.SYSTEMTESTSFILTER2;;
        this.failurePointEntryTestObjects = getExpectedFailurePointEntries();
    }

    @Override
    protected int getNumberOfExpectedFailurePointsForReport() {
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                return new TestReportTwoNumbers().getFailureAspects();
            default:
                throw new RuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected int getNumberOfExpectedFailurePointsForTestResult(TestResultHelper.TestResultFailurePointEntryType entryType) {
        int counter = 0;
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                for (FailureAspectEntry failureAspect : TestReportTwoFailureAspects.getAllFailureAspectEntryTestObjects()) {
                    if (failureAspect.getFailurePointEntryType().equals(entryType)) {
                        counter++;
                    }
                }
                return counter;
            default:
                throw new RuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected List<? extends AbstractResultTableFailureEntry> getExpectedFailurePointEntries() {
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                return TestReportTwoFailureAspects.getAllFailureAspectEntryTestObjects();
            default:
                throw new RuntimeException("Not implemented for Report: " + reportFilter);
        }
    }
    @Override
    protected void checkExpectedFailedMarkWorkflow(boolean intoReport) {
        final int failedNotIntoReportPosition = 4;
        final int failedIntoReportPosition = 6;
        final int position;
        if (intoReport) {
            position = failedIntoReportPosition;
        } else {
            position = failedNotIntoReportPosition;
        }
        final AbstractResultTableFailureEntry failedEntry = failurePointEntryTestObjects.get(position - 1);
        AbstractFailurePointsPage failurePointsPage = openFailuresPointsPage(ReportDirectory.REPORT_DIRECTORY_2);
        failurePointsPage.assertExpectedFailsReportMark(failedEntry, intoReport);
    }

    @Override
    protected AbstractFailurePointsPage openFailuresPointsPage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportFailureAspectsPage(WEB_DRIVER_MANAGER.getWebDriver(), PropertyManager.getProperty(reportDirectory.getReportDirectory()));
    }
}
