/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */

package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
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
    private final UiElement testTypeSelect = pageContent.find(By.xpath("//mdc-select[@label='Type']"));

    @Check
    private final UiElement testInputSearch = pageContent.find(By.xpath("//label[@label='Search']//input"));

    @Check
    private final UiElement testShowExpectedFailsButton = pageContent.find(By.xpath("//button[@role='switch']"));

    @Check
    private final UiElement failureAspectsTable = pageContent.find(By.xpath("//tbody[@ref='content']"));


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

    public List<UiElement> getColumns(final FailureAspectsTableEntry entry) {
        return getColumns(entry.index());
    }

    private List<UiElement> getColumns(int column) {
        List<UiElement> returnList = new ArrayList<>();
        for (UiElement tableRows : failureAspectsTable.find(By.xpath("//tr")).list()) {
            List<UiElement> tableColumns = tableRows.find(By.xpath("//td")).list().stream().collect(Collectors.toList());
            returnList.add(tableColumns.get(column));
        }
        return returnList;
    }

    /**
     * check existence of at least one failed state in each row, return as soon as the first row didn't have a failed state
     *
     * @return boolean
     */
    public boolean getContainsFailedStateExistenceInEachRow() {
        final List<UiElement> tableRows = failureAspectsTable.find(By.xpath("./tr")).list().stream().collect(Collectors.toList());
        boolean statusContainsFailed = false;

        for (final UiElement row : tableRows) {
            statusContainsFailed = false;

            final List<UiElement> statusEntriesInTable = row.find(By.xpath(".//td[3]/div")).list().stream().collect(Collectors.toList());
            for (final UiElement statusColumn : statusEntriesInTable) {
                final String foundStatusInTable = statusColumn.expect().text().getActual();
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
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public void assertRankColumnDescends() {
        final List<UiElement> columns = getColumns(FailureAspectsTableEntry.RANK);
        int expectedRankNumber = 1;

        for (UiElement column : columns) {
            Assert.assertEquals(Integer.parseInt(column.expect().text().getActual()), expectedRankNumber, "Rank column should descend and contain no duplicate!");
            expectedRankNumber++;
        }
    }

    public void assertFailureAspectsColumnContainsCorrectAspects(String filter) {
        getColumns(FailureAspectsTableEntry.FAILURE_ASPECT)
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
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
        return createPage(ReportFailureAspectsPage.class);
    }

    public ReportFailureAspectsPage clickShowExpectedFailedButton() {
        testShowExpectedFailsButton.click();
        return createPage(ReportFailureAspectsPage.class);
    }

    public ReportTestsPage clickStateLink(String observedFailureAspect, Status expectedState) {
        // failureAspect can contain apostrophes
        final UiElement failureAspectInRow = failureAspectsTable.find(
                By.xpath(".//tr[.//td[contains(., \"" + observedFailureAspect + "\")]]"));

        final int columnIndexStatus = FailureAspectsTableEntry.STATUS.value + 1;
        final UiElement statusInRow = failureAspectInRow.find(By.xpath(".//td[" + columnIndexStatus + "]//a[contains(., '" + expectedState.title + "')]"));

        statusInRow.click();
        return createPage(ReportTestsPage.class);
    }

    public ReportTestsPage clickFailureAspectLink(final String failureAspect) {
        // failureAspect can contain apostrophes
        final int columnIndexAspect = FailureAspectsTableEntry.FAILURE_ASPECT.value + 1;
        UiElement subElement = failureAspectsTable.find(By.xpath(".//tr//td[" + columnIndexAspect + "]//a[contains(., \"" + failureAspect + "\")]"));
        subElement.click();

        return createPage(ReportTestsPage.class);
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