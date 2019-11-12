/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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

import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.external.junit.SimpleReportEntry;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import org.json.JSONObject;

/**
 * Created by pele on 30.01.2017.
 */
public class GenerateOtherOutputsWorker extends GenerateReportsWorker {
    @Override
    public void run() {
        /*
        Create status json
         */
        JSONObject statusJSON = TestStatusController.createStatusJSON();
        LOGGER.info("Status:\n" + statusJSON);

        /*
        Create surefire and testng results xml
         */
        // generate xml reports for surefire
        LOGGER.info("Generating xml reports...");
        jUnitXMLReporter.testSetCompleted(new SimpleReportEntry("", "Results"));
        // generate testng-results.xml
        org.testng.reporters.XMLReporter testNgXmlReporter = new org.testng.reporters.XMLReporter();
        testNgXmlReporter.generateReport(xmlSuites, suites, Report.XML_DIRECTORY.getAbsolutePath());
    }
}
