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

package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportMethodHistoryTab extends AbstractReportMethodPage {
    @Check
    protected final UiElement methodHistoryChart = pageContent.find(By.xpath(".//method-history-chart"));
    @Check
    protected final UiElement statusShareChart = pageContent.find(By.xpath(".//status-share-chart//echart[.//canvas]"));
    @Check
    protected final UiElement historyStatisticsCard = pageContent.find(By.xpath(".//history-statistics-card"));
    @Check
    protected final UiElement failureAspectsChart = pageContent.find(By.xpath(".//failure-aspects-chart"));

    public ReportMethodHistoryTab(WebDriver driver) {
        super(driver);
    }

    private By getXpathToStatistics(String statisticsName) {
        final String xPathToStatisticsItemTemplate = ".//mdc-list-item[.//span[text()='%s']]";
        return By.xpath(String.format(xPathToStatisticsItemTemplate, statisticsName));
    }

    public void assertMethodHistoryChartMatchesScreenshot(double pixelDistance) {
        this.methodHistoryChart.expect().screenshot().pixelDistance("method_history_chart").isLowerThan(pixelDistance);
    }

    public void assertStatusShareChartMatchesScreenshot(double pixelDistance) {
        failureAspectsChart.scrollIntoView();
        this.statusShareChart.expect().screenshot().pixelDistance("status_share_chart").isLowerThan(pixelDistance);
    }

    public void assertFailureAspectsChartMatchesScreenshot(double pixelDistance) {
        failureAspectsChart.scrollIntoView();
        this.failureAspectsChart.expect().screenshot().pixelDistance("failure_aspects_chart").isLowerThan(pixelDistance);
    }

    public void checkStatistics(String statisticsName, String value) {
        final UiElement testsStatusElement = historyStatisticsCard.find((getXpathToStatistics(statisticsName)));
        testsStatusElement.expect().text().contains(value).is(true);
    }
}
