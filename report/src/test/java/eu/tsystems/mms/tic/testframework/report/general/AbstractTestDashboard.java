/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.TestReportFiveNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportFourNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportOneNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportSixNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportThreeNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoNumbers;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import org.testng.annotations.DataProvider;

public class AbstractTestDashboard extends AbstractAnnotationMarkerTest {

    /**
     * Wrapper method for doOpenBrowserAndReportDashboardPage
     *
     * @param reportDirectory
     * @return
     */
    public DashboardPage getDashboardPage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WEB_DRIVER_MANAGER.getWebDriver(), PropertyManager.getProperty(reportDirectory.getReportDirectory()));
    }

    @DataProvider(parallel = true)
    public Object[][] testResultNumbers(){
        Object[][] result = new Object[][]{
                new Object[]{ReportDirectory.REPORT_DIRECTORY_1,new TestReportOneNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_2,new TestReportTwoNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_3,new TestReportThreeNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_4,new TestReportFourNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_5,new TestReportFiveNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_6,new TestReportSixNumbers()}
        };
        return result;
    }
}
