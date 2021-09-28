/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.stream.Stream;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test {@link eu.tsystems.mms.tic.testframework.utils.UITestUtils}
 * <p>
 * Date: 04.08.2020
 * Time: 15:13
 *
 * @author Eric Kubenka
 */
public class UITestUtilsTest extends AbstractTestSitesTest {

    /**
     * Tests #1063
     */
    @Test
    public void testT01_takeScreenshotTwice() {

        WebDriver webDriver = WebDriverManager.getWebDriver();

        UITestUtils.takeScreenshot(webDriver, true);
        UITestUtils.takeScreenshot(webDriver, true);

        Assert.assertEquals(readScreenshots(ExecutionContextController.getCurrentMethodContext()).count(), 2);
    }

    private Stream<Screenshot> readScreenshots(MethodContext methodContext) {
        return methodContext.readTestSteps().flatMap(testStep -> testStep.getCurrentTestStepAction().readEntries(Screenshot.class));
    }

    @Test
    public void test_takeScreenshotOnExclusiveSession() {
        WebDriver webDriver = WebDriverManager.getWebDriver();
        WebDriverManager.makeSessionExclusive(webDriver);

        UITestUtils.takeScreenshot(webDriver, true);

        Assert.assertEquals(readScreenshots(ExecutionContextController.getCurrentMethodContext()).count(), 1);
    }

}
