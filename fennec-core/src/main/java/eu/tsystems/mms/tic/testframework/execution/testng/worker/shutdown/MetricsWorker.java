/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;

import java.lang.reflect.Method;

/**
 * Created by pele on 30.01.2017.
 */
public class MetricsWorker extends GenerateReportsWorker {
    @Override
    public void run() {
                /*
         * check for metrics module and call the analyzer
         */
        Class<?> aClass = null;
        try {
            aClass = Thread.currentThread().getContextClassLoader()
                    .loadClass("eu.tsystems.mms.tic.testframework.metrics.Analyzer");
            Flags.METRICS_ACTIVE = true;
        } catch (ClassNotFoundException e) {
            LOGGER.trace("fennec Metrics module not in classpath. Skipping analysis.");
        }
        if (aClass != null) {
            // Calling this methods:
            // Analyzer analyzer = Analyzer.getInstance();
            // analyzer.analyze("src");
            try {
                Method method = aClass.getDeclaredMethod("getInstance");
                Object object = method.invoke(null);
                Method analyze = aClass.getDeclaredMethod("analyze", String.class);
                analyze.invoke(object, "src"); // call it on the source path
            } catch (Exception e) {
                throw new FennecSystemException("Internal Error", e);
            }
        }

    }
}
