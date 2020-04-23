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
package eu.tsystems.mms.tic.testframework.execution.testng.worker;

import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.List;

public abstract class GenerateReportsWorker implements Worker {
    protected List<XmlSuite> xmlSuites;
    protected List<ISuite> suites;
    protected String outputDirectory;
    protected JUnitXMLReporter jUnitXMLReporter;

    public GenerateReportsWorker() {
    }

    public void init(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory, JUnitXMLReporter jUnitXMLReporter) {
        this.xmlSuites = xmlSuites;
        this.suites = suites;
        this.outputDirectory = outputDirectory;
        this.jUnitXMLReporter = jUnitXMLReporter;
    }

    public static class SharedAttributes {
    }

    public abstract void run();

}
