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
    private final UiElement testStatusSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Status']"));
    @Check
    private final UiElement testClassSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Class']"));
    @Check
    private final UiElement testSearchInput = pageContent.find(By.xpath(".//input[contains(@class, 'mdc-text-field__input')]"));
    @Check
    private final UiElement configurationMethodsSwitch = pageContent.find(By.xpath("//button[@role='switch']"));

    private final String tableRowsLocator = "//tbody//tr";
    private final UiElement tableRows = pageContent.find(By.xpath(tableRowsLocator));

    private final String methodLinkLocator = "//tbody//tr//td//a[text()='%s']";
    private final UiElement tableHead = pageContent.find(By.xpath(".//thead"));

    public enum TestsTableEntry {
        STATUS(0), CLASS(1), INDEX(2), METHOD(3);

        private final int value;

        TestsTableEntry(final int value) {
            this.value = value;
        }

        public int index() {
            return this.value;
        }
    }


    public ReportTestsPage(WebDriver driver) {
        super(driver);
    }

    public List<UiElement> getColumnWithoutHead(int columnNumber) {
        List<UiElement> column = new ArrayList<>();
        for (UiElement row : tableRows.list().stream().collect(Collectors.toList())) {
            column.add(row.find(By.xpath("//td")).list().stream().collect(Collectors.toList()).get(columnNumber));
        }
        return column;
    }


    public List<UiElement> getColumnWithoutHead(TestsTableEntry tableEntry) {
        return getColumnWithoutHead(tableEntry.index());
    }

    private UiElement getHeaderRow(TestsTableEntry tableEntry) {

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

        return tableHead.find(By.xpath(headerRowLocator));
    }


    //TODO:: FRAGE: fehler in Testerra? sollte 'Abstract Page' eine @Deprecated-methode aufrufen?
    public void assertPageIsShown() {
        verifyReportPage(ReportSidebarPageType.TESTS);
    }


    public void assertMethodColumnContainsCorrectMethods(String filter) {
        getColumnWithoutHead(TestsTableEntry.METHOD)
                .forEach(uiElement -> uiElement.expect().text().contains(filter).is(true,
                        String.format("Every found method [%s] should contain: %s", uiElement.expect().text().getActual(), filter)));
    }

    public void assertClassColumnContainsCorrectClasses(String expectedClass) {
        getColumnWithoutHead(TestsTableEntry.CLASS)
                .forEach(uiElement -> uiElement.expect().text().is(expectedClass, String.format(
                        "Class-column should contain correct only entries with correct class! [%s]", expectedClass)));
    }

    public void assertClassColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentClasses = getColumnWithoutHead(TestsTableEntry.CLASS)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toSet())
                .size();
        //get table head of class column
        String tableHeadClassColumn = getHeaderRow(TestsTableEntry.CLASS).expect().text().getActual();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Class (%s)", amountOfDifferentClasses), "Headline should contain correct number!");
    }

    public void assertStatusColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentStates = getColumnWithoutHead(TestsTableEntry.STATUS)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toSet())
                .size();
        //get table head of class column
        String tableHeadClassColumn = getHeaderRow(TestsTableEntry.STATUS).expect().text().getActual();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Status (%s)", amountOfDifferentStates), "Headline should contain correct number!");
    }

    public void assertMethodeColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentMethods = getColumnWithoutHead(TestsTableEntry.METHOD).size();
        //get table head of class column
        String tableHeadClassColumn = getHeaderRow(TestsTableEntry.METHOD).expect().text().getActual();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Method (%s)", amountOfDifferentMethods),
                "Headline should contain correct number!");
    }

    public void assertTestMethodIndicDoesNotAppearTwice() {
        int amountOfDifferentIndices = getColumnWithoutHead(TestsTableEntry.INDEX)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toSet())
                .size();
        Assert.assertEquals(amountOfDifferentIndices, tableRows.list().stream().count(), "Each row should contains a method with an different indices!");
    }

    public void assertCorrectTestStatus(Status status) {
        getColumnWithoutHead(TestsTableEntry.STATUS)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
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
        long amountOfDisplayedConfigurationMethods = getColumnWithoutHead(TestsTableEntry.METHOD)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .filter(i -> i.contains("Configuration"))
                .count();
        Assert.assertTrue(amountOfDisplayedConfigurationMethods > 0, "Configuration methods should be listed and contain the corresponding tag!");
    }

    public ReportStepsTab navigateToStepsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final UiElement methodNameLink = find(By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return createPage(ReportStepsTab.class);
    }

    public ReportSessionsTab navigateToSessionsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final UiElement methodNameLink = find(By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return createPage(ReportSessionsTab.class);
    }

    public ReportDetailsTab navigateToDetailsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final UiElement methodNameLink = find(By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return createPage(ReportDetailsTab.class);
    }

    public ReportDependenciesTab navigateToDependenciesTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final UiElement methodNameLink = find(By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return createPage(ReportDependenciesTab.class);
    }

    public ReportStepsTab navigateToStepsTab(String methodName) {

        UiElement subElement = pageContent.find(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();

        return createPage(ReportStepsTab.class);
    }

    public ReportSessionsTab navigateToSessionsTab(String methodName) {

        UiElement subElement = pageContent.find(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();

        return createPage(ReportSessionsTab.class);
    }

    public ReportDetailsTab navigateToDetailsTab(String methodName) {

        UiElement subElement = pageContent.find(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();

        return createPage(ReportDetailsTab.class);
    }

    public ReportDependenciesTab navigateToDependenciesTab(String methodName) {

        UiElement subElement = pageContent.find(By.xpath(String.format(methodLinkLocator, methodName)));
        subElement.click();

        return createPage(ReportDependenciesTab.class);
    }

    public ReportTestsPage clickConfigurationMethodsSwitch() {
        configurationMethodsSwitch.click();
        return createPage(ReportTestsPage.class);
    }

    public int getAmountOfTableRows() {
        return (int) tableRows.list().stream().count();
    }

    public ReportTestsPage search(String query) {
        testSearchInput.type(query);
        return createPage(ReportTestsPage.class);
    }

    public int getAmountOfEntries() {
        return getColumnWithoutHead(TestsTableEntry.STATUS).size();
    }

    public void assertCorrectTestStates(List<Status> statusList) {
        List<String> statesAsStrings = getColumnWithoutHead(TestsTableEntry.STATUS)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());

        for (Status status : statusList) {
            Assert.assertTrue(statesAsStrings.contains(status.title), "Status column should contain " + status.title + "!");
        }
    }

    public ReportTestsPage selectTestStatus(Status status) {
        return selectDropBoxElement(this.testStatusSelect, status.title, ReportTestsPage.class);
    }

    public ReportTestsPage selectClassName(String label) {
        return selectDropBoxElement(this.testClassSelect, label, ReportTestsPage.class);
    }
}