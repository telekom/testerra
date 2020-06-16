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
 package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.CustomContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;

import java.util.LinkedList;
import java.util.List;

public final class CollectedAssertions implements AssertionsCollector {

    private final ThreadLocal<List<AssertionInfo>> ASSERTION_INFOS = new ThreadLocal<>();

    public synchronized boolean store(Throwable throwable) {
        if (ASSERTION_INFOS.get() == null) {
            ASSERTION_INFOS.set(new LinkedList<>());
        }

        List<AssertionInfo> assertionInfos = ASSERTION_INFOS.get();

        /*
        add info
         */
        AssertionInfo assertionInfo = new AssertionInfo(throwable);

        MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();

        // take scrennshots
        List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();
        if (screenshots != null) {
            screenshots.forEach(s -> s.errorContextId = assertionInfo.id);
            currentMethodContext.screenshots.addAll(screenshots);
        }

        // get custom error contexts in queue
        List<CustomContext> customContexts = ExecutionContextController.getCurrentMethodContext().customContexts;
        currentMethodContext.customContexts.addAll(customContexts);
        customContexts.clear();

        // and store
        return assertionInfos.add(assertionInfo);
    }

    public void clear() {
        ASSERTION_INFOS.remove();
    }
    public boolean hasEntries() {
        if (ASSERTION_INFOS.get() == null) {
            return false;
        }
        if (ASSERTION_INFOS.get().size() > 0) {
            return true;
        }
        return false;
    }
    public List<AssertionInfo> getEntries() {
        return ASSERTION_INFOS.get();
    }
}
