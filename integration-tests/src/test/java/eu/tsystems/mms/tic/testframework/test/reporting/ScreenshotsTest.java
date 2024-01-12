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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import io.testerra.test.pretest_status.TestStatusTest;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Tests if screenshots are added to the MethodContext when a test fails.
 *
 * @author Mike Reiche
 */
public class ScreenshotsTest extends AbstractTestSitesTest implements PageFactoryTest, TestStatusTest {

    @Override
    public BasePage getPage() {
        return PAGE_FACTORY.createPage(BasePage.class, getWebDriver());
    }

    @Test
    public void test_DOMSource() throws IOException {
        WebTestPage page = new WebTestPage(getWebDriver());

        for (int s = 0; s < 3; ++s) {
            page.getOpenAgain().click();
        }
        Screenshot screenshot = UITestUtils.takeScreenshot(page.getWebDriver(), false);
        String screenshotSource = Files.readFile(new FileInputStream(screenshot.getPageSourceFile().get()));

        String expected = "<p id=\"99\">Open again clicked<br>Open again clicked<br>Open again clicked<br>";
        ASSERT.assertContains(screenshotSource, expected);
    }

    @Test
    public void testT01_takeScreenshotTwice() {

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();

        UITestUtils.takeScreenshot(webDriver, true);
        UITestUtils.takeScreenshot(webDriver, true);

        Assert.assertEquals(readScreenshots(ExecutionContextController.getMethodContextForThread().get()).count(), 2);
    }

    private Stream<Screenshot> readScreenshots(MethodContext methodContext) {
        return methodContext.readTestSteps().flatMap(testStep -> testStep.getCurrentTestStepAction().readEntries(Screenshot.class));
    }
}
