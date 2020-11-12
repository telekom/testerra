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

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import java.io.File;
import java.net.URL;

public class CopyReportAppListener implements FinalizeExecutionEvent.Listener, Loggable {
    private File targetDir;

    public CopyReportAppListener(File targetDir) {
        this.targetDir = targetDir;
    }

    @Subscribe
    @Override
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        try {
            URL resource = getClass().getClassLoader().getResource("report-ng");
            if (resource == null) {
                throw new Exception("App resource doesn't exists");
            }
            File resourceDir = new File(resource.getPath());
            FileUtils.copyDirectory(resourceDir, this.targetDir);
        } catch (Exception e) {
            log().error("Unable to copy app resource", e);
        }
    }
}
