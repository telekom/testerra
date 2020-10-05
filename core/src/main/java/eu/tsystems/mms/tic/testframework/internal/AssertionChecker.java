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

import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: pele
 * Date: 23.07.13
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 * @todo Move this code to {@link DefaultNonFunctionalAssertion}
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

    public static void storeNonFunctionalInfo(Throwable throwable) {
        LOGGER.warn("Found non-functional error", throwable);
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            // add nf info
            AssertionInfo assertionInfo = methodContext.addNonFunctionalInfo(throwable);

            // get screenshots and videos
            List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();
            methodContext.addScreenshots(screenshots.stream().peek(screenshot -> screenshot.setErrorContextId(assertionInfo.id)));
        }
    }
}
