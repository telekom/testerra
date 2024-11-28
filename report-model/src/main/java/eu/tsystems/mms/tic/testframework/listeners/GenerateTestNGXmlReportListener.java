/*
 * Testerra
 *
 * (C) 2021,  Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;

import com.google.common.eventbus.Subscribe;

/**
 * Generate TestNG result XML
 *
 * @author mgn
 */
public class GenerateTestNGXmlReportListener implements Loggable, ExecutionFinishEvent.Listener {

    private final Report report = Testerra.getInjector().getInstance(Report.class);

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        log().debug("Generating TestNG XML report...");
        org.testng.reporters.XMLReporter testNgXmlReporter = new org.testng.reporters.XMLReporter();
        testNgXmlReporter.generateReport(event.getXmlSuites(), event.getSuites(), report.getReportDirectory(Report.XML_FOLDER_NAME).toString());
    }
}
