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
import eu.tsystems.mms.tic.testframework.pageobjects.PreparedLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.report.methodReport.ReportMethodHistoryTab;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ReportRunComparisonTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement testExecutionSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Compare latest run with']"));
    @Check
    protected final UiElement runOverviewCardOfPreviousRun = pageContent.find(By.xpath("(//run-overview-card)[1]"));
    @Check
    private final UiElement runOverviewCardOfCurrentRun = pageContent.find(By.xpath("(//run-overview-card)[2]"));

    PreparedLocator preparedMethodLinkLocator = LOCATE.prepare("//a[contains(@route-href, 'method') and text()='%s']");
    PreparedLocator preparedClassLinkLocator = LOCATE.prepare("//a[contains(@route-href, 'class') and text()='%s']");
    private final UiElement tableHead = pageContent.find(By.xpath(".//thead"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportRunComparisonTab(WebDriver driver) {
        super(driver);
    }

    public enum ComparisonTableEntry {
        PREVIOUS_STATUS(0), CURRENT_STATUS(1), CLASS(2), METHOD(3);

        private final int value;

        ComparisonTableEntry(final int value) {
            this.value = value;
        }

        public int index() {
            return this.value;
        }
    }

    private UiElement getHeaderRow(ComparisonTableEntry tableEntry) {

        String headerRowLocator = ".//tr";

        switch (tableEntry) {
            case PREVIOUS_STATUS:
                headerRowLocator = "(//th[contains(text(),'Status')])[1]";
                break;
            case CURRENT_STATUS:
                headerRowLocator = "(//th[contains(text(),'Status')])[2]";
                break;
            case CLASS:
                headerRowLocator = "//th//div[contains(text(),'Class')]";
                break;
            case METHOD:
                headerRowLocator = "//th//div[contains(text(),'Method')]";
                break;
        }

        return tableHead.find(By.xpath(headerRowLocator));
    }

    public void assertPreviousStatusColumnHeadlineContainsCorrectHistoryIndex(int historyIndex) {
        //get table head of previous status column
        final UiElement headerRowClass = getHeaderRow(ComparisonTableEntry.PREVIOUS_STATUS);
        String tableHeadPreviousStatusColumn = headerRowClass.expect().text().getActual();

        //compare
        Assert.assertEquals(tableHeadPreviousStatusColumn, String.format("Status (Run %d)", historyIndex), "Headline should contain correct history index!");
    }

    public void assertCurrentStatusColumnHeadlineContainsCorrectHistoryIndex(int historyIndex) {
        //get table head of current status column
        final UiElement headerRowClass = getHeaderRow(ComparisonTableEntry.CURRENT_STATUS);
        String tableHeadCurrentStatusColumn = headerRowClass.expect().text().getActual();

        //compare
        Assert.assertEquals(tableHeadCurrentStatusColumn, String.format("Status (Run %d)", historyIndex), "Headline should contain correct history index!");
    }

    private By getXpathToStatistics(String statisticsName) {
        final String xPathToStatisticsItemTemplate = ".//mdc-list-item[.//span[text()='%s']]";
        return By.xpath(String.format(xPathToStatisticsItemTemplate, statisticsName));
    }

    public void checkStatisticsOfCurrentRun(String statisticsName, String value) {
        final UiElement statisticsElement = runOverviewCardOfCurrentRun.find((getXpathToStatistics(statisticsName)));
        statisticsElement.expect().text().contains(value).is(true);
    }

    public void checkStatisticsOfPreviousRun(String statisticsName, String value) {
        final UiElement statisticsElement = runOverviewCardOfPreviousRun.find((getXpathToStatistics(statisticsName)));
        statisticsElement.expect().text().contains(value).is(true);
    }

    public String getTextOfExecutionSelect() {
        return testExecutionSelect.expect().text().getActual();
    }

    public ReportTestClassesTab clickOnClassInTable(String className) {
        UiElement subElement = pageContent.find(this.preparedClassLinkLocator.with(className));
        subElement.click();
        return createPage(ReportTestClassesTab.class);
    }

    public ReportMethodHistoryTab clickOnMethodInTable(String methodName) {
        UiElement subElement = pageContent.find(this.preparedMethodLinkLocator.with(methodName));
        subElement.click();
        return createPage(ReportMethodHistoryTab.class);
    }

    public int getSelectedHistoryIndex() {
        String input = getTextOfExecutionSelect();
        int historyIndex = 0;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Run (\\d+) ");
        java.util.regex.Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            historyIndex = Integer.parseInt(matcher.group(1));
        }
        return historyIndex;
    }
}
