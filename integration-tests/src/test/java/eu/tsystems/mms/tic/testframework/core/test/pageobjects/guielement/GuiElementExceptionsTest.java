/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class GuiElementExceptionsTest extends AbstractTestSitesTest {

    /**
     * created because isPresent threw an exception when the GuiElement had a not existing frame
     * thanks at maco for finding!
     */
    @Test
    public void testT01N_GuiElement_notExistingFrame() {
        WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement notExistingFrame = new GuiElement(driver, By.xpath("meNoExist"));
        GuiElement elementToSearch = new GuiElement(driver, By.xpath("thisDoesNotMatter"), notExistingFrame);

        try {
            elementToSearch.isPresent();
        } catch (Exception e) {
            Assert.fail("isPresent should never throw an exception.", e);
        }
    }
}
