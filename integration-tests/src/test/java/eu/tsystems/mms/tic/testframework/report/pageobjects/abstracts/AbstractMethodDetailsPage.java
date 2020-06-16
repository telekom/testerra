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
package eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStackPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStepsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractMethodDetailsPage extends AbstractReportPage {

    protected GuiElement mainFrame = new GuiElement(this.getWebDriver(), By.cssSelector("frame[name='main']"));
    /**
     * Buttons on Top
     */
    protected GuiElement backButton = new GuiElement(this.getWebDriver(), By.xpath("//div[@class='detailsmenu']"), mainFrame);
    protected GuiElement detailsButton = new GuiElement(this.getWebDriver(), By.id("buttondetails"), mainFrame);
    protected GuiElement stepsButton = new GuiElement(this.getWebDriver(), By.id("buttondlogs"), mainFrame);
    protected GuiElement stackButton = new GuiElement(this.getWebDriver(), By.id("buttondstack"), mainFrame);
    protected GuiElement minorErrorButton = new GuiElement(this.getWebDriver(), By.id("buttonminor"), mainFrame);

    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public AbstractMethodDetailsPage(WebDriver driver) {
        super(driver);
        driver.switchTo().defaultContent();
        driver.switchTo().frame(0);
    }

    /**
     * click Buttons on Top
     *
     * @return DashboardPage
     */
    public DashboardPage gotoDashboardPageByClickingBackButton() {
        backButton.click();
        return PageFactory.create(DashboardPage.class, this.getWebDriver());
    }

    public MethodDetailsPage gotoMethodDetailsPage() {
        detailsButton.click();
        return PageFactory.create(MethodDetailsPage.class, this.getWebDriver());
    }

    public MethodStepsPage gotoMethodStepsPage() {
        stepsButton.click();
        return PageFactory.create(MethodStepsPage.class, this.getWebDriver());
    }

    public MethodStackPage gotoMethodStackPage() {
        stackButton.click();
        return PageFactory.create(MethodStackPage.class, this.getWebDriver());
    }
}
