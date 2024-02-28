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
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportThreadsPage extends AbstractReportPage {

    @Check
    private final UiElement testMethodSearchbar = pageContent.find(By.xpath("//label[@label='Method']//input"));
    @Check
    private final UiElement testMethodDropDownList = pageContent.find(By.xpath("//div[.//label[@label='Method']]//mdc-lookup"));
    @Check
    private final UiElement testThreadReport = pageContent.find(By.xpath("//echart[.//canvas]"));

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
        return createPage(ReportThreadsPage.class);
    }

    public ReportThreadsPage selectMethod(String method) {
        UiElement methodAsGuiElement = testMethodDropDownList.find(By.xpath(String.format("//mdc-list-item[.//span[starts-with(text(), '%s (')]]", method)));
        methodAsGuiElement.click();
        return createPage(ReportThreadsPage.class);
    }

    public void assertTooltipShown(String method) {
        UiElement tooltipHeader = testThreadReport.find(By.xpath("//div[@class='header']"));
        tooltipHeader.expect().text().contains(method).is(true);
    }

    public ReportThreadsPage clickSearchBar() {
        testMethodSearchbar.click();
        return createPage(ReportThreadsPage.class);
    }
}
