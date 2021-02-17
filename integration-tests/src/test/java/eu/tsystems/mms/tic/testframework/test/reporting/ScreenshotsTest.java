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
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import java.util.Optional;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests if screenshots are added to the MethodContext when a test fails.
 * @author Mike Reiche
 */
public class ScreenshotsTest extends AbstractTestSitesTest implements PageFactoryTest {

    @Override
    public BasePage getPage() {
        return PAGE_FACTORY.createPage(BasePage.class, WEB_DRIVER_MANAGER.getWebDriver());
    }

    @Test()
    @Fails(description = "This test needs to fail to create a screenshot")
    public void test_take_screenshot_on_failure() {
        System.setProperty(Report.Properties.SCREENCASTER_ACTIVE.toString(), "true");
        getPage().assertIsTextDisplayed("Screenshot present on failure");
    }

    @Test(dependsOnMethods = "test_take_screenshot_on_failure", alwaysRun = true)
    public void test_screenshot_present_in_MethodContext() {
        this.screenshot_is_present_in_MethodContext("test_take_screenshot_on_failure");
    }

    @Test()
    @Fails(description = "This test needs to fail to create a screenshot")
    public void test_take_screenshot_on_failure_without_closing_WebDriver() {
        WEB_DRIVER_MANAGER.getConfig().setShutdownSessions(false);
        System.setProperty(Report.Properties.SCREENCASTER_ACTIVE.toString(), "true");
        getPage().assertIsTextDisplayed("Screenshot present on failure");
    }

    @Test(dependsOnMethods = "test_take_screenshot_on_failure_without_closing_WebDriver", alwaysRun = true)
    public void test_screenshot_present_in_MethodContext_without_closing_WebDriver() {
        this.screenshot_is_present_in_MethodContext("test_take_screenshot_on_failure_without_closing_WebDriver");
    }

    private void screenshot_is_present_in_MethodContext(String methodName) {
        MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();

        Optional<MethodContext> optionalMethodContext = currentMethodContext.getClassContext().readMethodContexts()
                .filter(methodContext -> methodContext.getName().equals(methodName))
                .findFirst();

        Assert.assertTrue(optionalMethodContext.isPresent());
        optionalMethodContext.ifPresent(methodContext -> {
            long count = methodContext.readTestSteps()
                    .flatMap(testStep -> testStep.getTestStepActions().stream())
                    .flatMap(testStepAction -> testStepAction.readEntries(Screenshot.class))
                    .count();
            Assert.assertEquals(count, 1);
        });
    }
}
