package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riwa on 11.04.2017.
 */
public abstract class AbstractFailurePointsPage extends AbstractReportPage {

    protected ResultTableFailureType failurePointType;
    protected String failurePointID;

    private final String LOCATOR_FAILUREPOINT_ROW = "./../..//*[@id='row-%02d']";
    private final String LOCATOR_FAILUREPOINT_TOTAL = "//*[contains(text(),'%ss: %s')]";
    private final String LOCATOR_FAILUREPOINT_HEADER = "//*[contains(text(),'%s #%d (%d Tests)')]";
    private final String LOCATOR_FAILUREPOINT_DESCRIPTION = LOCATOR_FAILUREPOINT_HEADER + "/../..//*[contains(text(),'%s')]";
    private final String LOCATOR_FAILUREPOINT_METHOD = LOCATOR_FAILUREPOINT_HEADER + "/../..//*[contains(text(),'%s')]/following-sibling::*";
    private final String LOCATOR_FAILUREPOINT_READABLE_MESSAGE = LOCATOR_FAILUREPOINT_HEADER + "/../..//*[contains(text(),'%s')]";
    private final String LOCATOR_FAILUREPOINT_DETAILS_BUTTON = LOCATOR_FAILUREPOINT_HEADER + "/../..//a[@title='Details']";
    private final String LOCATOR_FAILUREPOINT_EXTEND_BUTTON = LOCATOR_FAILUREPOINT_HEADER + "//i[@class='fa fa-caret-square-o-down']";
    private final String LOCATOR_FAILUREPOINT_INTO_REPORT_NO = LOCATOR_FAILUREPOINT_HEADER + "/../..//*[@class='method expfailed']";
    private final String LOCATOR_FAILUREPOINT_INTO_REPORT_YES = LOCATOR_FAILUREPOINT_HEADER + "/../..//*[@class='method ']";

    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public AbstractFailurePointsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Asserts that Failure Points have the correct ranking (Failure Point contain most Tests on Top)
     *
     * @param expectedEntries the failure point entries to check
     */
    public abstract void assertFailurePointRanking(List<? extends AbstractResultTableFailureEntry> expectedEntries);

