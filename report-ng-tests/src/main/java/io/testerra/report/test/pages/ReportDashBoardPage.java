/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.helper.TestState;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ReportDashBoardPage extends AbstractReportPage {

    @Check
    private final GuiElement testsElement = pageContent.getSubElement(By.xpath(".//mdc-card[./div[contains(text(), 'Tests')]]"));
    @Check
    private final GuiElement testDurationElement = pageContent.getSubElement(By.tagName("test-duration-card"));
    @Check
    private final GuiElement testResultElement = pageContent.getSubElement(By.tagName("test-results-card"));
    @Check
    private final GuiElement testClassesElement = pageContent.getSubElement(By.tagName("test-classes-card"));

    public ReportDashBoardPage(WebDriver driver) {
        super(driver);
    }

    public void assertPageIsShown() {
        verifyReportPage(ReportPageType.DASHBOARD);
    }

    /**
     * extract information of executed tests per Status from DashBoardPage Tests card
     *
     * @param testStatus
     * @return
     */
    public String getTestsPerStatus(final Status testStatus) {

        String testsPerStatus = "not_existing";
        // repaired status is within element of passed status
        final GuiElement testsStatusElement = testStatus.equals(Status.REPAIRED)
                ? testsElement.getSubElement((getXpathToTestsPerStatus(Status.PASSED)))
                : testsElement.getSubElement((getXpathToTestsPerStatus(testStatus)));
        final List<GuiElement> listOfAmountInformation = testsStatusElement.getSubElement(By.xpath("./mdc-list-item-primary-text/span")).getList();

        // for passed when repaired executions exist
        if (listOfAmountInformation.size() > 1) {
            final List<GuiElement> listOfStatusInformation = testsStatusElement.getSubElement(By.xpath("./mdc-list-item-secondary-text/span")).getList();
            for (int i = 0; i < listOfStatusInformation.size(); i++) {
                if (listOfStatusInformation.get(i).getText().contains(testStatus.title)) {
                    testsPerStatus = listOfAmountInformation.get(i).getText() + " " + listOfStatusInformation.get(i).getText();
                }
            }
        } else {
            testsPerStatus = testsStatusElement.getText().replace("\n", " ");
        }

        return testsPerStatus;
    }

    private By getXpathToTestsPerStatus(final Status testStatus) {
        final String xPathToTestsPerStatusTemplate = ".//mdc-list-item[.//mdc-icon[@title = '%s']]//span[contains(@class, 'mdc-list-item__content')]";
        return By.xpath(String.format(xPathToTestsPerStatusTemplate, testStatus.title));
    }

    public void assertPieChartContainsTestState(TestState status) {
        GuiElement pieChartPart = new GuiElement(getWebDriver(),
                By.xpath(String.format("//*[@class='apexcharts-series apexcharts-pie-series' and @seriesName='%s']", status.getStateName())));
        pieChartPart.asserts().assertIsDisplayed();
    }

    public void clickPieChartPart(TestState status) {
        GuiElement pieChartPart = new GuiElement(getWebDriver(), By.xpath(String.format("//apex-chart//*[@seriesName='%s']", status.getStateName())));
        pieChartPart.click();
    }

    public void assertCorrectBarChartsAreDisplayed(TestState state) {
        GuiElement testClassesFirstBarChart = new GuiElement(getWebDriver(), By.xpath("//*[@class='apexcharts-bar-series apexcharts-plot-series']"));
        testClassesFirstBarChart.asserts().assertIsDisplayed();
        testClassesFirstBarChart.getSubElement(By.xpath(String.format("//*[@seriesName='%s']", state.getStateName()))).asserts().assertIsDisplayed();
    }
}
