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
package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.junit.JUnitXMLReporter;
import eu.tsystems.mms.tic.testframework.report.junit.SimpleReportEntry;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Generates JUnit xml reports
 */
public class GenerateJUnitXmlReportListener implements
        Loggable,
        ExecutionFinishEvent.Listener,
        TestStatusUpdateEvent.Listener,
        ISuiteListener,
        ExecutionAbortEvent.Listener {
    private Report report = TesterraListener.getReport();

    private JUnitXMLReporter xmlReporter;

    private org.testng.reporters.JUnitXMLReporter junitXMLReporter;

    public GenerateJUnitXmlReportListener() {
        xmlReporter = new JUnitXMLReporter(true, report.getReportDirectory(Report.XML_FOLDER_NAME));
    }

    @Subscribe
    @Override
    public void onStart(ISuite suite) {
        xmlReporter.testSetStarting(new SimpleReportEntry("", "Test starting"));
    }

    @Subscribe
    @Override
    public void onTestStatusUpdate(TestStatusUpdateEvent event) {

        Optional<Method> methodFromEvent = this.getMethodFromEvent(event);
        if (methodFromEvent.isPresent()) {

            Status status = event.getMethodContext().getStatus();
            Method method = methodFromEvent.get();

            SimpleReportEntry reportEntry = new SimpleReportEntry(method.getDeclaringClass().getName(), method.getName());

            switch (status) {
                case PASSED:
                case REPAIRED:
                case RECOVERED:
                    xmlReporter.testSucceeded(reportEntry);
                    break;
                case FAILED:
                case FAILED_EXPECTED:
                    // event.getMethodContext().getTestNgResult().get() --> Error message
                    xmlReporter.testFailed(reportEntry, "", "");
                    break;
                case NO_RUN:
                case RETRIED:
                    // do nothing
                    break;
                case SKIPPED:
                    xmlReporter.testSkipped(reportEntry);
                    break;
                default:
                    log().debug(String.format("Method state %s of %s cannot handle.", status, method.getName()));
            }
        }
    }

    @Subscribe
    @Override
    public void onExecutionFinish(ExecutionFinishEvent event) {
        generateReport();

        // generate testng-results.xml
//        org.testng.reporters.XMLReporter testNgXmlReporter = new org.testng.reporters.XMLReporter();
//        testNgXmlReporter.generateReport(event.getXmlSuites(), event.getSuites(), report.getReportDirectory(Report.XML_FOLDER_NAME).toString());
    }

    @Subscribe
    @Override
    public void onExecutionAbort(ExecutionAbortEvent event) {
        generateReport();
    }

    private void generateReport() {
        /*
        Create status json
         */
//        JSONObject statusJSON = TestStatusController.createStatusJSON();
//        log().debug("Status:\n" + statusJSON);

        log().debug("Generating JUnit XML report...");
        xmlReporter.testSetCompleted(new SimpleReportEntry("", "Results"));

    }

    private Optional<Method> getMethodFromEvent(TestStatusUpdateEvent event) {
        return event.getMethodContext().getTestNgResult()
                .map(iTestResult -> iTestResult.getMethod().getConstructorOrMethod().getMethod());
    }


}
