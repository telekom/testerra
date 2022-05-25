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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.*;
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

    private List<GuiElement> getRow(int rowNumber) {
        GuiElement row = tableRows.getList().get(rowNumber);
        return row.getSubElement(By.xpath("//td")).getList();
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
                .map(i -> i.getSubElement(By.xpath("//*[contains(text(), 'test')]")))
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertEquals(i.toUpperCase(Locale.ROOT), filter));
    }

    private void assertClassColumnContainsCorrectClasses() {
        if (testClassSelect.getText().equals("Class")) return;
        String expectedClass = testClassSelect.getText().split("\n")[0];
        getColumnWithoutHead(1)
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertEquals(i, expectedClass));
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
            Assert.assertTrue(actual.equals(expectedText) || actual.equals("Repaired"));
        } else {
            Assert.assertEquals(actual, expectedText);
        }
    }

    private void assertClassColumnHeadlineContainsCorrectText() {
        int amountOfDifferentClasses = getColumnWithoutHead(1).stream().map(GuiElement::getText).collect(Collectors.toSet()).size();
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

    public void selectTestStateFilter(Status status) {
        testStatusSelect.click();
        Optional<GuiElement> guiElementOptional = testStatusSelect.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .filter(i -> i.getText().equals(status.title))
                .findFirst();
        Assert.assertTrue(guiElementOptional.isPresent());
        guiElementOptional.get().click();
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
        Optional<GuiElement> optionalGuiElement = testClassSelect.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .filter(i -> i.getText().equals(className))
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
            TimerUtils.sleep(5000, "Necessary sleep  gives enough time to refresh all locator");
            assertTableIsDisplayedCorrect();
            testSearchInput.clear();
        }
    }

    public void assertShowConfigurationMethodsButtonDisplaysMoreMethods() {
        int amountOfMethodsBeforeSwitching = Integer.parseInt(getHeadRow().get(3).getText().split(" ")[1].replace("(", "").replace(")", ""));
        configurationMethodsSwitch.click();
        int amountOfMethodsAfterSwitching = Integer.parseInt(getHeadRow().get(3).getText().split(" ")[1].replace("(", "").replace(")", ""));
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
}
