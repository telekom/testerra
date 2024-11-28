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
import eu.tsystems.mms.tic.testframework.pageobjects.PreparedLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.AbstractReportMethodPage;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    PreparedLocator preparedMethodLinkLocator = LOCATE.prepare("//a[contains(@route-href, 'method') and text()='%s']");
    private final UiElement tableHead = pageContent.find(By.xpath(".//thead"));

    public void checkPriorityMessagesPreviewForTest(String methodName, String[] priorityMessages) {
        Optional<UiElement> methodSegment = getColumnWithoutHead(TestsTableEntry.METHOD)
                .stream()
                .filter(i -> i.expect().text().getActual().contains(methodName))
                .findFirst();
        Assert.assertTrue(methodSegment.isPresent(), "Should find table-entry with corresponding method name");
        for (String message : priorityMessages) {
            UiElement priorityMessageElement = methodSegment.get().find(By.xpath(String.format("//*[text()='%s']", message)));
            priorityMessageElement.expect().displayed().is(true);
        }
    }

    public void assertTicketString(String methodName, String expectedTicketString) {
        Optional<UiElement> methodSegment = getColumnWithoutHead(TestsTableEntry.METHOD)
                .stream()
                .filter(i -> i.expect().text().getActual().contains(methodName))
                .findFirst();
        Assert.assertTrue(methodSegment.isPresent(), "Should find table-entry with corresponding method name");
        UiElement priorityMessageElement = methodSegment.get().find(By.xpath(String.format("//*[text()='%s']", expectedTicketString)));
        priorityMessageElement.expect().displayed().is(true);
    }

    public enum TestsTableEntry {
        STATUS(0), INDEX(1), CLASS(2), METHOD(3);

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

    private List<UiElement> getColumnWithoutHead(final int columnNumber) {
        List<UiElement> column = new ArrayList<>();
        tableRows.list().forEach(row -> column.add(row.find(By.xpath("//td[" + (columnNumber + 1) + "]"))));
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
                headerRowLocator = "//th//div[contains(text(),'Class')]";
                break;
            case INDEX:
                headerRowLocator = "//th//div[contains(text(),'#')]";
                break;
            case METHOD:
                headerRowLocator = "//th//div[contains(text(),'Method')]";
                break;
        }

        return tableHead.find(By.xpath(headerRowLocator));
    }

    public void pageLoaded() {
        verifyReportPage(ReportSidebarPageType.TESTS);
    }

    public void assertMethodColumnMatchesFilter(String filter) {
        getColumnWithoutHead(TestsTableEntry.METHOD)
                .forEach(uiElement -> uiElement.expect().text().contains(filter).is(true,
                        String.format("Every found method [%s] should contain: %s", uiElement.expect().text().getActual(), filter)));
    }

    public void assertMethodColumnContainsCorrectMethods(List<String> methodNames) {
        getColumnWithoutHead(TestsTableEntry.METHOD).forEach(uiElement -> {
            String methodFromTable = uiElement.find(By.tagName("a")).waitFor().text().getActual();
            Assert.assertTrue(methodNames.contains(methodFromTable), String.format("Testmethod %s should not shown with the given filter.", methodFromTable));
        });
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
        final UiElement headerRowClass = getHeaderRow(TestsTableEntry.CLASS);
        String tableHeadClassColumn = headerRowClass.expect().text().getActual();

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
        final UiElement headerRowStatus = getHeaderRow(TestsTableEntry.STATUS);
        String tableHeadClassColumn = headerRowStatus.expect().text().getActual();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Status (%s)", amountOfDifferentStates), "Headline should contain correct number!");
    }

    public void assertMethodeColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentMethods = getColumnWithoutHead(TestsTableEntry.METHOD).size();
        //get table head of class column
        final UiElement headerRowMethod = getHeaderRow(TestsTableEntry.METHOD);
        String tableHeadClassColumn = headerRowMethod.expect().text().getActual();

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

    public ReportDetailsTab navigateToDetailsTab(final String methodName, final Status status) {

        final String methodNameLinkLocator = tableRowsLocator.concat("[.//a[text()= '" + status.title + "']]//a[text()= '" + methodName + "']");
        final UiElement methodNameLink = find(By.xpath(methodNameLinkLocator));
        methodNameLink.click();

        return createPage(ReportDetailsTab.class);
    }

    public <T extends AbstractReportMethodPage> T navigateToMethodDetails(final Class<T> reportPageClass, String methodName) {
        UiElement subElement = pageContent.find(this.preparedMethodLinkLocator.with(methodName));
        subElement.click();

        return createPage(reportPageClass);
    }

    public ReportStepsTab navigateToStepsTab(String methodName) {
        UiElement subElement = pageContent.find(this.preparedMethodLinkLocator.with(methodName));
        subElement.click();

        return createPage(ReportStepsTab.class);
    }

    public ReportDetailsTab navigateToDetailsTab(String methodName) {
        UiElement subElement = pageContent.find(this.preparedMethodLinkLocator.with(methodName));
        subElement.click();

        return createPage(ReportDetailsTab.class);
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
        testSearchInput.sendKeys(Keys.ENTER);
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
