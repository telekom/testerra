/*
 * Testerra
 *
 * (C) 2020, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.GuiElementShadowRootPage;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

public class GuiElementShadowRootTest extends AbstractTestSitesTest {

    @Override
    protected TestPage getTestPage() {
        return TestPage.SHADOW_ROOT;
    }

    @Test
    public void testShadowRootVisibility() {

        TestStep.begin("Step 1 - Call shadow root page");
        final GuiElementShadowRootPage page = preparePage();

        TestStep.begin("Step 2 - Assert correct visibility");
        page.assertShadowRootVisibility();
    }

    @Test
    public void testShadowRootInput() {

        final String expectedText = "asserting your shadow";

        TestStep.begin("Step 1 - Call shadow root page");
        final GuiElementShadowRootPage page = preparePage();

        TestStep.begin("Step 2 - type '" + expectedText + "' to shadow root input");
        page.typeText(expectedText);

        TestStep.begin("Step 3 - assert '" + expectedText + "' is in displayed in shadow root input");
        page.assertInputText(expectedText);
    }

    private GuiElementShadowRootPage preparePage() {
        return PageFactory.create(GuiElementShadowRootPage.class, WebDriverManager.getWebDriver());
    }
}