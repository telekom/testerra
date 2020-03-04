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

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

public class LogWDSessionsWorker extends MethodWorker {

    @Override
    public void run() {
        if (WebDriverManager.hasAnySessionActive()) {
            String executingSeleniumHost = WebDriverManager.getExecutingSeleniumHosts();
            if (executingSeleniumHost == null) {
                executingSeleniumHost = "unknown";
            }

            String msg = "";
            if (executingSeleniumHost.contains("\n")) {
                msg += "\n";
            }
            methodContext.infos.add(msg + executingSeleniumHost);
        }
    }
}
