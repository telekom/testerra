/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.external.junit.SimpleReportEntry;
import eu.tsystems.mms.tic.testframework.report.model.context.report.StaticReport;
import org.json.JSONObject;

public class GenerateXmlReportWorker extends GenerateReportsWorker implements Loggable {
    @Override
    public void run() {
        /*
        Create status json
         */
        JSONObject statusJSON = TestStatusController.createStatusJSON();
        log().debug("Status:\n" + statusJSON);

        /*
        Create surefire and testng results xml
         */

        Report report = Testerra.injector.getInstance(Report.class);
        // generate xml reports for surefire
        log().debug("Generating xml reports...");
        jUnitXMLReporter.testSetCompleted(new SimpleReportEntry("", "Results"));
        // generate testng-results.xml
        org.testng.reporters.XMLReporter testNgXmlReporter = new org.testng.reporters.XMLReporter();
        testNgXmlReporter.generateReport(xmlSuites, suites, report.getReportDirectory(StaticReport.XML_FOLDER_NAME).toString());
    }
}
