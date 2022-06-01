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
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    @Check
    private final GuiElement tableHead = pageContent.getSubElement(By.xpath(".//thead"));
    @Check
    private final GuiElement tableRows = pageContent.getSubElement(By.xpath("//tbody//tr"));

    public ReportTestsPage(WebDriver driver) {
        super(driver);
    }

    private List<GuiElement> getColumnWithoutHead(int columnNumber) {
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
        verifyReportPage(ReportPageType.TESTS);
    }

    public void assertTableIsDisplayedCorrect() {
        assertGivenAmountOfTestIsListed();

        assertStatusColumnHeadlineContainsCorrectText();
        assertClassColumnHeadlineContainsCorrectText();

        assertStatusColumnContainsCorrectStates();
        assertClassColumnContainsCorrectClasses();
        assertMethodColumnContainsCorrectMethods();
        assertTestMethodIndicesAreInPairsDifferent();
    }

    private void assertMethodColumnContainsCorrectMethods() {
        String filter = testSearchInput.getAttribute("value").toUpperCase(Locale.ROOT);
        if (filter.equals("")) return;
        getColumnWithoutHead(3)
                .stream()
                .map(i -> i.getText().toUpperCase(Locale.ROOT))
                .forEach(i -> Assert.assertTrue(i.contains(filter), "Every found Methode should contain: " + filter));
    }

    private void assertClassColumnContainsCorrectClasses() {
        if (testClassSelect.getText().equals("Class")) return;
        String expectedClass = testClassSelect.getText().split("\n")[0];
        getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertEquals(i, expectedClass, String.format(
                        "Class-column should contain correct only entries with correct class! [%s]", expectedClass)));
    }

    private void assertGivenAmountOfTestIsListed() {
        int expectedRows = Integer.parseInt(getHeadRow().get(3).getText().split(" ")[1].replace("(", "").replace(")", ""));
        List<GuiElement> methodColumn = getColumnWithoutHead(3);
        Assert.assertEquals(new HashSet<>(methodColumn).size(), expectedRows, "There should be as many rows as the amount of tests given");
    }

    private void assertStatusColumnHeadlineContainsCorrectText() {
        int differentStatusListed = getColumnWithoutHead(0).stream().map(GuiElement::getText).collect(Collectors.toSet()).size();
        Assert.assertEquals(getHeadRow().get(0).getText(), String.format("Status (%s)", differentStatusListed), "Headline should contain correct number!");
    }

    private void assertStatusColumnContainsCorrectStates() {
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

    private void assertClassColumnHeadlineContainsCorrectText() {
        int amountOfDifferentClasses = getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet())
                .size();
        Assert.assertEquals(getHeadRow().get(1).getText(), String.format("Class (%s)", amountOfDifferentClasses), "Headline should contain correct number!");
    }

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

    private void selectTestStateFilter(String stateName) {
        testStatusSelect.click();

        //code line below should work, but does not. No clue why... (throws 'ElementNotFound' Exception)
        //testStatusSelect.getSubElement(By.xpath(String.format("//mdc-list-item[contains(text(), '%s')]", stateName))).click();

        //alternative:
        Optional<GuiElement> optionalGuiElement = testStatusSelect.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .filter(i -> i.getText().contains(stateName))
                .findFirst();
        Assert.assertTrue(optionalGuiElement.isPresent());
        optionalGuiElement.get().click();
    }

    public void assertCorrectTableWhenLoopingThroughClasses() {
        Set<String> classes = getColumnWithoutHead(1).stream().map(GuiElement::getText).collect(Collectors.toSet());
        for (String className : classes) {
            selectClass(className);
            assertTableIsDisplayedCorrect();
        }
    }

    private void selectClass(String className) {
        testClassSelect.click();

        //code line below should work, but does not. No clue why... (throws 'ElementNotFound' Exception)
        //testClassSelect.getSubElement(By.xpath(String.format("//mdc-list-item[contains(text(), '%s')]", className))).click();

        //alternative:
        Optional<GuiElement> optionalGuiElement = testClassSelect.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .filter(i -> i.getText().contains(className))
                .findFirst();
        Assert.assertTrue(optionalGuiElement.isPresent());
        optionalGuiElement.get().click();
    }

    public void assertCorrectTableWhenLoopingThroughMethods() {
        Set<String> methods = getColumnWithoutHead(3)
                .stream()
                .map(i -> i.getSubElement(By.xpath("//a[contains(text(), 'test')]")))
                .map(GuiElement::getText)
                .collect(Collectors.toSet());
        for (String methodName : methods) {
            testSearchInput.type(methodName);
            TimerUtils.sleep(1000, "Necessary sleep  gives enough time to refresh all locator");
            assertTableIsDisplayedCorrect();
            testSearchInput.clear();
        }
    }

    public void assertShowConfigurationMethodsButtonDisplaysMoreMethods() {
        String amountOfMethodsBeforeSwitchingAsString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY,
                getHeadRow().get(3).getText().split(" ")[1]);
        int amountOfMethodsBeforeSwitching = Integer.parseInt(amountOfMethodsBeforeSwitchingAsString);
        configurationMethodsSwitch.click();
        String amountOfMethodsAfterSwitchingAsString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY,
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

    public void assertCorrectTableWhenLoopingThroughFailureAspect() {
        Set<String> advices = getColumnWithoutHead(3)
                .stream()
                .map(i -> i.getSubElement(By.xpath("/div")).getList())
                .filter(i -> i.size() > 1)
                .map(i -> i.get(i.size() - 1))
                .map(i -> i.getText().split(":")[0])
                .collect(Collectors.toSet());
        for (String advice : advices) {
            testSearchInput.type(advice);
            TimerUtils.sleep(1000, "Necessary sleep gives enough time to refresh all locator");
            assertTableIsDisplayedCorrect();
            testSearchInput.clear();
        }
    }

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

    public void LoopThroughPossibleTestStateListAndAssertTableIsDisplayedCorrect(List<String> testStates) {
        for (String stateName : testStates) {
            selectTestStateFilter(stateName);
            assertTableIsDisplayedCorrect();
        }
    }
}
