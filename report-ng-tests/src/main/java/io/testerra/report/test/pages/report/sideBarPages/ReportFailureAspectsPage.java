package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public void assertRankColumnDescends() {
        final List<GuiElement> columns = getColumns(0);
        int expectedRankNumber = 1;

        for (GuiElement column : columns) {
            Assert.assertEquals(Integer.parseInt(column.getText()), expectedRankNumber, "Rank column should descend and contain no duplicate!");
            expectedRankNumber++;
        }
    }

    public void assertFailureAspectsColumnContainsCorrectAspects(String filter) {
        getColumns(1)
                .stream()
                .map(GuiElement::getText)
                .map(String::toUpperCase)
                .forEach(i -> Assert.assertTrue(i.contains(filter.toUpperCase(Locale.ROOT)),
                        String.format("All listed aspects should match the given filter.[Actual: %s] [Expected: %s]", i, filter)));
    }

    public void assertStatusColumnContainsMajorStates() {
        Assert.assertTrue(getFailedStateExistence(), "There should be failed states in every row!");
    }

    public void assertStatusColumnContainsMinorStates() {
        Assert.assertFalse(getFailedStateExistence(), "There should not be any failed states in a row!");
    }

    public ReportFailureAspectsPage search(String s) {
        testInputSearch.type(s);
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());
    }

    public ReportFailureAspectsPage clearSearch() {
        testInputSearch.clear();
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());

    }

    public void assertShowExpectedFailedButtonIsDisabled() {
        Assert.assertEquals(testShowExpectedFailsButton.getAttribute("aria-checked"), "true", "Button should be enabled!");
    }

    public ReportFailureAspectsPage clickShowExpectedFailedButton() {
        testShowExpectedFailsButton.click();
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());
    }

    public ReportTestsPage clickStateLink(String observedFailureAspect, Status expectedState) {
        for (int i = 0; i < getColumns(0).size(); i++) {
            if (getRows(i).get(1).getText().equals(observedFailureAspect)) {
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

    public GuiElement getTestTypeSelect() {
        return this.testTypeSelect;
    }

    public void assertFailureAspectTypeIsFilteredCorrectly(String failureAspectType) {
        if (failureAspectType.equals("Major")) {
            assertStatusColumnContainsMajorStates();
        } else if (failureAspectType.equals("Minor")) {
            assertStatusColumnContainsMinorStates();
        } else {
            Assert.fail("Invalid method call! " + failureAspectType + " is not a failure aspect type!");
        }
    }
}