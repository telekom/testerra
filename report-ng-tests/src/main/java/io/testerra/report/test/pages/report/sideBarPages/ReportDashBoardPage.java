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

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ReportDashBoardPage(WebDriver driver) {
        super(driver);
    }

    public void assertPageIsShown() {
        verifyReportPage(ReportSidebarPageType.DASHBOARD);
    }

    public String getTestsPerStatus(final Status testStatus) {

        String testsPerStatus = "not_existing";

        final GuiElement testsStatusElement;
        switch (testStatus) {
            // retried status is within element of failed status
            case RETRIED:
                testsStatusElement = testsElement.getSubElement((getXpathToTestsPerStatus(Status.FAILED)));
                break;
            // repaired and recovered status are within element of passed status
            case REPAIRED:
            case RECOVERED:
                testsStatusElement = testsElement.getSubElement((getXpathToTestsPerStatus(Status.PASSED)));
                break;
            // remaining test status have dedicated elements
            default:
                testsStatusElement = testsElement.getSubElement((getXpathToTestsPerStatus(testStatus)));
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

    private By getXpathToTestsPerStatus(final Status testStatus) {
        final String xPathToTestsPerStatusTemplate = ".//mdc-list-item[.//mdc-icon[@title = '%s']]//span[contains(@class, 'mdc-list-item__content')]";
        return By.xpath(String.format(xPathToTestsPerStatusTemplate, testStatus.title));
    }

    public void assertPieChartContainsTestState(final Status status) {
        GuiElement pieChartPart = getPieChartPart(status);
        pieChartPart.asserts("There should be a pie chart displayed!").assertIsDisplayed();
    }

    public ReportDashBoardPage clickPieChartPart(final Status status) {
        GuiElement pieChartPart = getPieChartPart(status);
        pieChartPart.click();
        return PageFactory.create(ReportDashBoardPage.class, getWebDriver());
    }

    private GuiElement getPieChartPart(final Status status) {
        return testResultElement.getSubElement(
                By.xpath(String.format(xPathToPieChartPart, getTitleWithSpaceReplacement(status))));
    }

    public ReportDashBoardPage clickNumberChartPart(Status status) {
        GuiElement testClassesNumberChartElement = testsElement.getSubElement(By.xpath(String.format("//mdc-list-item[.//mdc-icon[@title='%s']]", status.title)));
        testClassesNumberChartElement.click();
        return PageFactory.create(ReportDashBoardPage.class, getWebDriver());
    }

    public void assertCorrectBarChartsAreDisplayed() {
        List<GuiElement> barList = barChartPartPerTestStatus.getList();
        Assert.assertEquals(barList.size(), 1, "There should be just 1 entry!");
    }


    public void assertNumbersChartContainsTestState(Status status) {
        String testClassesNumberChartPath = String.format("//mdc-list-item//mdc-icon[@title='%s']", status.title);
        GuiElement testClassesNumberChart = testsElement.getSubElement(By.xpath(testClassesNumberChartPath));
        testClassesNumberChart.asserts("There should be a chart displayed containing the amounts of each test state!").assertIsDisplayed();
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

    public void assertPieChartPercentages(int expectedAmount, Status status) {
        //iterate through tests card and sum up all "main" numbers of all test states (f.e. not retried or recovered)
        int amountOfTests = testsElement.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .map(GuiElement::getText)
                .map(i -> RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY, i))
                .map(Integer::parseInt)
                .mapToInt(i -> i)
                .sum();

        //there are two different GuiElements for each pie chart part, one contains graphics, the other contains percentage content
        //the GuiElement with percentage-content got no direct information which test state is corresponding, BUT: it got equivalent index
        //even if there is an attribute "data:realIndex", it is somehow not accessible, BUT: there is another attribute "rel" which is accessible
        //"data:realIndex" = "rel" - 1
        int index = Integer.parseInt(
                testResultElement.getSubElement(
                        By.xpath(String.format(xPathToPieChartPart, getTitleWithSpaceReplacement(status)))
                ).getAttribute("rel")) - 1;
        GuiElement pieChartPart = testResultElement.getSubElement(By.xpath("//*[@class='apexcharts-datalabels']")).getList().get(index);
        String percentageString = getPercentagesFromReportByStates(expectedAmount, amountOfTests);
        pieChartPart.asserts(String.format("The pie chart should contain percentage labels with correct content.[Actual:%s]", pieChartPart.getText())).assertText(percentageString);
    }

    private String getPercentagesFromReportByStates(double amount, double total) {
        DecimalFormat df = new DecimalFormat("##.# %", new DecimalFormatSymbols(Locale.ENGLISH));
        return df.format(amount / total).replace(" ", "");
    }

    public void assertPopupWhileHoveringWithCorrectContent(Status status) {
        Actions action = new Actions(getWebDriver());
        for (GuiElement bar : segmentsOfBarsPerTestStatus.getList()) {
            action.moveToElement(bar.getWebElement()).build().perform();
            String popUpTestStatePath = "//*[contains(@class,'apexcharts-canvas')]//div[contains(@class,'apexcharts-tooltip')]//span[@class='apexcharts-tooltip-text-label']";
            Optional<GuiElement> popUpTestState = new GuiElement(getWebDriver(), By.xpath(popUpTestStatePath)).getList().stream().filter(i -> i.getText().contains(status.title)).findFirst();
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
                Assert.assertNotNull(valAsString, "There should be a attribute 'val' with the expected length of 1!");
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
            String subStringOfOneAttribute = listOfAllAttributes.substring(attributeSubstringStart, attributeSubstringStart + lengthOfAttributeSequence);
            if (subStringOfOneAttribute.contains("=0,")) {
                return "0";
            }
            return subStringOfOneAttribute.split("=")[1];
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
        String xPathToFailureCorridor = "//*[contains(@class.bind,'Corridor') and contains(text(), '%s')]";
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

    private String getTitleWithSpaceReplacement(Status status){
        return status.title.replace(" ", "x");
    }
}
