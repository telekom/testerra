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

package io.testerra.report.test.pages.report.sideBarPages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.PreparedLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportDashBoardPage extends AbstractReportPage {

    // Pie chart
    @Check
    private final UiElement breakdownCard = pageContent.find(this.cardboxLocator.with("breakdown-chart-card"));
    private final UiElement breakdownChart = breakdownCard.find(By.xpath("//div[@data-testid = 'test-run-pie-chart']"));

    // Test results card
    @Check
    private final UiElement testResultCard = pageContent.find(this.cardboxLocator.with("test-results-card"));
    PreparedLocator testResultElement = LOCATE.prepare("//li[@data-testid='%s']");

    @Check
    private final UiElement historyCard = pageContent.find(this.cardboxLocator.with("history-chart-card"));

    // Duration card
    @Check
    private final UiElement durationCard = pageContent.find(this.cardboxLocator.with("duration-card"));
    private final UiElement testDurationElement = durationCard.find(By.xpath("//h5[@data-testid = 'duration-value']"));
    private final UiElement testStartElement = durationCard.find(By.xpath("//span[@data-testid = 'started-value']"));
    private final UiElement testEndElement = durationCard.find(By.xpath("//span[@data-testid = 'ended-value']"));



    // Test classes
    @Check
    private final UiElement testclassesCard = pageContent.find(this.cardboxLocator.with("test-classes-chart-card"));
    private final UiElement testClassesChart = testclassesCard.find(By.xpath("//div[@data-testid='test-classes-chart']"));

    private final UiElement barChartElement = testclassesCard
            .find(By.xpath("//*[contains(@class,'apexcharts-bar-series') and contains(@class,'apexcharts-plot-series')]"));
    private final UiElement barChartPartPerTestStatus = barChartElement.find(By.xpath("//*[@class='apexcharts-series']"));
    private final UiElement segmentsOfBarsPerTestStatus = barChartPartPerTestStatus.find(By.xpath("//*"));

    // Failure aspects
    private final UiElement failureAspectsCard = pageContent.find(this.cardboxLocator.with("failure-aspects-card"));
    private final UiElement majorLink = failureAspectsCard.find(By.xpath("//*[contains(text(), 'Major')]"));
    private final UiElement minorLink = failureAspectsCard.find(By.xpath("//*[contains(text(), 'Minor')]"));
    private final UiElement topFailuresLink = failureAspectsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    private final UiElement priorityMessagesElement = find(this.cardboxLocator.with("priority-messages-card"));

    private final String xPathToPieChartPart = "//*[@class='apexcharts-series apexcharts-pie-series' and @seriesName='%s']";

    public ReportDashBoardPage(WebDriver driver) {
        super(driver);
    }

    public String getTestResultText(Status status) {
        UiElement item = this.getTestResultItem(status);
        return item.waitFor().text().getActual();
    }

    public String getBreakpointSelection() {
        return this.breakdownChart.waitFor().attribute("data-selected-statuses").getActual();
    }

    // TODO: cannot get elements of PieChart
    private UiElement getPieChartPart(final Status status) {
        return breakdownCard.find(
                By.xpath(String.format(xPathToPieChartPart, getTitleWithSpaceReplacement(status))));
    }

    public ReportDashBoardPage clickTestResult(Status status) {
        UiElement item = this.getTestResultItem(status);
        item.click();
        return createPage(ReportDashBoardPage.class);
    }

    private UiElement getTestResultItem(Status status) {
        return testResultCard.find(this.testResultElement.with(status.title));
    }

    public List<Map<String, Object>> getFilteredTestclasses() throws JsonProcessingException {
        String classes = this.testClassesChart.waitFor().attribute("data-visible-classes").getActual();
        return new ObjectMapper().readValue(
                classes,
                new TypeReference<>() {
                });
    }

    public void assertNumbersChartContainsTestState(Status status) {
        UiElement testResultItem = this.getTestResultItem(status);
        testResultItem.expect().displayed().is(true, "<there should be a chart displayed, containing the amounts of each test state>");
    }

    public void assertDurationCard() {
        this.testDurationElement.assertThat().displayed().is(true, "Test Duration element is displayed");
        this.testStartElement.assertThat().displayed().is(true, "Test Start Element is displayed");
        this.testEndElement.assertThat().displayed().is(true, "Test End Element is displayed");
    }

    public String getTestDuration() {
        return this.testDurationElement.waitFor().text().getActual().trim();
    }

    public String getTestStarted() {
        return this.testStartElement.waitFor().text().getActual();
    }

    public String getTestEnded() {
        return this.testEndElement.waitFor().text().getActual();
    }

    public ReportTestsPage navigateToFilteredTestPageByClickingBarChartBar() {
        segmentsOfBarsPerTestStatus.click();
        return createPage(ReportTestsPage.class);
    }

    // Test classes chart
    public void assertPopupWhileHoveringWithCorrectContent(Status status) {
        for (UiElement bar : segmentsOfBarsPerTestStatus.list().stream().collect(Collectors.toList())) {
            bar.hover();
            TimerUtils.sleep(5_000);
            PreparedLocator tooltip = LOCATE.prepare("//div[contains(@class, 'apexcharts-tooltip')]//span[@class='apexcharts-tooltip-text-y-label' and contains(text(), '%s')]");
            UiElement tooltipElement = testclassesCard.find(tooltip.with(status.title));
            tooltipElement.assertThat().present(true);
        }
    }

    public void assertBarChartIsDisplayed() {
        long barListLength = barChartPartPerTestStatus.list().stream().count();
        Assert.assertEquals(barListLength, 4, "There should be 4 entries, 1 for each test state!");
    }

    public void assertCorrectBarsLength(double threshold) {
        final double lengthUnit = getOneLengthUnit();
        Assert.assertNotEquals(lengthUnit, 0, "The length of a bar in the barchart representing one test should not be 0!");

        List<UiElement> barTypeList = barChartPartPerTestStatus.list().stream().collect(Collectors.toList());
        for (UiElement barType : barTypeList) {
            List<UiElement> bars = barType.find(By.xpath("//*")).list().stream().collect(Collectors.toList());
            for (UiElement bar : bars) {
                final String valAsString = getBuggedAttributePerJavascriptExecutor(bar, "val", 1);
                Assert.assertNotNull(valAsString, "There should be a attribute 'val' with the expected length of 1!");

                int amountOfLengthUnits = Integer.parseInt(valAsString);
                double upperBound = (lengthUnit * amountOfLengthUnits) * (1 + threshold);
                double lowerBound = (lengthUnit * amountOfLengthUnits) * (1 - threshold);
                final String barWidth = getBuggedAttributePerJavascriptExecutor(bar, "barWidth", 8);
                double actualBarLength = Double.parseDouble(Objects.requireNonNull(barWidth));
                Assert.assertTrue(lowerBound <= actualBarLength && actualBarLength <= upperBound,
                        String.format("BarWidth got too much deviation to excepted bounds! %f not in [%f, %f]", actualBarLength, lowerBound, upperBound));
            }
        }
    }

    private double getOneLengthUnit() {
        List<UiElement> barTypeList = barChartPartPerTestStatus.list().stream().collect(Collectors.toList());
        for (UiElement barType : barTypeList) {
            List<UiElement> bars = barType.find(By.xpath("//*")).list().stream().collect(Collectors.toList());
            for (UiElement bar : bars) {
                if (bar.expect().attribute("val").getActual().equals("1")) {
                    String barWidth = getBuggedAttributePerJavascriptExecutor(bar, "barWidth", 8);
                    Assert.assertNotNull(barWidth, "GuiElement attribute value is null!");
                    return Double.parseDouble(barWidth);
                }
            }
        }
        return 0;
    }

    private String getBuggedAttributePerJavascriptExecutor(UiElement uiElement, String attribute, int expectedLengthOfValue) {
        try {

            final Object[] aa = {null};

            String jsScript = "var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;";
            uiElement.findWebElement(webElement -> {
                aa[0] = JSUtils.executeScript(uiElement.getWebDriver(), jsScript, webElement);
            });

            // JavascriptExecutor executor = (JavascriptExecutor) getWebDriver();
            // Object aa = executor.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", element);

            String listOfAllAttributes = aa[0].toString();
            int attributeSubstringStart = listOfAllAttributes.indexOf(attribute);
            int lengthOfAttributeSequence = attribute.length() + expectedLengthOfValue + 1;

            String subStringOfOneAttribute = listOfAllAttributes.substring(attributeSubstringStart, attributeSubstringStart + lengthOfAttributeSequence);

            if (subStringOfOneAttribute.contains("=0,")) {
                return "0";
            }
            // parsed String might contain additional attributes besides the intended attribute
            if (subStringOfOneAttribute.contains(",")) {
                // cut off after ","
                String[] split = subStringOfOneAttribute.split(",");
                subStringOfOneAttribute = split[0];
            }

            return subStringOfOneAttribute.split("=")[1];
        } catch (Exception e) {
            return "0";
        }
    }

    public void assertFailureCorridorValue(final String failureCorridorType, final int value) {
        final UiElement failureCorridor = getFailureCorridorPart(failureCorridorType);
        final String text = failureCorridor.expect().text().getActual();

        Assert.assertTrue(text.contains(String.valueOf(value)),
                String.format("Failure Corridor '%s' has value '%s'", failureCorridor, value));
    }

    public void assertFailureCorridorValuesAreCorrectClassified(String failureCorridorType, long bound) {
        UiElement failureCorridor = getFailureCorridorPart(failureCorridorType);
        int displayedAmount = Integer.parseInt(failureCorridor.expect().text().getActual().split(" ")[0]);
        if (displayedAmount <= bound) {
            Assert.assertTrue(failureCorridor.waitFor().attribute("class").getActual().contains("status-passed"), "Corridor should be classified correctly!");
        } else {
            Assert.assertTrue(failureCorridor.waitFor().attribute("class").getActual().contains("status-failed"), "Corridor should be classified correctly!");
        }
    }

    private UiElement getFailureCorridorPart(final String failureCorridorType) {
        String xPathToFailureCorridor = "//*[contains(@class.bind,'Corridor') and contains(text(), '%s')]";
        return find(By.xpath(String.format(xPathToFailureCorridor, failureCorridorType)));
    }

    public void assertTopFailureAspectsAreDisplayed() {
        failureAspectsCard.expect().displayed().is(true);
    }

    public ReportFailureAspectsPage clickMajorFailureAspectsLink() {
        majorLink.click();
        return createPage(ReportFailureAspectsPage.class);
    }

    public ReportFailureAspectsPage clickMinorFailureAspectsLink() {
        minorLink.click();
        return createPage(ReportFailureAspectsPage.class);
    }

    public List<String> getOrderListOfTopFailureAspects() {
        return topFailuresLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    private String getTitleWithSpaceReplacement(Status status) {
        return status.title.replace(" ", "x");
    }


    public void assertPriorityMessages(final String[] priorityMessages) {
        assertPriorityInfo(priorityMessages);
    }

    public void assertStatesOfPriorityMessages(final String[] states) {
        assertPriorityInfo(states);
    }

    private void assertPriorityInfo(final String[] infos) {
        for (final String info : infos) {
            final UiElement priorityMessageElement = priorityMessagesElement.find(By.xpath(String.format("//*[contains(@class,'%s')]", info)));
            priorityMessageElement.assertThat().displayed();
        }
    }

    public void assertHistoryChartMatchesScreenshot(double pixelDistance) {
        this.historyCard.expect().screenshot().pixelDistance("history_chart_dashboard").isLowerThan(pixelDistance);
    }
}
