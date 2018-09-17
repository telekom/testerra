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

import eu.tsystems.mms.tic.testframework.events.fennecEvent;
import eu.tsystems.mms.tic.testframework.events.fennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.fennecEventService;
import eu.tsystems.mms.tic.testframework.events.fennecEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;

import java.util.Date;

/**
 * Created by pele on 30.01.2017.
 */
public class fennecEventsWorker extends GenerateReportsWorker {
    @Override
    public void run() {
        // fire event
        long startTime = ExecutionContextController.RUN_CONTEXT.startTime.getTime();
        long endTime = new Date().getTime();
        fennecEventService.getInstance().fireEvent(new fennecEvent(fennecEventType.TEST_END)
                .addUserData()
                .addData(fennecEventDataType.TIMESTAMP, endTime)
                .addData(fennecEventDataType.DURATION, endTime - startTime)
        );
    }
}
