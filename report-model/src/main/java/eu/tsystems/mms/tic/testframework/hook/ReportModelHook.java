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
 */

package eu.tsystems.mms.tic.testframework.hook;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.listeners.GenerateXmlReportListener;
import eu.tsystems.mms.tic.testframework.report.TestStepLogAppender;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import org.apache.logging.log4j.core.Appender;

public class ReportModelHook implements ModuleHook {

    private Appender reportLogAppender;

    @Override
    public void init() {
        EventBus eventBus = TesterraListener.getEventBus();
        eventBus.register(new GenerateXmlReportListener());

        // Enable report formatter here
        this.reportLogAppender = new TestStepLogAppender();
        this.reportLogAppender.start();
        TesterraListener.getLoggerContext().getRootLogger().addAppender(this.reportLogAppender);
    }

    @Override
    public void terminate() {
        // Reset to default logger
        TesterraListener.getLoggerContext().getRootLogger().removeAppender(this.reportLogAppender);
    }
}
