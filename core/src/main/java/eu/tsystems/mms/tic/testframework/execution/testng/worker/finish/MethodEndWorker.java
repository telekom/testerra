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

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import eu.tsystems.mms.tic.testframework.utils.SourceUtils;
import java.util.Map;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class MethodEndWorker implements MethodEndEvent.Listener, Loggable {

    private final Formatter formatter = Testerra.injector.getInstance(Formatter.class);

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        // clear current result
        ExecutionContextController.clearCurrentTestResult();
        MethodContext methodContext = event.getMethodContext();
        ITestResult testResult = event.getTestResult();
        ITestNGMethod testMethod = event.getTestMethod();

        StringBuilder sb = new StringBuilder();
        if (event.isFailed()) {
            sb
                    .append(TestStatusController.Status.FAILED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().error(sb.toString(), testResult.getThrowable());
        }
        else if (event.getTestResult().isSuccess()) {
            sb
                    .append(TestStatusController.Status.PASSED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().info(sb.toString(), testResult.getThrowable());
        }
        else if (event.isSkipped()) {
            sb
                    .append(TestStatusController.Status.SKIPPED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().warn(sb.toString(), testResult.getThrowable());
        }

        if (testMethod.isTest()) {
            // cleanup thread locals from PropertyManager
            PropertyManager.clearThreadlocalProperties();
            Testerra.injector.getInstance(AssertionsCollector.class).clear();
        }

        try {
            /*
             * Read stored method infos, publish to method container and clean
             */
            ReportInfo.MethodInfo methodInfo = ReportInfo.getCurrentMethodInfo();
            if (methodInfo != null) {
                Map<String, String> infos = methodInfo.getInfos();
                for (String key : infos.keySet()) {
                    methodContext.infos.add(key + " = " + infos.get(key));
                }
            }

            // calculate fingerprint
            if (event.isFailed()) {
                Throwable throwable = methodContext.errorContext().getThrowable();
                if (throwable != null) {
                    // look for script source
                    ScriptSource scriptSourceForThrowable = SourceUtils.findScriptSourceForThrowable(throwable);
                    if (scriptSourceForThrowable != null) {
                        methodContext.errorContext().scriptSource = scriptSourceForThrowable;
                    }
                    methodContext.errorContext().executionObjectSource = TestEvidenceCollector.getSourceFor(throwable);
                }
                methodContext.errorContext().buildExitFingerprint();
            }


        } finally {
            TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(event.getMethodContext()));

            // clear method infos
            ReportInfo.clearCurrentMethodInfo();

            // gc
            //System.gc();
        }

    }
}
