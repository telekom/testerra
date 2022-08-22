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
import io.testerra.report.test.pages.report.methodReport.ReportDependenciesTab;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportSessionsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportTestsPage extends AbstractReportPage {

    @Check
    private final GuiElement testStatusSelect = pageContent.getSubElement(By.xpath(".//mdc-select[@label = 'Status']"));
    @Check
    private final GuiElement testClassSelect = pageContent.getSubElement(By.xpath(".//mdc-select[@label = 'Class']"));
    @Check
    private final GuiElement testSearchInput = pageContent.getSubElement(By.xpath(".//input[contains(@class, 'mdc-text-field__input')]"));
    @Check
    private final GuiElement configurationMethodsSwitch = pageContent.getSubElement(By.xpath("//button[@role='switch']"));

    private final String tableRowsLocator = "//tbody//tr";
    private final GuiElement tableRows = pageContent.getSubElement(By.xpath(tableRowsLocator));

    private final String methodLinkLocator = "//tbody//tr//td//a[text()='%s']";
    private final GuiElement tableHead = pageContent.getSubElement(By.xpath(".//thead"));

    public enum TestsTableEntries {
        STATUS(0), CLASS(1), INDEX(2), METHOD(3);

        private final int value;

        TestsTableEntries(final int value) {
            this.value = value;
        }

        public int index() {
            return this.value;
        }
    }


    public ReportTestsPage(WebDriver driver) {
        super(driver);
    }

    public List<GuiElement> getColumnWithoutHead(int columnNumber) {
        List<GuiElement> column = new ArrayList<>();
        for (GuiElement row : tableRows.getList()) {
            column.add(row.getSubElement(By.xpath("//td")).getList().get(columnNumber));
        }
        return column;
    }


    public List<GuiElement> getColumnWithoutHead(TestsTableEntries tableEntry) {
        return getColumnWithoutHead(tableEntry.index());
    }

    private GuiElement getHeaderRow(TestsTableEntries tableEntry) {

        String headerRowLocator = ".//tr";

        switch (tableEntry) {
            case STATUS:
                headerRowLocator = "//th[contains(text(),'Status')]";
                break;
            case CLASS:
                headerRowLocator = "//th[contains(text(),'Class')]";
                break;
            case INDEX:
                headerRowLocator = "//th[contains(text(),'#')]";
                break;
            case METHOD:
                headerRowLocator = "//th[contains(text(),'Method')]";
                break;
        }

        return tableHead.getSubElement(By.xpath(headerRowLocator));
    }

    public void assertPageIsShown() {
        verifyReportPage(ReportSidebarPageType.TESTS);
    }


    public void assertMethodColumnContainsCorrectMethods(String filter) {
        getColumnWithoutHead(TestsTableEntries.METHOD)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertTrue(i.contains(filter),
                        String.format("Every found method [%s] should contain: %s", i, filter)));
    }

    public void assertClassColumnContainsCorrectClasses(String expectedClass) {
        getColumnWithoutHead(TestsTableEntries.CLASS)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertEquals(i, expectedClass, String.format(
                        "Class-column should contain correct only entries with correct class! [%s]", expectedClass)));
    }

    public void assertClassColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentClasses = getColumnWithoutHead(TestsTableEntries.CLASS)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        //get table head of class column
        String tableHeadClassColumn = getHeaderRow(TestsTableEntries.CLASS).getText();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Class (%s)", amountOfDifferentClasses), "Headline should contain correct number!");
    }

    public void assertStatusColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentStates = getColumnWithoutHead(TestsTableEntries.STATUS)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        //get table head of class column
        String tableHeadClassColumn = getHeaderRow(TestsTableEntries.STATUS).getText();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Status (%s)", amountOfDifferentStates), "Headline should contain correct number!");
    }

    public void assertMethodeColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentMethods = getColumnWithoutHead(TestsTableEntries.METHOD).size();
        //get table head of class column
        String tableHeadClassColumn = getHeaderRow(TestsTableEntries.METHOD).getText();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Method (%s)", amountOfDifferentMethods),
                "Headline should contain correct number!");
    }

    public void assertTestMethodIndicDoesNotAppearTwice() {
        int amountOfDifferentIndices = getColumnWithoutHead(TestsTableEntries.INDEX)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        Assert.assertEquals(amountOfDifferentIndices, tableRows.getNumberOfFoundElements(), "Each row should contains a method with an different indices!");
    }

    public void assertCorrectTestStatus(Status status) {
        getColumnWithoutHead(TestsTableEntries.STATUS)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> assertMethodStatusContainsText(status.title, i));
    }

    private void assertMethodStatusContainsText(String expectedText, String actual) {
        if (expectedText.equals("Passed")) {
            Assert.assertTrue(actual.equals(expectedText) || actual.equals("Repaired") || actual.equals("Recovered"),
                    "Test states 'Passed','Repaired' and 'Recovered' are accepted test states when state 'passed' is selected.");
        } else {
            Assert.assertEquals(actual, expectedText, "There should be only test methods with selected state displayed.");
        }
    }

    public void assertConfigurationMethodsAreDisplayed() {
        long amountOfDisplayedConfigurationMethods = getColumnWithoutHead(TestsTableEntries.METHOD)
                .stream()
                .map(GuiElement::getText)
                .filter(i -> i.contains("Configuration"))
                .count();
        Assert.assertTrue(amountOfDisplayedConfigurationMethods > 0, "Configuration methods should be listed and contain the corresponding tag!");
    }

    public ReportStepsTab navigateToStepsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final GuiElement methodNameLink = new GuiElement(getWebDriver(), By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return PageFactory.create(ReportStepsTab.class, getWebDriver());
    }

    public ReportSessionsTab navigateToSessionsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final GuiElement methodNameLink = new GuiElement(getWebDriver(), By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return PageFactory.create(ReportSessionsTab.class, getWebDriver());
    }

    public ReportDetailsTab navigateToDetailsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final GuiElement methodNameLink = new GuiElement(getWebDriver(), By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return PageFactory.create(ReportDetailsTab.class, getWebDriver());
    }

    public ReportDependenciesTab navigateToDependenciesTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final GuiElement methodNameLink = new GuiElement(getWebDriver(), By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return PageFactory.create(ReportDependenciesTab.class, getWebDriver());
    }

    public ReportStepsTab navigateToStepsTab(String methodName) {

        GuiElement subElement = pageContent.getSubElement(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();
        //GuiElement element = new GuiElement(getWebDriver(), By.xpath("//tbody//tr//td//a[text()='"+methodName+"']"));
        //element.click();

        return PageFactory.create(ReportStepsTab.class, getWebDriver());
    }

    public ReportSessionsTab navigateToSessionsTab(String methodName) {

        GuiElement subElement = pageContent.getSubElement(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();
        //GuiElement element = new GuiElement(getWebDriver(), By.xpath("//tbody//tr//td//a[text()='"+methodName+"']"));
        //element.click();

        return PageFactory.create(ReportSessionsTab.class, getWebDriver());
    }

    public ReportDetailsTab navigateToDetailsTab(String methodName) {

        GuiElement subElement = pageContent.getSubElement(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();
        //GuiElement element = new GuiElement(getWebDriver(), By.xpath("//tbody//tr//td//a[text()='"+methodName+"']"));
        //element.click();

        return PageFactory.create(ReportDetailsTab.class, getWebDriver());
    }

    public ReportDependenciesTab navigateToDependenciesTab(String methodName) {

        GuiElement subElement = pageContent.getSubElement(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();
        //GuiElement element = new GuiElement(getWebDriver(), By.xpath("//tbody//tr//td//a[text()='"+methodName+"']"));
        //element.click();

        return PageFactory.create(ReportDependenciesTab.class, getWebDriver());
    }

    public ReportTestsPage clickConfigurationMethodsSwitch() {
        configurationMethodsSwitch.click();
        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }

    public GuiElement getTestStatusSelect() {
        return this.testStatusSelect;
    }

    public int getAmountOfTableRows() {
        return tableRows.getNumberOfFoundElements();
    }

    public boolean methodGotFailureAspect(int methodIndex) {
        return getColumnWithoutHead(TestsTableEntries.METHOD).get(methodIndex).getSubElement(By.xpath("//div")).getNumberOfFoundElements() > 1;
    }

    public ReportTestsPage search(String query) {
        testSearchInput.type(query);
        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }

    public int getAmountOfEntries() {
        return getColumnWithoutHead(TestsTableEntries.STATUS).size();
    }

    public GuiElement getTestClassSelect() {
        return this.testClassSelect;
    }

    public void assertCorrectTestStates(Status status1, Status status2) {
        List<String> statesAsStrings = getColumnWithoutHead(TestsTableEntries.STATUS)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toList());

        Assert.assertTrue(statesAsStrings.contains(status1.title), "Status column should contain " + status1.title + "!");

        if (status2 != null) {
            Assert.assertTrue(statesAsStrings.contains(status2.title), "Status column should contain " + status2.title + "!");
        }
    }
}