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
 package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.TesterraEventUserDataManager;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.Formatter;

public class TestMethodFinishedWorker extends MethodWorker implements Loggable {

    final AssertionsCollector assertionsCollector = Testerra.injector.getInstance(AssertionsCollector.class);
    private final Formatter formatter = Testerra.injector.getInstance(Formatter.class);

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        if (isFailed()) {
            sb
                .append(TestStatusController.Status.FAILED.title)
                .append(" ")
                .append(formatter.toString(testMethod));
            log().error(sb.toString(), testResult.getThrowable());
        }
        else if (isSuccess()) {
            sb
                .append(TestStatusController.Status.PASSED.title)
                .append(" ")
                .append(formatter.toString(testMethod));
            log().info(sb.toString(), testResult.getThrowable());
        }
        else if (isSkipped()) {
            sb
                .append(TestStatusController.Status.SKIPPED.title)
                .append(" ")
                .append(formatter.toString(testMethod));
            log().warn(sb.toString(), testResult.getThrowable());
        }

        if (isTest()) {
            // clean thread local event user data
            TesterraEventUserDataManager.cleanupThreadLocalData();

            // cleanup thread locals from PropertyManager
            PropertyManager.clearThreadlocalProperties();

            // cleanup collected assertions
            assertionsCollector.clear();
        }
    }
}
