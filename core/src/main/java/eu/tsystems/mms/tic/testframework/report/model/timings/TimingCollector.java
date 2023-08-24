/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report.model.timings;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2023-08-23
 *
 * @author mgn
 */
public class TimingCollector implements Loggable {

    public enum SessionTiming {
        BASEURL, SESSION_START;
    }

    private Map<SessionContext, Map<SessionTiming, StopWatch>> sessionTimings = new ConcurrentHashMap<>();

    private static TimingCollector instance;

    private TimingCollector() {

    }

    public static TimingCollector get() {
        if (instance == null) {
            instance = new TimingCollector();
        }
        return instance;
    }

    public void start(SessionContext sessionContext, SessionTiming timing) {
        StopWatch stopWatch = new StopWatch();
        this.addSessionContextTiming(sessionContext, timing, stopWatch);
        stopWatch.start();
    }

    public void stop(SessionContext sessionContext, SessionTiming timing) {
        Map<SessionTiming, StopWatch> entry = this.sessionTimings.get(sessionContext);
        if (entry != null) {
            entry.get(timing).stop();
        } else {
            log().warn("Cannot stop time: There is no entry for {} - {}", sessionContext.getSessionKey(), timing.toString());
        }
    }

    // TODO: Check if it should be an optional
    public StopWatch getSessionTiming(SessionContext context, SessionTiming timing) {
        Map<SessionTiming, StopWatch> entry = this.sessionTimings.get(context);
        if (entry != null) {
            return entry.get(timing);
        }
        return null;
    }

    private void addSessionContextTiming(SessionContext sessionContext, SessionTiming timing, StopWatch stopWatch) {
        Map<SessionTiming, StopWatch> mapEntry;
        if (this.sessionTimings.containsKey(sessionContext)) {
            mapEntry = this.sessionTimings.get(sessionContext);
        } else {
            mapEntry = new HashMap<>();
            this.sessionTimings.put(sessionContext, mapEntry);
        }
        mapEntry.put(timing, stopWatch);
    }


}
