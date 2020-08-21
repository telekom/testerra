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
 package eu.tsystems.mms.tic.testframework.hook;

import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.listener.GenerateReportListener;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.worker.GenerateTesterraReportWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportHook implements ModuleHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHook.class);

    @Override
    public void init() {
        // listener on report event
        TesterraEventService.addListener(new GenerateReportListener());
        // init GenerateReport
        TesterraListener.registerGenerateReportsWorker(GenerateTesterraReportWorker.class);
    }

    @Override
    public void terminate() {
        // anything to do here??
    }
}
