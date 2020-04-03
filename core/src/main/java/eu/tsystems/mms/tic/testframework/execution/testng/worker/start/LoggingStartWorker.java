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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class LoggingStartWorker extends MethodWorker implements Loggable {

    @Override
    public void run() {
        String testClassName = testMethod.getTestClass().getName();

        if (isTest()) {
            /*
             * Test method
             */

            log().info("Starting test method: " + testClassName + "." + methodName);
        } else {
            /*
             * Configuration methods
             */

            log().info("Starting configuration method: " + testClassName + "." + methodName);
        }

    }
}
