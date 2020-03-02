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
package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

public class ConditionalBehaviourWorker extends MethodWorker {

    @Override
    public void run() {
        if (isTest() && isFailed()) {

            // check state condition: shutdown
            boolean skipShutdown = PropertyManager.getBooleanProperty(
                    TesterraProperties.ON_STATE_TESTFAILED_SKIP_SHUTDOWN, false);
            if (skipShutdown) {
                LOGGER.debug("ON_STATE_TESTFAILED_SKIP_SHUTDOWN: true");
                // leave all windows open when this condition is true (except you call forceShutdown)
                WebDriverManager.config().executeCloseWindows = false;
            }

            // check state condition: skip test methods
            boolean skipFollowingTests = PropertyManager.getBooleanProperty(
                    TesterraProperties.ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS, false);
            if (skipFollowingTests) {
                LOGGER.info(TesterraProperties.ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS + " : true, skipping all tests from now on");
                TesterraListener.skipAllTests();
            }
        }

    }
}
