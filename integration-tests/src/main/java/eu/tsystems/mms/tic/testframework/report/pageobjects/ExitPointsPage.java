package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFailurePointsPage;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractResultTableFailureEntry;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;

/**
 * Created by riwa on 03.04.2017.
 */
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
        GuiElement tableRow;
        toggleElementsForFailurePoint(failedEntry);
        // Check - class is correct
        if (intoReport) {
            tableRow = getHeaderInformationElementForFailurePoint(failedEntry).getSubElement(By.xpath("./../.."));
            tableRow = tableRow.getSubElement(By.cssSelector(".method"));
            tableRow.asserts().assertIsDisplayed();
        } else {
            tableRow = getHeaderInformationElementForFailurePoint(failedEntry).getSubElement(By.xpath("./../.."));;
            tableRow = tableRow.getSubElement(By.cssSelector(NOT_INTO_REPORT_SELECTOR));
            tableRow.asserts().assertIsDisplayed();
        }
        // Check - text is correct
        final String expectedMarkup = EXPECTED_MARKUP_TEXT;
        final String actualMessage = getReadableMessageElementsForFailurePoint(failedEntry).get(0).getText();
        Assert.assertTrue(actualMessage.contains(expectedMarkup), "The message " + actualMessage + " does NOT contain " + expectedMarkup);
    }

}
