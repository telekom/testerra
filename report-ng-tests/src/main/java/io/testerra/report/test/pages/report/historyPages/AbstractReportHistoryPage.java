/*
 * Testerra
 *
 * (C) 2025, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages.report.historyPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AbstractReportHistoryPage extends AbstractReportPage {
    @Check
    protected final UiElement historyTabBar = pageContent.find(By.xpath(".//mdc-tab-bar"));
    @Check
    private final UiElement historyTestRunTab = historyTabBar.find(By.xpath(".//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Test run')]]"));
    @Check
    private final UiElement historyRunDurationTab = historyTabBar.find(By.xpath(".//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Run duration')]]"));
    @Check
    private final UiElement historyTestClassesTab = historyTabBar.find(By.xpath(".//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Test classes')]]"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public AbstractReportHistoryPage(WebDriver driver) {
        super(driver);
    }

    public ReportTestRunTab navigateToTestRunTab() {
        historyTestRunTab.click();
        return createPage(ReportTestRunTab.class);
    }

    public ReportRunDurationTab navigateToRunDurationTab() {
        historyRunDurationTab.click();
        return createPage(ReportRunDurationTab.class);
    }

    public ReportTestClassesTab navigateToTestClassesTab() {
        historyTestClassesTab.click();
        return createPage(ReportTestClassesTab.class);
    }
}
