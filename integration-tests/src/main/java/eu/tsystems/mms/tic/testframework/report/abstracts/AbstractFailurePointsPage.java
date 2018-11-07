package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
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

    private final String LOCATOR_FAILUREPOINT_DESCRIPTION_ALTERNATIVE = "//*div[contains(text(),'%s')]";
    private final String LOCATOR_FAILUREPOINT_HEADER_ALTERNATIVE = LOCATOR_FAILUREPOINT_DESCRIPTION_ALTERNATIVE + "/../../..//*[contains(text(),'%s')]";
    private final String LOCATOR_FAILUREPOINT_METHOD_ALTERNATIVE = LOCATOR_FAILUREPOINT_DESCRIPTION_ALTERNATIVE + "/../../..//*[contains(text(),'%s')]/following-sibling::*";
    private final String LOCATOR_FAILUREPOINT_READABLE_MESSAGE_ALTERNATIVE = LOCATOR_FAILUREPOINT_DESCRIPTION_ALTERNATIVE+ "/../../..//*[contains(text(),'%s')]";
    private final String LOCATOR_FAILUREPOINT_DETAILS_BUTTON_ALTERNATIVE = LOCATOR_FAILUREPOINT_DESCRIPTION_ALTERNATIVE + "/../../..//a[@title='Details']";

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
     * Getter for a GuiElement representing the header information for a single failure point entry
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

    public GuiElement getHeaderInformationElementForFailurePointAlternativeForExitpoints(AbstractResultTableFailureEntry entry) {
        GuiElement headerElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_HEADER_ALTERNATIVE,
                //DESCRIPTION
                entry.getDescription(),
                //HEADER
                failurePointType.getLabel()
        )), mainFrame);
        headerElement.setName("headerElement");
        return headerElement;
    }


    /**
     * Getter for a List containing all FailurePoint entry row GuiElements based on header xPath
     *
     * @return list of all failure point entries on FailurePointsPage
     */
    private List<GuiElement> getAllFailurePointEntryElements() {
        GuiElement failurePointEntries = new GuiElement(driver, By.xpath(TestResultHelper.TestResultFailurePointEntryType.ALL.getXPath()), mainFrame);
        failurePointEntries.setName("failurePointEntries");
        return failurePointEntries.getList();
    }

    /**
     * Getter for a List containing all FailurePoint entry GuiElements filtered by the given FailurePoint entry type
     *
     * @param testResult the testresult to filter
     * @return the filtered list of failure point entries
     */
    protected List<GuiElement> getFailurePointEntryElementsByTestResult(TestResultHelper.TestResultFailurePointEntryType testResult) {
        switch (testResult) {
            case FAILED:
            case FAILEDEXPECTED_INTOREPORT:
            case FAILEDEXPECTED_NOT_INTOREPORT:
                return filterFailurePointEntriesByTestResult(testResult);
            case ALL:
                return getAllFailurePointEntryElements();
            default:
                throw new FennecRuntimeException("TestResultFailurePoint [" + testResult + "] not implemented");
        }
    }

    /**
     * Filters the entries on a Failure Point Page by a given entry type
     *
     * @param testResultFailurePointType the entry type to filter
     * @return row elements with the given entry type only
     */
    private List<GuiElement> filterFailurePointEntriesByTestResult(TestResultHelper.TestResultFailurePointEntryType testResultFailurePointType) {
        GuiElement failurePointEntries = new GuiElement(driver, By.xpath(testResultFailurePointType.getXPath()), mainFrame);
        failurePointEntries.setName("failurePointEntries");
        return failurePointEntries.getList();
    }

    /**
     * Returns the GuiElement representing the Description for a given FailurePoint entry row
     *
     * @param entry the result table failure entry
     * @return a GuiElement representing the description element of a row entry
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

    public GuiElement getDescriptionElementByFailurePointAlternative(AbstractResultTableFailureEntry entry) {
        GuiElement descriptionElement = new GuiElement(driver, By.xpath(String.format(
                LOCATOR_FAILUREPOINT_DESCRIPTION_ALTERNATIVE,
                entry.getDescription()
        )), mainFrame);
        descriptionElement.setName("descriptionElement");
        return descriptionElement;
    }

    /**
     * Returns the GuiElement representing the total number of FailurePoints
     *
     * @param totalNumber the number the element contains
     * @return the GuiElement containing the given number
     */
    public GuiElement getTotalNumberOfFailurePointsElement(int totalNumber) {
        GuiElement totalNumberElement = new GuiElement(driver, By.xpath(String.format(
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

        GuiElement headerElement = getHeaderInformationElementForFailurePoint(entry);
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
    public List<GuiElement> getTestMethodsForSingleFailurePoint(AbstractResultTableFailureEntry entry) {
        List<GuiElement> allMethods = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            GuiElement testMethodElement = new GuiElement(driver, By.xpath(String.format(
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

    public List<GuiElement> getTestMethodsForSingleFailurePointAlternative(AbstractResultTableFailureEntry entry) {
        List<GuiElement> allMethods = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            GuiElement testMethodElement = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_METHOD_ALTERNATIVE,
                    // DESCRIPTION
                    entry.getDescription(),
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
    public List<GuiElement> getReadableMessageElementsForFailurePoint(AbstractResultTableFailureEntry entry) {
        List<GuiElement> allMessages = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            GuiElement testMethodElement = new GuiElement(driver, By.xpath(String.format(
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

    public List<GuiElement> getReadableMessageElementsForFailurePointAlternative(AbstractResultTableFailureEntry entry) {
        List<GuiElement> allMessages = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            GuiElement testMethodElement = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_READABLE_MESSAGE_ALTERNATIVE,
                    // DESCRIPTION
                    entry.getDescription(),
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
    public List<GuiElement> getDetailsLinksForFailurePoint(AbstractResultTableFailureEntry entry) {
        toggleElementsForFailurePoint(entry);
        List<GuiElement> allDetailLinks = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            GuiElement detailLink = new GuiElement(driver, By.xpath(String.format(
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

    public List<GuiElement> getDetailsLinksForFailurePointAlternative(AbstractResultTableFailureEntry entry) {
        toggleElementsForFailurePoint(entry);
        List<GuiElement> allDetailLinks = new ArrayList<>();
        for (int index = 0; index < entry.getNumberOfTests(); index++) {
            GuiElement detailLink = new GuiElement(driver, By.xpath(String.format(
                    LOCATOR_FAILUREPOINT_DETAILS_BUTTON_ALTERNATIVE,
                    entry.getDescription()
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
            GuiElement methodDetailElement = getTestMethodsForSingleFailurePoint(entry).get(index);
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
            GuiElement readableMessageElement = getReadableMessageElementsForFailurePoint(entry).get(index);
            readableMessageElement.asserts().assertIsDisplayed();
            String firstAssertionLine = readableMessageElement.getText();
            if (firstAssertionLine.contains("\n")) {
                firstAssertionLine = firstAssertionLine.substring(0, firstAssertionLine.indexOf("\n"));
            }
            Assert.assertTrue(possibleAssertions.contains(firstAssertionLine), "Element " + firstAssertionLine + " does NOT contain one of the following assertions: " + possibleAssertions);
            // Method Detail Link
            GuiElement detailLink = getDetailsLinksForFailurePoint(entry).get(index);
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
        GuiElement detailsLink = getDetailsLinksForFailurePoint(entryWithMethods).get(methodEntryPosition - 1);
        detailsLink.setName("detailLinkAtPosition" + methodEntryPosition);
        gotoDetails(detailsLink);
    }

    public void assertMethodClassPathAndDescription(AbstractResultTableFailureEntry entry) {
        if (entry.getMethodDetailPaths().isEmpty() || entry.getMethodDetailAssertions().isEmpty()) {
            Assert.fail("Failure Point " + entry.getTitle() + " must have methodInformation classPath and assertion");
        }
    }

    public MethodDetailsPage gotoDetails(GuiElement detailsLink) {
        detailsLink.asserts().assertIsDisplayed();
        detailsLink.click();
        return PageFactory.create(MethodDetailsPage.class, driver);
    }

    public abstract void assertExpectedFailsReportMark(AbstractResultTableFailureEntry failedEntry, boolean intoReport);

}