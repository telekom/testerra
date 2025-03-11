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

public class ReportTestRunTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement testHistoryChart = pageContent.find(By.xpath(".//test-history-chart//echart[.//canvas]"));
    @Check
    private final UiElement testStatusSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Status']"));
    @Check
    protected final UiElement historyStatisticsCard = pageContent.find(By.xpath(".//history-statistics-card"));
    @Check
    private final UiElement statusShareChart = pageContent.find(By.xpath(".//status-share-chart//echart[.//canvas]"));
    @Check
    protected final UiElement topFlakyTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()='Top 3 flaky tests']]"));
    private final UiElement topFlakyTestsLink = topFlakyTestsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));
    @Check
    protected final UiElement topFailingTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()='Top 3 failing tests']]"));
    private final UiElement topFailingTestsLink = topFailingTestsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportTestRunTab(WebDriver driver) {
        super(driver);
    }

    private By getXpathToStatistics(String statisticsName) {
        final String xPathToStatisticsItemTemplate = ".//mdc-list-item[.//span[text()='%s']]";
        return By.xpath(String.format(xPathToStatisticsItemTemplate, statisticsName));
    }

    public void assertHistoryChartMatchesScreenshot(double pixelDistance) {
        this.testHistoryChart.expect().screenshot().pixelDistance("test_history_chart").isLowerThan(pixelDistance);
    }

    public List<String> getOrderListOfTopFlakyTests() {
        return topFlakyTestsLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public List<String> getOrderListOfTopFailingTests() {
        return topFailingTestsLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public void checkStatistics(String statisticsName, String value) {
        final UiElement testsStatusElement = historyStatisticsCard.find((getXpathToStatistics(statisticsName)));
        testsStatusElement.expect().text().contains(value).is(true);
    }

    public ReportMethodHistoryTab clickOnTopFlakyTest() {
        topFlakyTestsLink.list().first().click();
        return createPage(ReportMethodHistoryTab.class);
    }
}
