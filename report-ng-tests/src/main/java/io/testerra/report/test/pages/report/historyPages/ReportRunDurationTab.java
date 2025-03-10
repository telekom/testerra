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
import io.testerra.report.test.pages.report.methodReport.ReportMethodHistoryTab;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class ReportRunDurationTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement durationHistoryChart = pageContent.find(By.xpath(".//echart[.//canvas]"));
    @Check
    protected final UiElement topLongestRunsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()='Longest runs']]"));
    private final UiElement topLongestRunsLabel = topLongestRunsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));
    @Check
    protected final UiElement topLongestTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()='Longest methods']]"));
    private final UiElement topLongestTestsLink = topLongestTestsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportRunDurationTab(WebDriver driver) {
        super(driver);
    }

    public List<String> getOrderListOfLongestRuns() {
        return topLongestRunsLabel.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public List<String> getOrderListOfLongestTests() {
        return topLongestTestsLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public ReportMethodHistoryTab clickOnLongestTest() {
        topLongestTestsLink.list().first().click();
        return createPage(ReportMethodHistoryTab.class);
    }
}
