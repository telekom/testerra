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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class MethodParametersWorker implements MethodStartEvent.Listener {

    @Override
    @Subscribe
    public void onMethodStart(MethodStartEvent event) {
        ITestNGMethod testMethod = event.getTestMethod();
        ITestResult testResult = event.getTestResult();
        if (testMethod.isTest()) {
            Object[] parameters = testResult.getParameters();
            if (parameters != null && parameters.length > 0) {
                event.getMethodContext().parameters = Arrays.stream(parameters).collect(Collectors.toList());
            }
        }
        else {
            /*
             * Config methods: log warning when injected method is missing
             */
            if (testMethod.isBeforeMethodConfiguration() || testMethod.isAfterMethodConfiguration()) {
                // check for method injection
                ExecutionContextUtils.checkForInjectedMethod(testResult, event.getTestContext());
            }
        }
    }
}
