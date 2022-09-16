/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import io.testerra.report.test.pages.AbstractReportPage;

public class ReportThreadsPage extends AbstractReportPage {

    @Check
    private final GuiElement testMethodSearchbar = pageContent.getSubElement(By.xpath("//label[@label='Method']//input"));
    @Check
    private final GuiElement testMethodDropDownList = pageContent.getSubElement(By.xpath("//div[./label[@label='Method']]//mdc-lookup"));
    @Check
    private final GuiElement testThreadReport = pageContent.getSubElement(By.xpath("//div[@class='vis-foreground']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportThreadsPage(WebDriver driver) {
        super(driver);
    }

    public ReportThreadsPage search(String filter) {
        testMethodSearchbar.type(filter);
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public ReportThreadsPage selectMethod(String method) {
        GuiElement methodAsGuiElement = testMethodDropDownList.getSubElement(By.xpath(String.format("//mdc-list-item[.//span[text()='%s']]", method)));
        methodAsGuiElement.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public void assertMethodBoxIsSelected(String method) {
        GuiElement subElement = testThreadReport.getSubElement(
                By.xpath("//div[contains(@class, 'vis-item') and contains(@class, 'vis-range') and .//div[text()='" + method + "']]"));

        // make list to avoid checking wrong entry, e.g. retried tests have multiple entries, which aren't distinguishable explicitly
        final List<GuiElement> list = subElement.getList();
        final boolean isSelected = list.stream().anyMatch(entry -> entry.getAttribute("class").contains("vis-selected"));
        Assert.assertTrue(isSelected, String.format("Method '%s' is selected in Threads Overview", method));
    }

    public ReportThreadsPage clickSearchBar(){
        testMethodSearchbar.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }
}
