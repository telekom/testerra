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
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportMethodPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final GuiElement tableHead = pageContent.getSubElement(By.xpath(".//thead"));
    private final GuiElement tableRows = pageContent.getSubElement(By.xpath("//tbody//tr"));

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

    private List<GuiElement> getHeadRow() {
        return tableHead.getSubElement(By.xpath("//tr//th")).getList();
    }

    public void assertPageIsShown() {
        verifyReportPage(ReportSidebarPageType.TESTS);
    }


    public void assertMethodColumnContainsCorrectMethods(String filter) {
        //TODO: add readable method for getting part of column in row with enums: parameter to method for switching index to use
        // (--> ?)
        getColumnWithoutHead(3)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertTrue(i.contains(filter),
                        String.format("Every found method [%s] should contain: %s", i, filter)));
    }

    public void assertClassColumnContainsCorrectClasses(String expectedClass) {
        getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertEquals(i, expectedClass, String.format(
                        "Class-column should contain correct only entries with correct class! [%s]", expectedClass)));
    }

    public void assertClassColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentClasses = getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        //get table head of class column
        String tableHeadClassColumn = getHeadRow().get(1).getText();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Class (%s)", amountOfDifferentClasses), "Headline should contain correct number!");
    }

    public void assertStatusColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentStates = getColumnWithoutHead(0)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        //get table head of class column
        String tableHeadClassColumn = getHeadRow().get(0).getText();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Status (%s)", amountOfDifferentStates), "Headline should contain correct number!");
    }

    public void assertMethodeColumnHeadlineContainsCorrectText() {
        // counts amount of different displayed classes
        int amountOfDifferentMethods = getColumnWithoutHead(3).size();
        //get table head of class column
        String tableHeadClassColumn = getHeadRow().get(3).getText();

        //compare
        Assert.assertEquals(tableHeadClassColumn, String.format("Method (%s)", amountOfDifferentMethods),
                "Headline should contain correct number!");
    }

    public void assertTestMethodIndicDoesNotAppearTwice() {
        int amountOfDifferentIndices = getColumnWithoutHead(2)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        Assert.assertEquals(amountOfDifferentIndices, tableRows.getNumberOfFoundElements(), "Each row should contains a method with an different indices!");
    }

    public void assertCorrectTestStatus(Status status) {
        getColumnWithoutHead(0)
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
        int amountOfDisplayedConfigurationMethods = (int) getColumnWithoutHead(3)
                .stream()
                .map(GuiElement::getText)
                .filter(i -> i.contains("Configuration"))
                .count();
        Assert.assertTrue(amountOfDisplayedConfigurationMethods > 0, "Configuration methods should be listed and contain the corresponding tag!");
    }


    public ReportMethodPage navigateToMethodReport(String methodName, ReportMethodPageType pageType) {
        Optional<GuiElement> optionalLink = getColumnWithoutHead(3).stream().filter(i -> i.getText().contains(methodName)).findFirst();
        if (optionalLink.isPresent()) {
            optionalLink.get().getSubElement(By.xpath("//a")).click();
        } else {
            Assert.fail(String.format("Method %s not displayed!", methodName));
        }
        ReportMethodPage reportMethodPage = PageFactory.create(ReportMethodPage.class, getWebDriver());
        reportMethodPage.setActivePage(pageType);
        return reportMethodPage;
    }

    public List<String[]> getTable() {
        // TODO: use map with talking key names for better usability and readability
        // (--> how? separation in rows should still remain?)
        List<String[]> table = new ArrayList<>();
        for (GuiElement row : tableRows.getList()) {
            List<GuiElement> columns = row.getSubElement(By.xpath("//td")).getList();
            String[] stringRow = new String[4];
            stringRow[0] = columns.get(0).getText();
            stringRow[1] = columns.get(1).getText();
            stringRow[2] = columns.get(2).getText();
            stringRow[3] = columns.get(3).getSubElement(By.xpath("//a")).getText();
            table.add(stringRow);
        }
        return table;
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
        return getColumnWithoutHead(3).get(methodIndex).getSubElement(By.xpath("//div")).getNumberOfFoundElements() > 1;
    }

    public ReportTestsPage search(String query) {
        testSearchInput.type(query);
        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }

    public int getAmountOfEntries() {
        return getColumnWithoutHead(0).size();
    }

    public GuiElement getTestClassSelect() {
        return this.testClassSelect;
    }

    public void assertCorrectTestStates(Status status1, Status status2) {
        List<String> statesAsStrings = getColumnWithoutHead(0)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toList());
        Assert.assertTrue(statesAsStrings.contains(status1.title), "Status column should contain " + status1.title + "!");
        if (status2 == null) return;
        Assert.assertTrue(statesAsStrings.contains(status2.title), "Status column should contain " + status2.title + "!");
    }
}