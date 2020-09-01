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

package eu.tsystems.mms.tic.testframework.events;

import java.util.List;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

/**
 * This event gets fired when execution ends completely.
 * Use this to finalize your listeners.
 */
public class ExecutionEndEvent {
    public interface Listener {
        void onExecutionEnd(ExecutionEndEvent event);
    }
    private List<XmlSuite> xmlSuites;
    private List<ISuite> suites;

    public List<XmlSuite> getXmlSuites() {
        return xmlSuites;
    }

    public ExecutionEndEvent setXmlSuites(List<XmlSuite> xmlSuites) {
        this.xmlSuites = xmlSuites;
        return this;
    }

    public List<ISuite> getSuites() {
        return suites;
    }

    public ExecutionEndEvent setSuites(List<ISuite> suites) {
        this.suites = suites;
        return this;
    }
}
