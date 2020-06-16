/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodMinorErrorsPage extends MethodDetailsPage {

    GuiElement assertion = new GuiElement(this.getWebDriver(), By.cssSelector(".standardTable.tr>td>a"), mainFrame);
    GuiElement assertionMessage = new GuiElement(this.getWebDriver(), By.cssSelector("#non-functional-exception-0>div"), mainFrame);

    public MethodMinorErrorsPage(WebDriver driver) {
        super(driver);
        checkPage();
    }

    public GuiElement getAssertion() {
        return assertion;
    }

    public MethodMinorErrorsPage clickAssertion() {
        assertion.click();
        return PageFactory.create(MethodMinorErrorsPage.class, this.getWebDriver());
    }

    public String getAssertionMessage() {
        return assertionMessage.getText();
    }
}
