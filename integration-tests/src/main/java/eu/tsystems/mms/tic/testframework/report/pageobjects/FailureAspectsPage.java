package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFailurePointsPage;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractResultTableFailureEntry;
import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by riwa on 11.04.2017.
 */
public class FailureAspectsPage extends AbstractFailurePointsPage {

    private final String LOCATOR_FAILURE_ASPECT_ENTRY_ID = "aspect-%d"; // counting starts with '1'

    public FailureAspectsPage(WebDriver driver) {
        super(driver);
        this.failurePointType = ResultTableFailureType.FAILURE_ASPECT;
        this.failurePointID = LOCATOR_FAILURE_ASPECT_ENTRY_ID;
    }

    @Override
    public void assertFailurePointRanking(List<? extends AbstractResultTableFailureEntry> expectedEntries) {
        int numberOfTestsThreshold = Integer.MAX_VALUE;
        for (AbstractResultTableFailureEntry failureEntry : expectedEntries) {
            assertHeaderInformation(failureEntry);
            int currentNumberOfTests = failureEntry.getNumberOfTests();
            if (currentNumberOfTests <= numberOfTestsThreshold) {
                numberOfTestsThreshold = currentNumberOfTests;
            } else {
                Assert.fail("Failure Point ranking is NOT correct. " + currentNumberOfTests + " must be less than or equal to " + numberOfTestsThreshold);
            }
        }
    }

    @Override
    public void assertExpectedFailsReportMark(AbstractResultTableFailureEntry failedEntry, boolean intoReport) {
        GuiElement headerTableRow = getHeaderInformationElementForFailurePoint(failedEntry).getSubElement(By.xpath("./../../.."));
        headerTableRow.setName("headerTableRow");
        Assert.assertEquals(headerTableRow.getAttribute("class"), failedEntry.getFailurePointEntryType().getClassAttribute(), "The Expected Failed points are NOT correct marked");
    }

}
