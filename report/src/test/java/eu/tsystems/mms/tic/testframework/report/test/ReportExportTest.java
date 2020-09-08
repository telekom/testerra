/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.context.report.DefaultReport;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.io.File;
import org.testng.annotations.Test;

public class ReportExportTest extends TesterraTest {

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #921
    public void test_TestNG_XML() {
        File testNGExportFile = new File(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()) + "/" + DefaultReport.XML_FOLDER_NAME + "/testng-results.xml");
        Assert.assertTrue(testNGExportFile.exists());
    }

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #922
    public void test_JUnit_XML() {
        File testNGExportFile = new File(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()) + "/" + DefaultReport.XML_FOLDER_NAME + "/TEST-Results.xml");
        Assert.assertTrue(testNGExportFile.exists());
    }
}
