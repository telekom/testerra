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
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.CustomErrorContextObject;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pele on 17.10.2016.
 */
public final class CollectedAssertions {

    private static final Logger LOGGER = LoggerFactory.getLogger("AssertCollector");
    private static final ThreadLocal<List<AssertionInfo>> ASSERTION_INFOS = new ThreadLocal<>();

    public synchronized static void store(Throwable throwable) {
        if (ASSERTION_INFOS.get() == null) {
            ASSERTION_INFOS.set(new LinkedList<>());
        }

        List<AssertionInfo> assertionInfos = ASSERTION_INFOS.get();

        /*
        add info
         */
        AssertionInfo assertionInfo = new AssertionInfo(throwable);

        // take scrennshots
        List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();
        if (screenshots != null) {
            screenshots.forEach(s -> s.errorContextId = assertionInfo.id);
            assertionInfo.screenshots.addAll(screenshots);
        }

        // get custom error contexts in queue
        List<CustomErrorContextObject> customErrorContextObjects = ExecutionContextController.getCurrentMethodContext().customErrorContextObjects;
        assertionInfo.customErrorContextObjects.addAll(customErrorContextObjects);
        customErrorContextObjects.clear();

        // and store
        assertionInfos.add(assertionInfo);
    }

    public static void clear() {
        ASSERTION_INFOS.remove();
    }

    public static boolean hasEntries() {
        if (ASSERTION_INFOS.get() == null) {
            return false;
        }
        if (ASSERTION_INFOS.get().size() > 0) {
            return true;
        }
        return false;
    }

    public static List<AssertionInfo> getEntries() {
        return ASSERTION_INFOS.get();
    }

}
