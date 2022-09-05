package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.utils.FailureAspectType;

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


    public enum FailureAspectsTableEntry {
        RANK(0), FAILURE_ASPECT(1), STATUS(2);

        private final int value;

        FailureAspectsTableEntry(final int value) {
            this.value = value;
        }

        public int index() {
            return this.value;
        }
    }

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportFailureAspectsPage(WebDriver driver) {
        super(driver);
    }

    public List<GuiElement> getColumns(final FailureAspectsTableEntry entry) {
        return getColumns(entry.index());
    }

    private List<GuiElement> getColumns(int column) {
        List<GuiElement> returnList = new ArrayList<>();
        for (GuiElement tableRows : failureAspectsTable.getSubElement(By.xpath("//tr")).getList()) {
            List<GuiElement> tableColumns = tableRows.getSubElement(By.xpath("//td")).getList();
            returnList.add(tableColumns.get(column));
        }
        return returnList;
    }

    /**
     * check existence of at least one failed state in each row, return as soon as the first row didn't have a failed state
     * @return boolean
     */
    public boolean getContainsFailedStateExistenceInEachRow() {
        final List<GuiElement> tableRows = failureAspectsTable.getSubElement(By.xpath("./tr")).getList();
        boolean statusContainsFailed = false;

        for (final GuiElement row : tableRows) {
            statusContainsFailed = false;

            final List<GuiElement> statusEntriesInTable = row.getSubElement(By.xpath(".//td[3]/div")).getList();
            for (final GuiElement statusColumn : statusEntriesInTable) {
                final String foundStatusInTable = statusColumn.getText();
                if (!foundStatusInTable.contains(Status.FAILED_EXPECTED.title) && foundStatusInTable.contains(Status.FAILED.title)) {
                    statusContainsFailed = true;
                }
            }
            // reached end of all found status in row: quit loop of row, if no FAILED was found
            if (!statusContainsFailed) break;
        }
        return statusContainsFailed;
    }

    public List<String> getOrderListOfTopFailureAspects() {
        return getColumns(FailureAspectsTableEntry.FAILURE_ASPECT)
                .stream()
                .map(GuiElement::getText)
                .collect(Collectors.toList());
    }

    public void assertRankColumnDescends() {
        final List<GuiElement> columns = getColumns(FailureAspectsTableEntry.RANK);
        int expectedRankNumber = 1;

        for (GuiElement column : columns) {
            Assert.assertEquals(Integer.parseInt(column.getText()), expectedRankNumber, "Rank column should descend and contain no duplicate!");
            expectedRankNumber++;
        }
    }

    public void assertFailureAspectsColumnContainsCorrectAspects(String filter) {
        getColumns(FailureAspectsTableEntry.FAILURE_ASPECT)
                .stream()
                .map(GuiElement::getText)
                .map(String::toUpperCase)
                .forEach(i -> Assert.assertTrue(i.contains(filter.toUpperCase(Locale.ROOT)),
                        String.format("All listed aspects should match the given filter.[Actual: %s] [Expected: %s]", i, filter)));
    }

    public void assertStatusColumnContainsMajorStates() {
        Assert.assertTrue(getContainsFailedStateExistenceInEachRow(), "There should be failed states in every row!");
    }

    public void assertStatusColumnContainsMinorStates() {
        Assert.assertFalse(getContainsFailedStateExistenceInEachRow(), "There should not be any failed states in a row!");
    }

    public ReportFailureAspectsPage search(String s) {
        testInputSearch.clear();
        testInputSearch.type(s);
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());
    }

    public ReportFailureAspectsPage clickShowExpectedFailedButton() {
        testShowExpectedFailsButton.click();
        return PageFactory.create(ReportFailureAspectsPage.class, getWebDriver());
    }

    public ReportTestsPage clickStateLink(String observedFailureAspect, Status expectedState) {
        // failureAspect can contain apostrophes
        final GuiElement failureAspectInRow = failureAspectsTable.getSubElement(
                By.xpath(".//tr[.//td[contains(., \"" + observedFailureAspect + "\")]]"));

        final int columnIndexStatus = FailureAspectsTableEntry.STATUS.value + 1;
        final GuiElement statusInRow = failureAspectInRow.getSubElement(By.xpath(".//td[" + columnIndexStatus + "]//a[contains(., '" + expectedState.title + "')]"));

        statusInRow.click();
        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }

    public ReportTestsPage clickFailureAspectLink(final String failureAspect) {
        // failureAspect can contain apostrophes
        final int columnIndexAspect = FailureAspectsTableEntry.FAILURE_ASPECT.value + 1;
        GuiElement subElement = failureAspectsTable.getSubElement(By.xpath(".//tr//td[" + columnIndexAspect + "]//a[contains(., \"" + failureAspect + "\")]"));
        subElement.click();

        return PageFactory.create(ReportTestsPage.class, getWebDriver());
    }

    public void assertFailureAspectTypeIsFilteredCorrectly(FailureAspectType failureAspectType) {
        switch (failureAspectType) {
            case MAJOR:
                assertStatusColumnContainsMajorStates();
                break;
            case MINOR:
                assertStatusColumnContainsMinorStates();
                break;
            default:
                Assert.fail("Invalid method call! " + failureAspectType + " is not a failure aspect type!");
                break;
        }
    }

    public ReportFailureAspectsPage selectFailureAspect(FailureAspectType failureAspectType) {
        return selectDropBoxElement(this.testTypeSelect, failureAspectType.value(), ReportFailureAspectsPage.class);
    }
}