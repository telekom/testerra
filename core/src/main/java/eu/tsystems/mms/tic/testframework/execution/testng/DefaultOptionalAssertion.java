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

package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.List;

/**
 * Default implementation of {@link OptionalAssertion}
 */
public class DefaultOptionalAssertion extends AbstractAssertion implements
        OptionalAssertion,
        Loggable
{
    @Override
    public void fail(Error error) {
        log().warn("Failed optional assertion: " + error.getMessage());
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            // add nf info
            methodContext.addOptionalAssertion(error);

            // get screenshots and videos
            List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();
            if (screenshots != null) {
                methodContext.addScreenshots(screenshots.stream());
            }

            // not collecting a video here
        }
    }
}
