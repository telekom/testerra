package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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

    private List<GuiElement> getColumn(int column) {
        List<GuiElement> returnList = new ArrayList<>();
        for (GuiElement element : failureAspectsTable.getSubElement(By.xpath("//tr")).getList()) {
            returnList.add(element.getSubElement(By.xpath("//td")).getList().get(column));
        }
        return returnList;
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
        return getColumn(1)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toList());
    }

    public void assertFailureAspectsTableIsDisplayedCorrect() {
        assertRankColumnDoesNotContainDuplicate();
        assertFailureAspectsColumnContainsCorrectAspects();
        assertStatusColumnContainsCorrectStates();
    }

    private void assertRankColumnDoesNotContainDuplicate() {
        int amountOfRows = new HashSet<>(getColumn(0)).size();
        int amountOfDifferentRanks = getColumn(0)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toSet()).size();
        Assert.assertEquals(amountOfDifferentRanks, amountOfRows, "There should be as many rows as ranks, one for each!");
    }

    private void assertFailureAspectsColumnContainsCorrectAspects() {
        String filter = testInputSearch.getAttribute("value").toUpperCase(Locale.ROOT);
        if (filter.equals("")) return;
        getColumn(1)
                .stream()
                .map(GuiElement::getText)
                .map(String::toUpperCase)
                .forEach(i -> Assert.assertTrue(i.contains(filter),
                        String.format("All listed aspects should match the given filter.[Actual: %s] [Expected: %s]", i, filter)));
    }

    private void assertStatusColumnContainsCorrectStates() {
        if (testTypeSelect.getText().equals("Major")) {
            Assert.assertTrue(getFailedStateExistence(), "There should be failed states in every row!");
        }
        if (testTypeSelect.getText().equals("Minor")) {
            Assert.assertFalse(getFailedStateExistence(), "There should not be any failed states in a row!");
        }
    }

    public void assertShowExpectedFailedButtonWorksCorrectly(){
        int amountOfAspectsButtonEnabled = new HashSet<>(getColumn(2)).size();
        disableButton();
        int amountOfAspectsButtonDisabled = new HashSet<>(getColumn(2)).size();
        Assert.assertTrue(amountOfAspectsButtonDisabled < amountOfAspectsButtonEnabled,
                "There should be more aspects listed, when expected fails button is enabled!");
    }

    public void assertFailureAspectTableIsCorrectDisplayedWhenIteratingThroughSelectableTypes() {

        //Minor
        selectDropBoxElement(testTypeSelect, "Minor");
        assertFailureAspectsTableIsDisplayedCorrect();

        //Major
        selectDropBoxElement(testTypeSelect, "Major");
        assertFailureAspectsTableIsDisplayedCorrect();
    }

    public void assertFailureAspectTableIsCorrectDisplayedWhenSearchingForDifferentAspects() {
        Set<String> failureAspects = getColumn(1)
                .stream()
                .map(i -> i.getText().split(":")[0])
                .collect(Collectors.toSet());

        for (String aspect : failureAspects) {
            testInputSearch.type(aspect);
            TimerUtils.sleep(1000, "Necessary sleep gives page enough time to refresh page table content and locator");
            assertFailureAspectsTableIsDisplayedCorrect();
            testInputSearch.clear();
        }
    }

    public void disableButton() {
        Assert.assertEquals(testShowExpectedFailsButton.getAttribute("aria-checked"), "true", "Button should be enabled!");
        testShowExpectedFailsButton.click();
    }
}