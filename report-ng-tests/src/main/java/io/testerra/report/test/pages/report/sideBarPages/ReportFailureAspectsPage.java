package io.testerra.report.test.pages.report.sideBarPages;

import com.google.common.collect.Streams;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import io.testerra.report.test.pages.AbstractReportPage;

public class ReportFailureAspectsPage extends AbstractReportPage {

    @Check
    private final GuiElement testTypeSelect = pageContent.getSubElement(By.xpath("//mdc-select[@label='Type']"));

    @Check
    private final GuiElement testInputSearch = pageContent.getSubElement(By.xpath("//label[@label='Search']//input"));

    @Check
    private final GuiElement testShowExpectedFailsButton = pageContent.getSubElement(By.xpath("//button[@role='switch']"));

    @Check
    private final GuiElement failureAspectsTable = pageContent.getSubElement(By.xpath("//tbody[@ref='content']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportFailureAspectsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * return specified columns by index
     * @param column
     * @return
     */
    public List<GuiElement> getColumns(int column) {
        List<GuiElement> returnList = new ArrayList<>();
        for (GuiElement tableRows : failureAspectsTable.getSubElement(By.xpath("//tr")).getList()) {
            List<GuiElement> tableColumns = tableRows.getSubElement(By.xpath("//td")).getList();
            returnList.add(tableColumns.get(column));
        }
        return returnList;
    }

    private List<GuiElement> getRows(int row) {
        return failureAspectsTable.getSubElement(By.xpath("//tr")).getList().get(row).getSubElement(By.xpath("//td")).getList();
    }

    public boolean getFailedStateExistence() {
        List<GuiElement> tableRows = failureAspectsTable.getList();
        boolean statusContainsFailed = false;
        for (GuiElement row : tableRows) {
            statusContainsFailed = false;
            for (GuiElement status : row.getSubElement(By.xpath("(//td)[3]/div")).getList()) {
                if (status.getText().contains(Status.FAILED.title)) {
                    if (status.getText().split(" ").length > 2)
                        continue;    //skips expected fails
                    statusContainsFailed = true;
                }
            }
        }
        return statusContainsFailed;
    }

    public List<String> getOrderListOfTopFailureAspects() {
        return getColumns(1)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toList());
    }

    // TODO: not all called methods here are relevant when calling the base method --> second one has a return in case of no search input
    //  refactoring needed: call needed methods separately in tests, than generalization might be an option
    public void assertFailureAspectsTableIsDisplayedCorrect() {
        assertRankColumnDoesNotContainDuplicate();
        // TODO: avoid: only needed when search occurred --> call in specific test
        assertFailureAspectsColumnContainsCorrectAspects();
        // TODO: avoid: only needed when filtering occurred --> call in specific test
        assertStatusColumnContainsCorrectStates();
    }

    private void assertRankColumnDoesNotContainDuplicate() {
        final List<GuiElement> columns = getColumns(0);

        // TODO: Why? columns.size should be the same
        final int amountOfRows = new HashSet<>(columns).size();
        // TODO: what is done here? there is no filter of any kind, so basically it is all the same as getColumns size
        final int amountOfDifferentRanks = columns
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet()).size();
        Assert.assertEquals(amountOfDifferentRanks, amountOfRows, "There should be as many rows as ranks, one for each!");
    }

    private void assertFailureAspectsColumnContainsCorrectAspects() {
        String filter = testInputSearch.getAttribute("value").toUpperCase(Locale.ROOT);


        // TODO: this hides unwanted assert skips --> avoid, call method from test, when it is clear if input occurred before;
        //  expected value as parameter, not from element reading
        if (filter.equals("")) return;


        getColumns(1)
                .stream()
                .map(GuiElement::getText)
                .map(String::toUpperCase)
                .forEach(i -> Assert.assertTrue(i.contains(filter),
                        String.format("All listed aspects should match the given filter.[Actual: %s] [Expected: %s]", i, filter)));
    }

    // TODO: assert might be skipped but not wanted to; call directly from test when selection is known
    private void assertStatusColumnContainsCorrectStates() {
        if (testTypeSelect.getText().equals("Major")) {
            Assert.assertTrue(getFailedStateExistence(), "There should be failed states in every row!");
        }
        if (testTypeSelect.getText().equals("Minor")) {
            Assert.assertFalse(getFailedStateExistence(), "There should not be any failed states in a row!");
        }
    }

    // TODO: Why Hashsets? getColumns returns a list already; avoid action in assert methods, make actions in test and call assert from test
    public void assertShowExpectedFailedButtonWorksCorrectly(){
        int amountOfAspectsButtonEnabled = new HashSet<>(getColumns(2)).size();
        disableButton();
        int amountOfAspectsButtonDisabled = new HashSet<>(getColumns(2)).size();
        Assert.assertTrue(amountOfAspectsButtonDisabled < amountOfAspectsButtonEnabled,
                "There should be more aspects listed, when expected fails button is enabled!");
    }

    // TODO: separate action from assert, call assert after each selection in the test
    public void assertFailureAspectTableIsCorrectDisplayedWhenIteratingThroughSelectableTypes() {

        //Minor
        selectDropBoxElement(testTypeSelect, "Minor");
        // TODO: check only needed stuff --> do not make general methods, with if dependent asserts
        assertFailureAspectsTableIsDisplayedCorrect();

        //Major
        selectDropBoxElement(testTypeSelect, "Major");
        // TODO: check only needed stuff --> do not make general methods, with if dependent asserts
        assertFailureAspectsTableIsDisplayedCorrect();
    }

    // TODO: separate action from assert, call assert after each selection in the test
    public void assertFailureAspectTableIsCorrectDisplayedWhenSearchingForDifferentAspects() {

        Set<String> failureAspects = getColumns(1)
                .stream()
                .map(i -> i.getText().split(":")[0])
                .collect(Collectors.toSet());

        for (String aspect : failureAspects) {
            testInputSearch.type(aspect);
            // TODO: avoid sleeps, use page object pattern with instantiation of Pages after actions
            TimerUtils.sleep(1000, "Necessary sleep gives page enough time to refresh page table content and locator");
            assertFailureAspectsTableIsDisplayedCorrect();
            testInputSearch.clear();
        }
    }

    // TODO: assert and action in one method, do actions in test and assert afterwards
    //  add fitting method name, if method is need at all
    public void disableButton() {
        Assert.assertEquals(testShowExpectedFailsButton.getAttribute("aria-checked"), "true", "Button should be enabled!");
        testShowExpectedFailsButton.click();
    }

    public ReportTestsPage clickStateLink(String observedFailureAspect, Status expectedState) {
        for(int i = 0; i < getColumns(0).size(); i++){
            if (getRows(i).get(1).getText().equals(observedFailureAspect)){
                getRows(i).get(2).getSubElement(By.xpath(String.format("//a[contains(text(), '%s')]", expectedState.title))).click();
                return PageFactory.create(ReportTestsPage.class, getWebDriver());
            }
        }
        return null;
    }

    public ReportTestsPage clickFailureAspectLink(GuiElement failureAspectLink) {
        failureAspectLink.getSubElement(By.xpath("//a")).click();
        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }
}