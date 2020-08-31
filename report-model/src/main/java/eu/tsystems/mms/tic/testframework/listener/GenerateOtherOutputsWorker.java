/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.listener;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.ExecutionEndEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.junit.JUnitXMLReporter;
import eu.tsystems.mms.tic.testframework.report.junit.SimpleReportEntry;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class GenerateOtherOutputsWorker implements
        Loggable,
        ExecutionEndEvent.Listener,
        MethodEndEvent.Listener,
        ISuiteListener
{
    Report report = new Report();

    JUnitXMLReporter XML_REPORTER;

    public GenerateOtherOutputsWorker() {
        XML_REPORTER =  new JUnitXMLReporter(true, report.getReportDirectory(Report.XML_FOLDER_NAME));
    }

    @Subscribe
    @Override
    public void onStart(ISuite suite) {
        XML_REPORTER.testSetStarting(new SimpleReportEntry("", "Test starting"));
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        // create xml results entry
        SimpleReportEntry reportEntry = new SimpleReportEntry(event.getMethod().getDeclaringClass().getName(), event.getMethod().getName());
        if (event.getTestResult().isSuccess()) {
            XML_REPORTER.testSucceeded(reportEntry);
        } else if (event.isFailed()) {
            // set xml status
            XML_REPORTER.testFailed(reportEntry, "", "");
        }
    }

    @Subscribe
    @Override
    public void onExecutionEnd(ExecutionEndEvent event) {
        /*
        Create status json
         */
//        JSONObject statusJSON = TestStatusController.createStatusJSON();
//        log().debug("Status:\n" + statusJSON);

        /*
        Create surefire and testng results xml
         */
        // generate xml reports for surefire
        log().debug("Generating xml reports...");
        XML_REPORTER.testSetCompleted(new SimpleReportEntry("", "Results"));
        // generate testng-results.xml
        org.testng.reporters.XMLReporter testNgXmlReporter = new org.testng.reporters.XMLReporter();
        testNgXmlReporter.generateReport(event.getXmlSuites(), event.getSuites(), report.getReportDirectory(Report.XML_FOLDER_NAME).toString());
    }
}
