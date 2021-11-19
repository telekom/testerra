/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.test.execution.TestStatusTest;
import eu.tsystems.mms.tic.testframework.test.page.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.io.IOException;
import java.util.Optional;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

/**
 * Tests if screenshots are added to the MethodContext when a test fails.
 * @author Mike Reiche
 */
public class ScreenshotsTest extends AbstractTestSitesTest implements PageFactoryTest, TestStatusTest {

    @Override
    public BasePage getPage() {
        return PageFactory.create(BasePage.class, WebDriverManager.getWebDriver());
    }

    @Test()
    @Fails()
    public void test_takeScreenshot_fails() {
        Flags.SCREENCASTER_ACTIVE = true;
        getPage().assertIsTextDisplayed("Screenshot present on failure");
    }

    @Test(dependsOnMethods = "test_takeScreenshot_fails", alwaysRun = true)
    public void test_screenshotPresentInMethodContext() {
        this.screenshotIsPresentInMethodContext("test_takeScreenshot_fails");
    }

    @Test()
    @Fails()
    public void test_takeScreenshotOnExclusiveSession_fails() {
        WebDriverManager.getConfig().setShutdownSessions(false);
        Flags.SCREENCASTER_ACTIVE = true;
        getPage().assertIsTextDisplayed("Screenshot present on failure");
    }

    @Test(dependsOnMethods = "test_takeScreenshotOnExclusiveSession_fails", alwaysRun = true)
    public void test_exclusiveSessionScreenshotPresentInMethodContext() {
        this.screenshotIsPresentInMethodContext("test_takeScreenshotOnExclusiveSession_fails");
        WebDriverManager.getConfig().reset();
        WebDriverManager.forceShutdownAllThreads();
    }

    private void screenshotIsPresentInMethodContext(String methodName) {
        Optional<MethodContext> optionalMethodContext = findMethodContexts(methodName).findFirst();
        Assert.assertTrue(optionalMethodContext.isPresent());
        optionalMethodContext.ifPresent(methodContext -> {
            long count = methodContext.readTestSteps()
                    .flatMap(testStep -> testStep.getTestStepActions().stream())
                    .flatMap(testStepAction -> testStepAction.readEntries(Screenshot.class))
                    .filter(screenshot -> !screenshot.getMetaData().getOrDefault(Screenshot.MetaData.SESSION_KEY, "").startsWith("EXCLUSIVE_"))
                    .count();
            Assert.assertEquals(count, 1, "Screenshots in MethodContext " + methodName);
        });
    }

    @Test()
    @Fails()
    public void test_takeScreenshotViaCollectedAssertion_fails() {
        Flags.SCREENCASTER_ACTIVE = true;
        AssertCollector.assertTrue(false);
    }

    @Test(dependsOnMethods = "test_takeScreenshotViaCollectedAssertion_fails", alwaysRun = true)
    public void test_collectedAssertionScreenshotIsPresentInMethodContext() {
        this.screenshotIsPresentInMethodContext("test_takeScreenshotViaCollectedAssertion_fails");
    }

    @Test
    public void test_DOMSource() throws IOException {
        WebTestPage page = new WebTestPage(WebDriverManager.getWebDriver());

        for (int s = 0; s < 3; ++s) {
            page.getOpenAgain().click();
        }
        Screenshot screenshot = UITestUtils.takeScreenshot(page.getWebDriver(), false);
        String screenshotSource = Files.readFile(screenshot.getPageSourceFile().get());

        String expected = "<p id=\"99\">Open again clicked<br>Open again clicked<br>Open again clicked<br>";
        AssertUtils.assertContains(screenshotSource, expected);
    }
}
