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

package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Status;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import io.testerra.report.test.pages.AbstractReportPage;

public class ReportDashBoardPage extends AbstractReportPage {

    @Check
    private final GuiElement testsElement = pageContent.getSubElement(By.xpath(".//mdc-card[./div[contains(text(), 'Tests')]]"));

    @Check
    private final GuiElement testDurationElement = pageContent.getSubElement(By.tagName("test-duration-card"));

    private final GuiElement testStartElement = testDurationElement.getSubElement(By.xpath("//span[contains(text(), 'Started')]/following-sibling::span"));
    private final GuiElement testEndElement = testDurationElement.getSubElement(By.xpath("//span[contains(text(), 'Ended')]/following-sibling::span"));

    @Check
    private final GuiElement testResultElement = pageContent.getSubElement(By.tagName("test-results-card"));    //pieChart
    @Check
    private final GuiElement testClassesElement = pageContent.getSubElement(By.tagName("test-classes-card"));

    private final GuiElement barChartElement = testClassesElement
            .getSubElement(By.xpath("//*[contains(@class,'apexcharts-bar-series') and contains(@class,'apexcharts-plot-series')]"));

    private final GuiElement barChartPartPerTestStatus = barChartElement.getSubElement(By.xpath("//*[@class='apexcharts-series']"));
    private final GuiElement segmentsOfBarsPerTestStatus = barChartPartPerTestStatus.getSubElement(By.xpath("//*"));

