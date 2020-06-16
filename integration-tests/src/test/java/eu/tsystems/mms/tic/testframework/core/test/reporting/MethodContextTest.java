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
 package eu.tsystems.mms.tic.testframework.core.test.reporting;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.StackTrace;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.testng.annotations.Test;

public class MethodContextTest extends AbstractWebDriverTest {

    String level0String = "level 0";

    RuntimeException level3 = new RuntimeException("level 3");
    RuntimeException level2 = new RuntimeException("level 2", level3);
    RuntimeException level1 = new RuntimeException("level 1", level2);
    RuntimeException level0 = new RuntimeException(level0String, level1);

    private RuntimeException getStackedThrowable() {
        return level0;
    }

    @Test
    public void testT01_SetThrowable() throws Exception {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        methodContext.errorContext().setThrowable(null, getStackedThrowable());

        StackTrace stackTrace = methodContext.errorContext().getStackTrace();

        String errorMessage = methodContext.errorContext().getReadableErrorMessage();
        AssertCollector.assertTrue(errorMessage.contains(level0String), "error message contains " + level0String);

        String[] toCheck = new String[]{
                level1.toString(),
                level2.toString(),
                level3.toString(),
        };

        String stackTraceString = stackTrace.toString();
        for (String s : toCheck) {
            AssertCollector.assertTrue(stackTraceString.contains(s), "stack trace contains " + s);
        }
    }
}
