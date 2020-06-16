/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractFailurePointsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractResultTableFailureEntry;
import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoNumbers;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ExitPointsPage extends AbstractFailurePointsPage {

    private final String LOCATOR_EXIT_POINT_ENTRY_ID = "exit-%d"; // counting starts with '1'
    private final String INTO_REPORT_SELECTOR = ".method ";
    private final String NOT_INTO_REPORT_SELECTOR = ".method.expfailed";
    private final String EXPECTED_MARKUP_TEXT = "Failing of test expected";

    public ExitPointsPage(WebDriver driver) {
        super(driver);
        driver.switchTo().defaultContent();
        driver.switchTo().frame(0);
        this.failurePointType = ResultTableFailureType.EXIT_POINT;
        this.failurePointID = LOCATOR_EXIT_POINT_ENTRY_ID;
    }

    @Override
    public void assertFailurePointRanking(List<? extends AbstractResultTableFailureEntry> expectedEntries) {
        int numberOfTestsThreshold = Integer.MAX_VALUE;
        for (AbstractResultTableFailureEntry failureEntry : expectedEntries) {
            // check - page information fits to test object model
            assertHeaderInformation(failureEntry);
            // following lines not really necessary since it considers the model only
            int currentNumberOfTests = failureEntry.getNumberOfTests();
            // check - order is descending or last entry
            if (currentNumberOfTests <= numberOfTestsThreshold) {
                numberOfTestsThreshold = currentNumberOfTests;
            } else if (failureEntry.getEntryNumber() != expectedEntries.size()) {
                // Todo: else branch mot necessary, since Exit Points are separated in single ones
                Assert.fail("Failure Point ranking is NOT correct. " + currentNumberOfTests + " must be less than or equal to " + numberOfTestsThreshold);
            }
        }
    }

    /**
     * Asserts that the markup for expected failed test is correct
     * for states intoReport and notintoReport respectively
     *
     * @param failedEntry
     * @param intoReport
     */
    @Override
    public void assertExpectedFailsReportMark(AbstractResultTableFailureEntry failedEntry, boolean intoReport) {

        GuiElement exitPointEntry;
        GuiElement exitPointExtendButton;
        GuiElement exitPointMethod;
        GuiElement exitPointType = null;

        TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();

        boolean entryFound = false;
        int currentTestNumber = 1; // first exit point entry

        while (currentTestNumber <= testReportTwoNumbers.getExitPoints()) {

            exitPointEntry = getHeaderInformationElementAlternativeForExitpoints(failedEntry, currentTestNumber);
            exitPointEntry.asserts().assertIsDisplayed();

            exitPointExtendButton = getExtendButtonAlternativeForExitpoints(failedEntry, currentTestNumber);
            exitPointExtendButton.click();

            exitPointMethod = getMethodInformationAlternativeForExitpoints(failedEntry, currentTestNumber);

            if (exitPointMethod.isDisplayed()) {
                entryFound = true;
                break;
            }

            currentTestNumber++;
        }

        if (entryFound) {
            if (intoReport) {
                exitPointType = getIntoReportInformationAlternativeForExitpoints(failedEntry, currentTestNumber, intoReport);
            } else {
                exitPointType = getIntoReportInformationAlternativeForExitpoints(failedEntry, currentTestNumber, intoReport);
            }
        }

        exitPointType.asserts().assertIsDisplayed();
    }
}