    private final GuiElement testTopFailureAspectsElement = pageContent.getSubElement(By.xpath(".//mdc-card[./div[contains(text(), 'Failure Aspects')]]"));
    private final GuiElement majorLink = testTopFailureAspectsElement.getSubElement(By.xpath("//*[contains(text(), 'Major')]"));
    private final GuiElement minorLink = testTopFailureAspectsElement.getSubElement(By.xpath("//*[contains(text(), 'Minor')]"));
    private final GuiElement topFailuresLink = testTopFailureAspectsElement.getSubElement(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    private final String xPathToPieChartPart = "//*[@class='apexcharts-series apexcharts-pie-series' and @seriesName='%s']";
    private final String xPathToFailureCorridor = "//*[contains(@class.bind,'Corridor') and contains(text(), '%s')]";

    public ReportDashBoardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * extract information of executed tests per Status from DashBoardPage Tests card
     *
     * @param testStatus
     * @return
     */
    public String getTestsPerStatus(final Status testStatus) {

        String testsPerStatus = "not_existing";

        final GuiElement testsStatusElement;
        switch (testStatus) {
            // retried status is within element of failed status
            case RETRIED:
                testsStatusElement = getTestStateElementPerStatus(Status.FAILED);
                break;
            // repaired and recovered status are within element of passed status
            case REPAIRED:
            case RECOVERED:
                testsStatusElement = getTestStateElementPerStatus(Status.PASSED);
                break;
            // remaining test status have dedicated elements
            default:
               testsStatusElement = getTestStateElementPerStatus(testStatus);
               break;
        }

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

    private GuiElement getTestStateElementPerStatus(final Status testStatus) {
        GuiElement testStateIconInTestsList = getTestStateIconInTestsList(testStatus);
        return testStateIconInTestsList.getSubElement(By.xpath(".//span[contains(@class, 'mdc-list-item__content')]"));
    }

    private GuiElement getTopFailureAspectsCard() {
        Optional<GuiElement> optionalTopFailureAspects = new GuiElement(getWebDriver(), By.xpath("//mdc-card")).getList()
                .stream()
                .filter(i -> i.getSubElement(By.xpath("/div[contains(text(), 'Failure Aspects')]")).isDisplayed())
                .findFirst();
        Assert.assertTrue(optionalTopFailureAspects.isPresent());
        return optionalTopFailureAspects.get();
    }

    public void assertPieChartContainsTestState(final Status status) {
        GuiElement pieChartPart = getPieChartPart(status);
        pieChartPart.asserts().assertIsDisplayed();
    }

    public void clickPieChartPart(final Status status) {
        GuiElement pieChartPart = getPieChartPart(status);
        pieChartPart.click();
    }

    private GuiElement getPieChartPart(final Status status) {
        return testResultElement.getSubElement(
                By.xpath(String.format(xPathToPieChartPart, status.getTitleWithSpaceReplacement())));
    }

    public ReportDashBoardPage clickTestStateIconInTestsList(Status status) {
        GuiElement testStateIcon = getTestStateIconInTestsList(status);
        testStateIcon.click();

        return PageFactory.create(ReportDashBoardPage.class, getWebDriver());
    }

    public void assertCorrectBarChartsAreDisplayed() {
        List<GuiElement> barList = barChartPartPerTestStatus.getList();
        Assert.assertEquals(barList.size(), 1, "There should be just 1 entry!");
    }


    public void assertTestStateIconInTestsList(Status status) {
        GuiElement testStateIcon = getTestStateIconInTestsList(status);
        testStateIcon.asserts(String.format("Test state icon '%s' is displayed", status.title)).assertIsDisplayed();
    }

    private GuiElement getTestStateIconInTestsList(Status status) {
        String xpath = String.format(".//mdc-list-item[.//mdc-icon[@title='%s']]", status.title);
        return testsElement.getSubElement(By.xpath(xpath));
    }

    public void assertStartTimeIsDisplayed() {
        testStartElement.asserts("Test Start Element is displayed").assertIsDisplayed();
    }

    public void assertEndedTimeIsDisplayed() {
        testEndElement.asserts("Test End Element is displayed").assertIsDisplayed();
    }

    public String getTestDuration() {
        GuiElement durationGuiElement = testDurationElement.getSubElement(By.xpath("//div[contains(@class,'card-content')]"));
        return durationGuiElement.getText().split("\n")[1];
    }

    public ReportTestsPage navigateToFilteredTestPageByClickingBarChartBar() {
        segmentsOfBarsPerTestStatus.click();
        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }

    // TODO: total amount printed not fitting for percentage calculation, as soon as retried tests occurred
    public void assertPieChartPercentages(int expectedAmount, Status status) {

        GuiElement pieChartPart = testResultElement.getSubElement(By.xpath(String.format("(//*[@class='apexcharts-datalabels'])[%d]", status.ordinal() + 1)));

        double amountOfTotalTestAsString = getAmountOfTests();

        String percentageString = getPercentagesFromReportByStates(expectedAmount, amountOfTotalTestAsString);
        pieChartPart.asserts(String.format("Pie Charts contains '%s'", percentageString)).assertText(percentageString);
    }

    private String getPercentagesFromReportByStates(double amount, double total) {
        DecimalFormat df = new DecimalFormat("##.# %", new DecimalFormatSymbols(Locale.ENGLISH));
        return df.format(amount / total).replace(" ", "");
    }

    public void assertPopupWhileHoveringWithCorrectContent(Status status) {
        Actions action = new Actions(getWebDriver());
        for (GuiElement bar : segmentsOfBarsPerTestStatus.getList()) {
            action.moveToElement(bar.getWebElement()).build().perform();
            String path = "//*[contains(@class,'apexcharts-canvas')]//div[contains(@class,'apexcharts-tooltip')]//span[@class='apexcharts-tooltip-text-label']";
            Optional<GuiElement> popUpTestState = new GuiElement(getWebDriver(), By.xpath(path)).getList().stream().filter(i -> i.getText().contains(status.title)).findFirst();
            Assert.assertTrue(popUpTestState.isPresent(), "Should find a text element, which contains the corresponding state description!");
        }
    }

    public void assertBarChartIsDisplayed() {
        List<GuiElement> barList = barChartPartPerTestStatus.getList();
        Assert.assertEquals(barList.size(), 4, "There should be 4 entries, 1 for each test state!");
    }

    public void assertCorrectBarsLength(double threshold) {
        final double lengthUnit = getOneLengthUnit();
        Assert.assertNotEquals(lengthUnit, 0, "The length of a bar in the barchart representing one test should not be 0!");

        List<GuiElement> barTypeList = barChartPartPerTestStatus.getList();
        for (GuiElement barType : barTypeList) {
            List<GuiElement> bars = barType.getSubElement(By.xpath("//*")).getList();
            for (GuiElement bar : bars) {
                String valAsString = getBuggedAttributePerJavascriptExecutor(bar, "val", 1);
                Assert.assertNotNull(valAsString);
                int amountOfLengthUnits = Integer.parseInt(valAsString);
                double upperBound = (lengthUnit * amountOfLengthUnits) * (1 + threshold);
                double lowerBound = (lengthUnit * amountOfLengthUnits) * (1 - threshold);
                double actualBarLength = Double.parseDouble(Objects.requireNonNull(getBuggedAttributePerJavascriptExecutor(bar, "barWidth", 8)));
                //System.out.printf("%f €? [%f, %f]%n", actualBarLength, lowerBound, upperBound);
                Assert.assertTrue(lowerBound <= actualBarLength && actualBarLength <= upperBound,
                        String.format("BarWidth got too much deviation to excepted bounds! %f not in [%f, %f]", actualBarLength, lowerBound, upperBound));
            }
        }
    }

    private double getOneLengthUnit() {
        List<GuiElement> barTypeList = barChartPartPerTestStatus.getList();
        for (GuiElement barType : barTypeList) {
            List<GuiElement> bars = barType.getSubElement(By.xpath("//*")).getList();
            for (GuiElement bar : bars) {
                if (bar.getAttribute("val").equals("1")) {
                    String barWidth = getBuggedAttributePerJavascriptExecutor(bar, "barWidth", 8);
                    Assert.assertNotNull(barWidth, "GuiElement attribute value is null!");
                    return Double.parseDouble(barWidth);
                }
            }
        }
        return 0;
    }

    private String getBuggedAttributePerJavascriptExecutor(GuiElement guiElement, String attribute, int expectedLengthOfValue) {
        try {
            WebElement element = guiElement.getWebElement();
            JavascriptExecutor executor = (JavascriptExecutor) getWebDriver();
            Object aa = executor.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", element);
            String listOfAllAttributes = aa.toString();
            int attributeSubstringStart = listOfAllAttributes.indexOf(attribute);
            int lengthOfAttributeSequence = attribute.length() + expectedLengthOfValue + 1;
            String substring = listOfAllAttributes.substring(attributeSubstringStart, attributeSubstringStart + lengthOfAttributeSequence);
            if (substring.contains("=0,")) {
                return "0";
            }
            return substring.split("=")[1];
        } catch (Exception e) {
            return "0";
        }
    }

    public void assertFailureCorridorIsDisplayed(String failureCorridorType) {
        GuiElement failureCorridor = getFailureCorridorPart(failureCorridorType);
        failureCorridor.asserts().assertIsDisplayed();
    }

    public void assertFailureCorridorValuesAreCorrectClassified(String failureCorridorType, int bound) {
        GuiElement failureCorridor = getFailureCorridorPart(failureCorridorType);
        int displayedAmount = Integer.parseInt(failureCorridor.getText().split(" ")[0]);
        if (displayedAmount <= bound) {
            Assert.assertTrue(failureCorridor.getAttribute("class").contains("status-passed"), "Corridor should be classified correctly!");
        } else {
            Assert.assertTrue(failureCorridor.getAttribute("class").contains("status-failed"), "Corridor should be classified correctly!");
        }
    }

    private GuiElement getFailureCorridorPart(final String failureCorridorType) {
        return new GuiElement(getWebDriver(), By.xpath(String.format(xPathToFailureCorridor, failureCorridorType)));
    }

    public void assertTopFailureAspectsAreDisplayed() {
        testTopFailureAspectsElement.asserts().assertIsDisplayed();
    }

    public ReportFailureAspectsPage clickMajorFailureAspectsLink() {
        majorLink.click();
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());
    }

    public ReportFailureAspectsPage clickMinorFailureAspectsLink() {
        minorLink.click();
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());
    }

    public List<String> getOrderListOfTopFailureAspects() {
        return topFailuresLink.getList()
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toList());
    }
}
