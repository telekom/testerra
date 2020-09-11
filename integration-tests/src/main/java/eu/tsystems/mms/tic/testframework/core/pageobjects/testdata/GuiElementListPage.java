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
 *
 */
package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GuiElementListPage extends Page {
    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public GuiElementListPage(WebDriver driver) {
        super(driver);
    }

    public GuiElement getNavigation() {
        return new GuiElement(this.getWebDriver(), Locate.byQa("section/navigation"));
    }

    public GuiElement getTable() {
        return new GuiElement(this.getWebDriver(), Locate.byQa("section/table"));
    }

    public GuiElement getNavigationSubElementsByTagName() {
        return getNavigation().getSubElement(By.tagName("a"));
    }

    public GuiElement getNavigationSubElementsByChildrenXPath() {
        return getNavigation().getSubElement(By.xpath("./a"));
    }

    public GuiElement getNavigationSubElementsByDescendantsXPath() {
        return getNavigation().getSubElement(By.xpath(".//a"));
    }

    public GuiElement getNavigationSubElementsByAbsoluteChildrenXPath() {
        return new GuiElement(this.getWebDriver(), By.xpath("//nav[@data-qa='section/navigation']/a"));
    }

    public GuiElement getNavigationSubElementsByAbsoluteDescendantsXPath() {
        return new GuiElement(this.getWebDriver(), By.xpath("//nav[@data-qa='section/navigation']//a"));
    }

    public GuiElement getTableRowsByTagName() {
        return getTable().getSubElement(By.tagName("tr"));
    }
}