    /**
     * Getter for a IGuiElement representing the header information for a single failure point entry
     *
     * @param entry the entry to get the header information from
     * @return a GuiElemet representing the header of the given entry
     */
    public GuiElement getHeaderInformationElementForFailurePoint(AbstractResultTableFailureEntry entry) {
        GuiElement headerElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_HEADER,
                failurePointType.getLabel(),
                entry.getEntryNumber(),
                entry.getNumberOfTests()
        )), mainFrame);
        headerElement.setName("headerElement");
        return headerElement;
    }

    /**
     * Alternate Methods used for the new exit points
     * @param entry
     * @param index
     * @return
     */
    public IGuiElement getHeaderInformationElementAlternativeForExitpoints(AbstractResultTableFailureEntry entry, int index) {
        IGuiElement headerElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_HEADER,
                failurePointType.getLabel(),
                index,
                entry.getNumberOfTests()
        )), mainFrame);
        headerElement.setName("headerElement");
        return headerElement;
    }

    public IGuiElement getExtendButtonAlternativeForExitpoints(AbstractResultTableFailureEntry entry, int index) {
        IGuiElement extendButton = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_EXTEND_BUTTON,
                failurePointType.getLabel(),
                index,
                entry.getNumberOfTests()
        )), mainFrame);
        extendButton.setName("headerElement");
        return extendButton;
    }

    public IGuiElement getMethodInformationAlternativeForExitpoints(AbstractResultTableFailureEntry entry, int index) {
        IGuiElement methodElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_READABLE_MESSAGE,
                failurePointType.getLabel(),
                index,
                entry.getNumberOfTests(),
                entry.getDescription()
        )), mainFrame);
        methodElement.setName("headerElement");
        return methodElement;
    }

    public IGuiElement getIntoReportInformationAlternativeForExitpoints(AbstractResultTableFailureEntry entry, int index, boolean intoReport) {

        if(intoReport) {
            IGuiElement extendButton = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_INTO_REPORT_YES,
                    failurePointType.getLabel(),
                    index,
                    entry.getNumberOfTests()
            )), mainFrame);
            extendButton.setName("headerElement");
            return extendButton;

        } else {
            IGuiElement extendButton = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_INTO_REPORT_NO,
                    failurePointType.getLabel(),
                    index,
                    entry.getNumberOfTests()
            )), mainFrame);
            extendButton.setName("headerElement");
            return extendButton;
        }

    }

    /**
     * Getter for a List containing all FailurePoint entry row GuiElements based on header xPath
     *
     * @return list of all failure point entries on FailurePointsPage
     */
    private List<IGuiElement> getAllFailurePointEntryElements() {
        IGuiElement failurePointEntries = new GuiElement(driver, By.xpath(TestResultHelper.TestResultFailurePointEntryType.ALL.getXPath()), mainFrame);
        failurePointEntries.setName("failurePointEntries");
        return failurePointEntries.getList();
    }

    /**
     * Getter for a List containing all FailurePoint entry GuiElements filtered by the given FailurePoint entry type
     *
     * @param testResult the testresult to filter
     * @return the filtered list of failure point entries
     */
    protected List<IGuiElement> getFailurePointEntryElementsByTestResult(TestResultHelper.TestResultFailurePointEntryType testResult) {
        switch (testResult) {
            case FAILED:
            case FAILEDEXPECTED_INTOREPORT:
            case FAILEDEXPECTED_NOT_INTOREPORT:
                return filterFailurePointEntriesByTestResult(testResult);
            case ALL:
                return getAllFailurePointEntryElements();
            default:
                throw new TesterraRuntimeException("TestResultFailurePoint [" + testResult + "] not implemented");
        }
    }

    /**
     * Filters the entries on a Failure Point Page by a given entry type
     *
     * @param testResultFailurePointType the entry type to filter
     * @return row elements with the given entry type only
     */
    private List<IGuiElement> filterFailurePointEntriesByTestResult(TestResultHelper.TestResultFailurePointEntryType testResultFailurePointType) {
        IGuiElement failurePointEntries = new GuiElement(driver, By.xpath(testResultFailurePointType.getXPath()), mainFrame);
        failurePointEntries.setName("failurePointEntries");
        return failurePointEntries.getList();
    }

    /**
     * Returns the IGuiElement representing the Description for a given FailurePoint entry row
     *
     * @param entry the result table failure entry
     * @return a IGuiElement representing the description element of a row entry
     */
    public GuiElement getDescriptionElementByFailurePoint(AbstractResultTableFailureEntry entry) {
        GuiElement descriptionElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_DESCRIPTION,
                // HEADER
                failurePointType.getLabel(),
                entry.getEntryNumber(),
                entry.getNumberOfTests(),
                // DESCRIPTION
                entry.getDescription()
        )), mainFrame);
        descriptionElement.setName("descriptionElement");
        return descriptionElement;
    }

    /**
     * Returns the IGuiElement representing the total number of FailurePoints
     *
     * @param totalNumber the number the element contains
     * @return the IGuiElement containing the given number
     */
    public IGuiElement getTotalNumberOfFailurePointsElement(int totalNumber) {
        IGuiElement totalNumberElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_TOTAL,
                failurePointType.getLabel(),
                totalNumber
        )), mainFrame);
        totalNumberElement.setName("totalNumberElement");
        return totalNumberElement;
    }

    /**
     * Toggles the Failure Point Entry Row to display the row information
     *
     * @param entry the result table failure entry
     * @return
     */
    public AbstractFailurePointsPage toggleElementsForFailurePoint(AbstractResultTableFailureEntry entry) {
        return toggleElementsForFailurePoint(entry, false);
    }

    /**
     * Toggles the Failure Point Entry Row to display the row information with forcing flag
     *
     * @param entry
     * @param forceClick if flag is set, there will be a click on the header, no matter whether the row information is already open
     * @return
     */
    public AbstractFailurePointsPage toggleElementsForFailurePoint(AbstractResultTableFailureEntry entry, boolean forceClick) {

        IGuiElement headerElement = getHeaderInformationElementForFailurePoint(entry);
        if (!forceClick && headerElement.getSubElement(By.xpath(String.format(LOCATOR_FAILUREPOINT_ROW, 0))).isDisplayed()) {
            return this;
        }
        headerElement.click();
        return this;
    }

    /**
     * Returns FailurePoint row information concerning the test method section
     *
     * @param entry
     * @return
     */
    public List<IGuiElement> getTestMethodsForSingleFailurePoint(AbstractResultTableFailureEntry entry) {
        List<IGuiElement> allMethods = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            IGuiElement testMethodElement = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_METHOD,
                    // HEADER
                    failurePointType.getLabel(),
                    entry.getEntryNumber(),
                    entry.getNumberOfTests(),
                    // METHOD
                    entry.getMethodDetailPaths().get(index).substring(0, entry.getMethodDetailPaths().get(index).lastIndexOf(" - t"))
            )), mainFrame);
            testMethodElement.setName("testMethodElementClassPath_" + index);
            allMethods.add(testMethodElement);
        }
        return allMethods;
    }

    /**
     * Returns FailurePoint row information concerning the description
     *
     * @param entry
     * @return
     */
    public List<IGuiElement> getReadableMessageElementsForFailurePoint(AbstractResultTableFailureEntry entry) {
        List<IGuiElement> allMessages = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            IGuiElement testMethodElement = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_READABLE_MESSAGE,
                    // HEADER
                    failurePointType.getLabel(),
                    entry.getEntryNumber(),
                    entry.getNumberOfTests(),
                    // ASSERTION
                    entry.getMethodDetailAssertions().get(index)
            )), mainFrame);
            testMethodElement.setName("testMethodElementMessage_" + index);
            allMessages.add(testMethodElement);
        }
        return allMessages;
    }

    /**
     * Returns all Detail-Links for a given row entry
     *
     * @param entry
     * @return
     */
    public List<IGuiElement> getDetailsLinksForFailurePoint(AbstractResultTableFailureEntry entry) {
        toggleElementsForFailurePoint(entry);
        List<IGuiElement> allDetailLinks = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            IGuiElement detailLink = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_DETAILS_BUTTON,
                    failurePointType.getLabel(),
                    entry.getEntryNumber(),
                    entry.getNumberOfTests()
            )), mainFrame);
            detailLink.setName("detailLink_" + index);
            allDetailLinks.add(detailLink);
        }
        return allDetailLinks;
    }

    public void assertTotalNumberOfFailurePoints(int expectedNumberOfFailurePoints) {
        getTotalNumberOfFailurePointsElement(expectedNumberOfFailurePoints).asserts("The number of Failure Points is NOT correct.").assertIsDisplayed();
    }

    public void assertNumberOfTestsForAllFailurePoints(int expectedNumberOfTests, List<? extends AbstractResultTableFailureEntry> expectedEntries) {
        int sumOfTests = 0;
        for (AbstractResultTableFailureEntry actualEntry : expectedEntries) {
            assertHeaderInformation(actualEntry);
            sumOfTests += actualEntry.getNumberOfTests();
        }
        Assert.assertEquals(sumOfTests, expectedNumberOfTests, "Sum of tests is NOT correct");
    }

    public void assertHeaderInformation(List<? extends AbstractResultTableFailureEntry> expectedEntries) {
        for (AbstractResultTableFailureEntry entry : expectedEntries) {
            assertHeaderInformation(entry);
        }
    }

    public void assertHeaderInformation(AbstractResultTableFailureEntry entry) {
        getHeaderInformationElementForFailurePoint(entry).assertCollector().assertIsDisplayed();
    }

    public void assertTestMethodInformation(AbstractResultTableFailureEntry entry) {
        assertMethodClassPathAndDescription(entry);
        toggleElementsForFailurePoint(entry);

        List<String> possibleAssertions = entry.getMethodDetailAssertions();
        for (int index = 0; index < entry.getMethodDetailPaths().size(); index++) {
            // Method Name Path
            IGuiElement methodDetailElement = getTestMethodsForSingleFailurePoint(entry).get(index);
            methodDetailElement.asserts().assertIsDisplayed();
            boolean isFound;
            int i = 0;
            do{
                String expectedTestMethod = entry.getMethodDetailPathSimpleMethodNames().get(i);
                isFound = methodDetailElement.getText().contains(expectedTestMethod);
                i++;
            }while(!isFound && i < entry.getMethodDetailPathSimpleMethodNames().size());

            Assert.assertTrue(isFound, "Element " + methodDetailElement.getText() + " does NOT contain one of the following test methods: " + entry.getMethodDetailPathSimpleMethodNames());

            // Method Assertion Message
            IGuiElement readableMessageElement = getReadableMessageElementsForFailurePoint(entry).get(index);
            readableMessageElement.asserts().assertIsDisplayed();
            String firstAssertionLine = readableMessageElement.getText();
            if (firstAssertionLine.contains("\n")) {
                firstAssertionLine = firstAssertionLine.substring(0, firstAssertionLine.indexOf("\n"));
            }
            Assert.assertTrue(possibleAssertions.contains(firstAssertionLine), "Element " + firstAssertionLine + " does NOT contain one of the following assertions: " + possibleAssertions);
            // Method Detail Link
            IGuiElement detailLink = getDetailsLinksForFailurePoint(entry).get(index);
            detailLink.asserts().assertIsDisplayed();
        }


    }

    public void assertDescriptionsForFailurePointsIsCorrect(List<? extends AbstractResultTableFailureEntry> expectedEntries) {
        for (AbstractResultTableFailureEntry entry : expectedEntries) {
            assertDescriptionsForFailurePointIsCorrect(entry);
        }

    }

    public void assertDescriptionsForFailurePointIsCorrect(AbstractResultTableFailureEntry expectedEntry) {
        getDescriptionElementByFailurePoint(expectedEntry).assertCollector("The DESCRIPTION for Failure Point " + expectedEntry.getTitle() + "is NOT correct").assertIsDisplayed();
    }

    public void assertDetailsLinkNavigation(AbstractResultTableFailureEntry entryWithMethods, int methodEntryPosition) {
        assertMethodClassPathAndDescription(entryWithMethods);
        IGuiElement detailsLink = getDetailsLinksForFailurePoint(entryWithMethods).get(methodEntryPosition - 1);
        detailsLink.setName("detailLinkAtPosition" + methodEntryPosition);
        gotoDetails(detailsLink);
    }

    public void assertMethodClassPathAndDescription(AbstractResultTableFailureEntry entry) {
        if (entry.getMethodDetailPaths().isEmpty() || entry.getMethodDetailAssertions().isEmpty()) {
            Assert.fail("Failure Point " + entry.getTitle() + " must have methodInformation classPath and assertion");
        }
    }

    public MethodDetailsPage gotoDetails(IGuiElement detailsLink) {
        detailsLink.asserts().assertIsDisplayed();
        detailsLink.click();
        return PageFactory.create(MethodDetailsPage.class, driver);
    }

    public abstract void assertExpectedFailsReportMark(AbstractResultTableFailureEntry failedEntry, boolean intoReport);

}
