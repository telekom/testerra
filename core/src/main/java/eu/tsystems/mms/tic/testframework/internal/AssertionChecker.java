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
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pele
 * Date: 23.07.13
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public final class AssertionChecker {

    /**
     * Constructor.
     */
    private AssertionChecker() {
    }

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionChecker.class);

    /**
     * utility method that checks is called from NonFunctionalAssert. It stores the Throwable, creates
     * a screenshot if possible and lets the test continue.
     *
     * @param throwable .
     * @throws Throwable .
     */
    @Deprecated
    public static void check(final Throwable throwable) throws Throwable {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            if ("eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert".equals(className)) {

                return;
            }
        }
        /*
        else
         */
        throw throwable;
    }

    public static void storeNonFunctionalInfo(Throwable throwable) {
        LOGGER.error("Found non-functional error", throwable);
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            // add nf info
            AssertionInfo assertionInfo = methodContext.addNonFunctionalInfo(throwable);

            // get screenshots and videos
            List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();
            if (screenshots != null) {
                screenshots.forEach(s -> s.errorContextId = assertionInfo.id);
                assertionInfo.screenshots.addAll(screenshots);
            }

            // not collecting a video here
        }
    }
}
