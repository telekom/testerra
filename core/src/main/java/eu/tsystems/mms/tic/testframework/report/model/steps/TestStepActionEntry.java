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
package eu.tsystems.mms.tic.testframework.report.model.steps;

import eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.Serializable;

public class TestStepActionEntry implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    public final LogMessage logMessage;
    public final Screenshot screenshot;
    public final ClickPathEvent clickPathEvent;

    public TestStepActionEntry(LogMessage logMessage) {
        this.logMessage = logMessage;
        this.screenshot = null;
        this.clickPathEvent = null;
    }

    public TestStepActionEntry(ClickPathEvent event) {
        this.clickPathEvent = event;
        this.screenshot = null;
        this.logMessage = null;
    }

    public TestStepActionEntry(Screenshot screenshot) {
        this.screenshot = screenshot;
        this.logMessage = null;
        this.clickPathEvent = null;
    }
}
