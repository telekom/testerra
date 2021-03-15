/*
 * Testerra
 *
 * (C) 2020, Alex Rockstroh, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodAssertionsPage extends MethodDetailsPage {

    @Check
    private GuiElement headLine = new GuiElement(this.getWebDriver(), By.xpath("//h5[text()='Collected Assertions']"), mainFrame);
    private String LOCATOR_ASSERTION_HEADLINE = "//a[contains(text(), '%s')]";
    private String LOCATOR_ASSERTION_DESCRIPTION = "//a[contains(text(), '%s')]/..//pre";

    public MethodAssertionsPage(WebDriver driver) {
        super(driver);
    }

    public void checkAssertionIsDisplayedCorrectly(String assertionTitle, String assertionDescription){
        GuiElement headline = new GuiElement(this.getWebDriver(), By.xpath(String.format(LOCATOR_ASSERTION_HEADLINE, assertionTitle)), mainFrame);
        headline.asserts("The headline of the assertion should be displayed.").assertIsDisplayed();
        headline.asserts("The text of the headline of the assertion should be correct.").assertTextContains(assertionTitle);

        openAssertionDescriptionMenu(headline);
        GuiElement description = new GuiElement(this.getWebDriver(), By.xpath(String.format(LOCATOR_ASSERTION_DESCRIPTION, assertionTitle)), mainFrame);
        description.asserts("The text of the assert description should be correct.").assertTextContains(assertionDescription);
    }

    public void openAssertionDescriptionMenu(GuiElement assertionElement) {
        assertionElement.click();
    }
}
