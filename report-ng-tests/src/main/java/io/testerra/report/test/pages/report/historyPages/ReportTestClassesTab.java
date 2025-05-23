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
import eu.tsystems.mms.tic.testframework.report.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportTestClassesTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement testClassSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Class']"));
    @Check
    private final UiElement testStatusSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Status']"));
    @Check
    private final UiElement classesHistoryCard = pageContent.find(By.xpath(".//mdc-card[contains(@class, 'classes-history-card')]"));
    @Check
    private final UiElement cardHeadline = classesHistoryCard.find(By.xpath(".//div[contains(@class, 'card-headline')]"));
    @Check
    private final UiElement classesHistoryChart = classesHistoryCard.find(By.xpath(".//echart[.//canvas]"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportTestClassesTab(WebDriver driver) {
        super(driver);
    }

    public void assertClassesHistoryChartMatchesScreenshot(double pixelDistance, String referenceImageName) {
        this.classesHistoryChart.expect().screenshot().pixelDistance(referenceImageName).isLowerThan(pixelDistance);
    }

    public void assertSelectedClass(String className) {
        testClassSelect.expect().text().contains(className).is(true);
        assertCardHeadlineContainsText(className);
    }

    public void assertCardHeadlineContainsText(String text) {
        cardHeadline.expect().text().contains(text).is(true);
    }

    public ReportTestClassesTab selectClassName(String label) {
        return selectDropBoxElement(this.testClassSelect, label, ReportTestClassesTab.class);
    }

    public ReportTestClassesTab selectTestStatus(Status status) {
        return selectDropBoxElement(this.testStatusSelect, status.title, ReportTestClassesTab.class);
    }
}
