/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.execution.testng.worker;

import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.LinkedList;
import java.util.List;

public class GenerateReportsWorkerExecutor implements WorkerExecutor {

    private final List<GenerateReportsWorker> workers = new LinkedList<>();

    public void add(GenerateReportsWorker worker) {
        workers.add(worker);
    }

    public void run(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory, JUnitXMLReporter jUnitXMLReporter) {
        workers.forEach(w -> {
            w.init(xmlSuites, suites, outputDirectory, jUnitXMLReporter);
            w.run();
        });
    }

    @Override
    public void add(Worker worker) {
        add((GenerateReportsWorker) worker);
    }
}
