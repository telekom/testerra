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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ReportUtils;
import eu.tsystems.mms.tic.testframework.utils.SourceUtils;

import java.util.Map;

/**
 * Created by pele on 19.01.2017.
 */
public class MethodFinishedWorker extends MethodWorker {


    @Override
    public void run() {

        // clear current result
        ExecutionContextController.clearCurrentTestResult();

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
            if (isFailed()) {
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

            // generate html
            try {
                ReportUtils.createMethodDetailsStepsView(methodContext);
            } catch (Throwable e) {
                LOGGER.error("FATAL: Could not create html", e);
            }
        } finally {

            // Fire CONTEXT UPDATE EVENT.
            TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.CONTEXT_UPDATE)
                    .addUserData()
                    .addData(TesterraEventDataType.CONTEXT, methodContext));

            // clear method infos
            ReportInfo.clearCurrentMethodInfo();

            // gc
            System.gc();
        }

    }
}
