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
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportMethodPage;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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

    // TODO: avoid general method with skipping asserts
    public void assertTableIsDisplayedCorrect() {
        assertGivenAmountOfTestIsListed();

        assertStatusColumnHeadlineContainsCorrectText();
        assertClassColumnHeadlineContainsCorrectText();

        // TODO: not always needed, why add it in general method?, call only from tests that need to verify this
        assertStatusColumnContainsCorrectStates();
        assertClassColumnContainsCorrectClasses();
        assertMethodColumnContainsCorrectMethods();

        assertTestMethodIndicesAreInPairsDifferent();
    }

    private void assertMethodColumnContainsCorrectMethods() {
        // TODO: avoid/remove possible assert skips, separate action from assert, call this method from test, when need to, expected search as parameter
        String filter = testSearchInput.getAttribute("value").toUpperCase(Locale.ROOT);
        if (filter.equals("")) return;
        // TODO: add readable method for getting part of column in row with enums: parameter to method for switching index to use
        getColumnWithoutHead(3)
                .stream()
                .map(i -> i.getText().toUpperCase(Locale.ROOT))
                .forEach(i -> Assert.assertTrue(i.contains(filter), "Every found Methode should contain: " + filter));
    }

    private void assertClassColumnContainsCorrectClasses() {
        // TODO: avoid/remove possible assert skips, separate action from assert, call this method from test, when need to, expected class as parameter
        if (testClassSelect.getText().equals("Class")) return;
        String expectedClass = testClassSelect.getText().split("\n")[0];
        getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertEquals(i, expectedClass, String.format(
                        "Class-column should contain correct only entries with correct class! [%s]", expectedClass)));
    }

    // TODO: fitting method name, description of method, checking amount of overall rows equals displayed amount of methods in last column of table header
    private void assertGivenAmountOfTestIsListed() {
        // TODO: use regex, avoid multiple method calls in one line, comment what is done here exactly, add readable method for getting HEaderRows with enums: parameter to method for switching index to use
        int expectedRows = Integer.parseInt(getHeadRow().get(3).getText().split(" ")[1].replace("(", "").replace(")", ""));
        List<GuiElement> methodColumn = getColumnWithoutHead(3);
        // TODO: why not use list size directly?
        Assert.assertEquals(new HashSet<>(methodColumn).size(), expectedRows, "There should be as many rows as the amount of tests given");
    }

    // TODO: fitting method name, description of method, checking amount of different status row equals displayed amount of status in first column of table header
    private void assertStatusColumnHeadlineContainsCorrectText() {
        // TODO: avoid multiple method calls, comment what is done here exactly, add readable method for getting part of column in row with enums: parameter to method for switching index to use
        int differentStatusListed = getColumnWithoutHead(0).stream().map(GuiElement::getText).collect(Collectors.toSet()).size();
        // TODO: add readable method for getting HeaderRows with enums: parameter to method for switching index to use, use reg ex to read amount from header and assert with int of former line
        Assert.assertEquals(getHeadRow().get(0).getText(), String.format("Status (%s)", differentStatusListed), "Headline should contain correct number!");
    }

    private void assertStatusColumnContainsCorrectStates() {
        // TODO: avoid/remove possible assert skips, separate action from assert, call this method from test, when need to, expected state as parameter
        if (testStatusSelect.getText().equals("Status")) return;
        String expectedState = testStatusSelect.getText().split("\n")[0];
        getColumnWithoutHead(0)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> assertMethodStatusContainsText(expectedState, i));
    }

    private void assertMethodStatusContainsText(String expectedText, String actual) {
        if (expectedText.equals("Passed")) {
            Assert.assertTrue(actual.equals(expectedText) || actual.equals("Repaired") || actual.equals("Recovered"),
                    "Test states 'Passed','Repaired' and 'Recovered' are accepted test states when state 'passed' is selected.");
        } else {
            Assert.assertEquals(actual, expectedText, "There should be only test methods with selected state displayed.");
        }
    }

    // TODO: fitting method name, description of method,
    private void assertClassColumnHeadlineContainsCorrectText() {
        // TODO: comment what is done here exactly, add readable method for getting part of column in row with enums: parameter to method for switching index to use
        int amountOfDifferentClasses = getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        // TODO: avoid multiple method calls, add readable method for getting HEaderRows with enums: parameter to method for switching index to use, use reg ex to read amount from header and assert with int of former line
        Assert.assertEquals(getHeadRow().get(1).getText(), String.format("Class (%s)", amountOfDifferentClasses), "Headline should contain correct number!");
    }

    // TODO: fitting method name
    private void assertTestMethodIndicesAreInPairsDifferent() {
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
                .forEach(i -> Assert.assertEquals(i, status.title, "There should be only methods with expected status listed"));
    }

    public void assertCorrectTestStates(Status... severalStatus) {
        getColumnWithoutHead(0)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertTrue(Arrays.stream(severalStatus).map(status -> status.title).anyMatch(statusTitle -> statusTitle.equals(i)), "There should be only methods with expected states listed"));
    }

    // TODO: separate action and assert, select in test, assert from test
    public void assertCorrectTableWhenLoopingThroughClasses() {
        Set<String> classes = getColumnWithoutHead(1).stream().map(GuiElement::getText).collect(Collectors.toSet());
        for (String className : classes) {
            selectDropBoxElement(testClassSelect, className);
            // TODO: assert only aspects that can be asserted, do not use general method that does implicit distinctions
            assertTableIsDisplayedCorrect();
        }
    }

    // TODO: separate action and asserts: search in test, assert in test
    public void assertCorrectTableWhenLoopingThroughMethods() {
        Set<String> methods = getColumnWithoutHead(3)
                .stream()
                // TODO: use explicit DataProvier with test some methodnames
                .map(i -> i.getSubElement(By.xpath("//a[contains(text(), 'test')]")))
                .map(GuiElement::getText)
                .collect(Collectors.toSet());
        for (String methodName : methods) {
            // TODO: implement working search method on page with clear as first step
            testSearchInput.type(methodName);
            // TODO: implement correct POP with Page Instantiation, no explicit sleeps
            TimerUtils.sleep(1000, "Necessary sleep  gives enough time to refresh all locator");
            // TODO: assert only aspects that can be asserted, and call from test
            assertTableIsDisplayedCorrect();
            testSearchInput.clear();
        }
    }

    // TODO: separate assert and action
    //  get amount from test, activate button in test, get amoint in test, assert in test
    public void assertShowConfigurationMethodsButtonDisplaysMoreMethods() {
        String amountOfMethodsBeforeSwitchingAsString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY,
                // TODO: add readable method for getting HEaderRows with enums: parameter to method for switching index to use
                //  why splitting?
                //  tidy up get text outside method call
                getHeadRow().get(3).getText().split(" ")[1]);
        int amountOfMethodsBeforeSwitching = Integer.parseInt(amountOfMethodsBeforeSwitchingAsString);
        configurationMethodsSwitch.click();
        String amountOfMethodsAfterSwitchingAsString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY,
                // TODO: add readable method for getting HEaderRows with enums: parameter to method for switching index to use
                //  why splitting?
                //  tidy up get text outside method call
                getHeadRow().get(3).getText().split(" ")[1]);
        int amountOfMethodsAfterSwitching = Integer.parseInt(amountOfMethodsAfterSwitchingAsString);
        Assert.assertTrue(amountOfMethodsAfterSwitching > amountOfMethodsBeforeSwitching, "There should be some configuration methods added!");
    }

    public void assertConfigurationMethodsAreDisplayed() {
        int amountOfDisplayedConfigurationMethods = (int) getColumnWithoutHead(3)
                .stream()
                .map(GuiElement::getText)
                .filter(i -> i.contains("Configuration"))
                .count();
        Assert.assertTrue(amountOfDisplayedConfigurationMethods > 0, "Configuration methods should be listed and contain the corresponding tag!");
    }

    // TODO: separate action and assert: get possible aspects from test, search in test, assert in test
    public void assertCorrectTableWhenLoopingThroughFailureAspect() {
        // TODO: comment what is done here: finding failure aspects
        Set<String> advices = getColumnWithoutHead(3)
                .stream()
                .map(i -> i.getSubElement(By.xpath("/div")).getList())
                .filter(i -> i.size() > 1)
                .map(i -> i.get(i.size() - 1))
                .map(i -> i.getText().split(":")[0])
                .collect(Collectors.toSet());
        for (String advice : advices) {
            // TODO: implement working search method on page with clear as first step
            testSearchInput.type(advice);
            // TODO: implement correct POP with Page Instantiation, no explicit sleeps
            TimerUtils.sleep(1000, "Necessary sleep gives enough time to refresh all locator");
            // TODO: assert only aspects that can be asserted, and call from test
            assertTableIsDisplayedCorrect();
            testSearchInput.clear();
        }
    }


    // TODO: needed? TestStatus already existing as enum and usable in test as dataprovider
    public List<String> getListOfAllSelectableStates() {
        testStatusSelect.click();
        List<String> returnList = testStatusSelect.getSubElement(By.xpath("//mdc-menu//mdc-list-item")).getList()
                .stream()
                .map(GuiElement::getText)
                .filter(i -> !i.contains("All"))
                .collect(Collectors.toList());
        testStatusSelect.click();
        TimerUtils.sleep(1000, "Necessary sleep gives some time to refresh status selection");
        return returnList;
    }

    // TODO: separate action and assert: select status in test, assert in test
    public void LoopThroughPossibleTestStateListAndAssertTableIsDisplayedCorrect(List<String> testStates) {
        for (String stateName : testStates) {
            selectDropBoxElement(testStatusSelect, stateName);
            assertTableIsDisplayedCorrect();
        }
    }

    public ReportMethodPage navigateToMethodReport(int methodIndex) {
        GuiElement rowEntries = tableRows.getList().get(methodIndex).getSubElement(By.xpath("//td"));
        // TODO: why assert?
        rowEntries.asserts().assertIsDisplayed();
        rowEntries.getList().get(3).getSubElement(By.xpath("//a")).click();
        return PageFactory.create(ReportMethodPage.class, getWebDriver());
    }

    public List<String[]> getTable() {
        // TODO: use map with talking key names for better usability and readability
        List<String[]> table = new ArrayList<>();
        for(GuiElement row : tableRows.getList()){
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

    public void clickConfigurationMethodsSwitch() {
        configurationMethodsSwitch.click();
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

    public void search(String query) {
        testSearchInput.type(query);
    }

    public void clearSearchbar() {
        testSearchInput.clear();
    }
}