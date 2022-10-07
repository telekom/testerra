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
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
    private final UiElement testsElement = pageContent.find(By.xpath(".//mdc-card[./div[contains(text(), 'Tests')]]"));

    @Check
    private final UiElement testDurationElement = pageContent.find(By.tagName("test-duration-card"));

    private final UiElement testStartElement = testDurationElement.find(By.xpath("//span[contains(text(), 'Started')]/following-sibling::span"));
    private final UiElement testEndElement = testDurationElement.find(By.xpath("//span[contains(text(), 'Ended')]/following-sibling::span"));

    @Check
    private final UiElement testResultElement = pageContent.find(By.tagName("test-results-card"));    //pieChart
    @Check
    private final UiElement testClassesElement = pageContent.find(By.tagName("test-classes-card"));

    private final UiElement barChartElement = testClassesElement
            .find(By.xpath("//*[contains(@class,'apexcharts-bar-series') and contains(@class,'apexcharts-plot-series')]"));

    private final UiElement barChartPartPerTestStatus = barChartElement.find(By.xpath("//*[@class='apexcharts-series']"));
    private final UiElement segmentsOfBarsPerTestStatus = barChartPartPerTestStatus.find(By.xpath("//*"));

    private final UiElement testTopFailureAspectsElement = pageContent.find(By.xpath(".//mdc-card[./div[contains(text(), 'Failure Aspects')]]"));
    private final UiElement majorLink = testTopFailureAspectsElement.find(By.xpath("//*[contains(text(), 'Major')]"));
    private final UiElement minorLink = testTopFailureAspectsElement.find(By.xpath("//*[contains(text(), 'Minor')]"));
    private final UiElement topFailuresLink = testTopFailureAspectsElement.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    private final String xPathToPieChartPart = "//*[@class='apexcharts-series apexcharts-pie-series' and @seriesName='%s']";

    public ReportDashBoardPage(WebDriver driver) {
        super(driver);
    }

    //TODO: fehler in Testerra? 'AbstractPage' ruft @Deprecated-methode auf?
    public void assertPageIsShown() {
        verifyReportPage(ReportSidebarPageType.DASHBOARD);
    }

    public String getTestsPerStatus(final Status testStatus) {

        String testsPerStatus = "not_existing";

        final UiElement testsStatusElement;
        switch (testStatus) {
            // retried status is within element of failed status
            case RETRIED:
                testsStatusElement = testsElement.find((getXpathToTestsPerStatus(Status.FAILED)));
                break;
            // repaired and recovered status are within element of passed status
            case REPAIRED:
            case RECOVERED:
                testsStatusElement = testsElement.find((getXpathToTestsPerStatus(Status.PASSED)));
                break;
            // remaining test status have dedicated elements
            default:
                testsStatusElement = testsElement.find((getXpathToTestsPerStatus(testStatus)));
                break;
        }

        final List<UiElement> listOfAmountInformation = testsStatusElement.find(By.xpath("./mdc-list-item-primary-text/span")).list().stream().collect(Collectors.toList());


        // for passed when repaired executions exist
        if (listOfAmountInformation.size() > 1) {
            final List<UiElement> listOfStatusInformation = testsStatusElement.find(By.xpath("./mdc-list-item-secondary-text/span")).list().stream().collect(Collectors.toList());
            for (int i = 0; i < listOfStatusInformation.size(); i++) {
                if (listOfStatusInformation.get(i).expect().text().getActual().contains(testStatus.title)) {
                    testsPerStatus = listOfAmountInformation.get(i).expect().text().getActual() + " " + listOfStatusInformation.get(i).expect().text().getActual();
                }
            }
        } else {
            testsPerStatus = testsStatusElement.expect().text().getActual().replace("\n", " ");
        }

        return testsPerStatus;
    }

    private By getXpathToTestsPerStatus(final Status testStatus) {
        final String xPathToTestsPerStatusTemplate = ".//mdc-list-item[.//mdc-icon[@title = '%s']]//span[contains(@class, 'mdc-list-item__content')]";
        return By.xpath(String.format(xPathToTestsPerStatusTemplate, testStatus.title));
    }

    public void assertPieChartContainsTestState(final Status status) {
        UiElement pieChartPart = getPieChartPart(status);
        //pieChartPart.asserts("There should be a pie chart displayed!").assertIsDisplayed();
        pieChartPart.expect().displayed().is(true, "There should be a pie chart displayed!");
    }

    public ReportDashBoardPage clickPieChartPart(final Status status) {
        UiElement pieChartPart = getPieChartPart(status);
        pieChartPart.click();
        return createPage(ReportDashBoardPage.class);
    }

    private UiElement getPieChartPart(final Status status) {
        return testResultElement.find(
                By.xpath(String.format(xPathToPieChartPart, getTitleWithSpaceReplacement(status))));
    }

    public ReportDashBoardPage clickNumberChartPart(Status status) {
        UiElement testClassesNumberChartElement = testsElement.find(By.xpath(String.format("//mdc-list-item[.//mdc-icon[@title='%s']]", status.title)));
        testClassesNumberChartElement.click();
        return createPage(ReportDashBoardPage.class);
    }

    public void assertCorrectBarChartsAreDisplayed() {
        List<UiElement> barList = barChartPartPerTestStatus.list().stream().collect(Collectors.toList());
        Assert.assertEquals(barList.size(), 1, "There should be just 1 entry!");
    }


    public void assertNumbersChartContainsTestState(Status status) {
        String testClassesNumberChartPath = String.format("//mdc-list-item//mdc-icon[@title='%s']", status.title);
        UiElement testClassesNumberChart = testsElement.find(By.xpath(testClassesNumberChartPath));
        testClassesNumberChart.expect().displayed().is(true, "<there should be a chart displayed, containing the amounts of each test state>");
        //testClassesNumberChart.asserts("There should be a chart displayed containing the amounts of each test state!").assertIsDisplayed();
    }

    public void assertStartTimeIsDisplayed() {
        testStartElement.expect().displayed().is(true, "Test Start Element is displayed");
    }

    public void assertEndedTimeIsDisplayed() {
        testEndElement.expect().displayed().is(true, "Test End Element is displayed");
    }

    public String getTestDuration() {
        UiElement durationGuiElement = testDurationElement.find(By.xpath("//div[contains(@class,'card-content')]"));
        return durationGuiElement.expect().text().getActual().split("\n")[1];
    }

    public ReportTestsPage navigateToFilteredTestPageByClickingBarChartBar() {
        segmentsOfBarsPerTestStatus.click();
        return createPage(ReportTestsPage.class);
    }

    public void assertPieChartPercentages(int expectedAmount, Status status) {
        //iterate through tests card and sum up all "main" numbers of all test states (f.e. not retried or recovered)
        int amountOfTests = testsElement.find(By.xpath("//mdc-list-item")).list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .map(i -> RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY, i))
                .map(Integer::parseInt)
                .mapToInt(i -> i)
                .sum();

        //there are two different GuiElements for each pie chart part, one contains graphics, the other contains percentage content
        //the GuiElement with percentage-content got no direct information which test state is corresponding, BUT: it got equivalent index
        //even if there is an attribute "data:realIndex", it is somehow not accessible, BUT: there is another attribute "rel" which is accessible
        //"data:realIndex" = "rel" - 1
        int index = Integer.parseInt(
                testResultElement.find(
                        By.xpath(String.format(xPathToPieChartPart, getTitleWithSpaceReplacement(status)))
                ).expect().attribute("rel").getActual()) - 1;
        UiElement pieChartPart = testResultElement.find(By.xpath("//*[@class='apexcharts-datalabels']")).list().stream().collect(Collectors.toList()).get(index);
        String percentageString = getPercentagesFromReportByStates(expectedAmount, amountOfTests);
        //pieChartPart.asserts(String.format("The pie chart should contain percentage labels with correct content.[Actual:%s]", pieChartPart.getText())).assertText(percentageString);
        pieChartPart.expect().text().is(percentageString, String.format("The pie chart should contain percentage labels with correct content.[Actual:%s]", pieChartPart.expect().text().getActual()));
    }

    private String getPercentagesFromReportByStates(double amount, double total) {
        DecimalFormat df = new DecimalFormat("##.# %", new DecimalFormatSymbols(Locale.ENGLISH));
        return df.format(amount / total).replace(" ", "");
    }

    public void assertPopupWhileHoveringWithCorrectContent(Status status) {
        for (UiElement bar : segmentsOfBarsPerTestStatus.list().stream().collect(Collectors.toList())) {
            bar.hover();
            TimerUtils.sleep(10_000);
            //String popUpTestStatePath = "//*[contains(@class,'apexcharts-canvas')]//div[contains(@class,'apexcharts-tooltip')]//span[@class='apexcharts-tooltip-text-label']";
            String popUpTestStatePath = "//div[contains(@class,'apexcharts-tooltip')]//span[@class='apexcharts-tooltip-text-label']";

            Optional<UiElement> popUpTestState = find(By.xpath(popUpTestStatePath)).list()
                    .stream()
                    .filter(uiElement -> uiElement.expect().text().getActual().contains(status.title)) //text().contains(...) oder text().isContaining(...) funktioniert nicht, da dort schon ein assert mit ausgeführt wird
                    .findFirst();
            Assert.assertTrue(popUpTestState.isPresent(), "Should find a text element, which contains the corresponding state description!");
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
        //testTopFailureAspectsElement.asserts().assertIsDisplayed();
        testTopFailureAspectsElement.expect().displayed().is(true);
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
}
