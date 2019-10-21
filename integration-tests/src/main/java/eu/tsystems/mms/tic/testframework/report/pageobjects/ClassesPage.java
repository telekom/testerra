package eu.tsystems.mms.tic.testframework.report.pageobjects;


import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractReportPage;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by jlma on 26.10.2016.
 */
public class ClassesPage extends AbstractReportPage {

    private static final String SYNC_FAILED_WARNING_INDICATOR_TITLE = "Sync failed, see console log for details";

    //legend iterator
    @Check
    private GuiElement testsPassedLegendIndicator = new GuiElement(this.driver, By.xpath("//span[@title='Passed']"), mainFrame);

    private GuiElement testsRetriedLegendIndicator = new GuiElement(this.driver, By.xpath("//span[@title='Passed after Retry']"), mainFrame);

    private GuiElement testsFailedLegendIndicator = new GuiElement(this.driver, By.xpath("//span[@title='Failed']"), mainFrame);

    private GuiElement configMethodsIndicator = new GuiElement(this.driver, By.xpath("//font[@class='configMethods']"), mainFrame);

    //additional functions on class page
    private GuiElement hidePassedTestsCheckbox = new GuiElement(this.driver, By.id("hidePassed"), mainFrame);

    private GuiElement buildUserString = new GuiElement(this.driver, By.xpath("//tbody[@id='tests-3']/tr[1]/td[2]"), mainFrame);

    private GuiElement buildVerionString = new GuiElement(this.driver, By.xpath("//tbody[@id='tests-3']/tr[2]/td[2]"), mainFrame);

    private GuiElement buildTimeStampString = new GuiElement(this.driver, By.xpath("//tbody[@id='tests-3']/tr[3]/td[2]"), mainFrame);

    public ClassesPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns all test classes table rows as web elements
     *
     * @return list of web elements representing the class table row (tr)
     */
    public List<GuiElement> getTestClasses() {
        return new GuiElement(driver, By.xpath("//*[@class='testclassrow hidden test' and @style='display: table-row;']"), mainFrame).getList();
    }

    /**
     * Returns the displayed numbers for the given class name for the given test result or all numbers if specified
     *
     * @param className  The simple displayed class name of testundertest class
     * @param testResult filter for test results. TestResult.ALL returns all possible numbers
     * @return map which contains the given test result and the corresponding number as single entry
     * or all possible test results and corresponding numbers
     */
    public Map<TestResultHelper.TestResultClassesColumn, String> getActualTestNumbers(String className, TestResultHelper.TestResultClassesColumn testResult) {


        Map<TestResultHelper.TestResultClassesColumn, String> actual = new HashMap<>();
        GuiElement classTableRow = getClassTableRowForClass(className);
        classTableRow.asserts().assertIsDisplayed();
        IGuiElement classTestNumber;
        if (testResult != TestResultHelper.TestResultClassesColumn.ALL) {
            classTestNumber = classTableRow.getSubElement(By.xpath(testResult.getNumberXPath()));
            classTestNumber.setName("classTestNumber");
            classTestNumber.asserts().assertIsDisplayed();
            String actualValue = classTestNumber.getText();
            actual.put(testResult, actualValue);
        } else {
            actual.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.PASSED));
            actual.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.PASSEDMINOR));
            actual.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.FAILED));
            actual.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.FAILEDMINOR));
            actual.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.FAILEDEXPECTED));
            actual.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.SKIPPED));
        }

        return actual;

    }

    /**
     * Returns the single table row element (tr) based on the given class name
     *
     * @param className
     * @return GuiElement classTableRow
     */
    private GuiElement getClassTableRowForClass(String className) {
        GuiElement classTableRow = new GuiElement(driver, By.xpath("//a[contains(text(),'" + className + "')]/../.."), mainFrame);
        classTableRow.setName("classTableRow");
        return classTableRow;
    }

    /**
     * Returns the single table row element (tr) based on the given row position
     *
     * @param position
     * @return GuiElement classTableRow
     */
    private GuiElement getClassTableRowForPosition(int position) {
        GuiElement classTableRow = new GuiElement(this.driver, By.xpath("//*[@class='columnHeadings']/following-sibling::tr[" + position + "]"), mainFrame);
        classTableRow.setName("classTableRow");
        return classTableRow;
    }

    /**
     * Method to hide passed test class by clicking the "Hide passed Tests" checkbox
     *
     * @return true if hiding is selected
     */
    public boolean hidePassedTests() {
        hidePassedTestsCheckbox.asserts().assertIsDisplayed();
        hidePassedTestsCheckbox.click();
        return hidePassedTestsCheckbox.isSelected();
    }

    /**
     * Asserts the green tick mark (V) as success indicator is displayed on the left of the testundertest of a given class name
     *
     * @param className
     */
    public void assertSuccessIndicatorIsDisplayedForClass(String className) {
        GuiElement classTableRow = getClassTableRowForClass(className);
        IGuiElement successIndicator = classTableRow.getSubElement(By.xpath("//*[@class='textleft']/span[@title='Passed']"));
        successIndicator.setName("successIndicator");
        successIndicator.asserts().assertIsDisplayed();
    }

    /**
     * Asserts the red mark (X) as failed indicator is displayed on the left of the testundertest of a given class name
     *
     * @param className
     */
    public void assertBrokenIndicatorIsShownForClass(String className) {
        GuiElement classTableRow = getClassTableRowForClass(className);
        IGuiElement brokenIndicator = classTableRow.getSubElement(By.xpath(".//*[@class='textleft']/span[@title='Failed']"));
        brokenIndicator.setName("brokenIndicator");
        brokenIndicator.asserts().assertIsDisplayed();
        IGuiElement successIndicator = classTableRow.getSubElement(By.xpath(".//*[@class='textleft']/span[@title='Passed']"));
        successIndicator.setName("successIndicator");
        successIndicator.asserts().assertIsNotDisplayed();
    }

    /**
     * Asserts only failed tests with the red mark (X) are displayed
     */
    public void assertClassesAreDisplayedForHidePassedTestFilter() {
        List<GuiElement> testClasses = getTestClasses();
        for (GuiElement currentTestClass : testClasses) {
            String className = currentTestClass.getSubElement(By.xpath(".//a")).getText();
            assertBrokenIndicatorIsShownForClass(className);
        }
    }

    /**
     * Asserts the exclamation mark (!) - indicating a sync failed warning - is displayed on the left of the testundertest of a given class name
     *
     * @param className
     */
    public void assertSyncFailedWarningIsDisplayedForTestclass(String className) {
        GuiElement classTableRow = getClassTableRowForClass(className);
        classTableRow.setName(className + "ClassTableRow");
        IGuiElement syncFailedWarningMethodIndicator = classTableRow.getSubElement(By.xpath(".//img[@title='" + SYNC_FAILED_WARNING_INDICATOR_TITLE + "']"));
        syncFailedWarningMethodIndicator.setName("syncFailedWarningMethodIndicator");
        syncFailedWarningMethodIndicator.asserts().assertIsDisplayed();
    }

    /**
     * Asserts the displayed numbers for the given class name for the given test result equals the expected numbers
     *
     * @param expectedNumbers
     * @param className
     */
    public void assertNumbersForTestResultsOfOneTestClass(Map<TestResultHelper.TestResultClassesColumn, String> expectedNumbers, String className) {
        Map<TestResultHelper.TestResultClassesColumn, String> actualClassesTableRowNumbers = new HashMap<>();
        actualClassesTableRowNumbers.putAll(getActualTestNumbers(className, TestResultHelper.TestResultClassesColumn.ALL));
        AssertCollector.assertEquals(actualClassesTableRowNumbers, expectedNumbers, "Numbers of test result are correct for Class: " + className);
    }

    /**
     * Asserts the values of the "XETA INFORMATION" section are correctly displayed
     */
    public void assertTesterraInformationIsDisplayed() throws ParseException {

        final DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);

        buildUserString.asserts().assertIsDisplayed();
        buildVerionString.asserts().assertIsDisplayed();
        buildTimeStampString.asserts().assertIsDisplayed();
        dateFormat.parse(buildTimeStampString.getText());
    }

    /**
     * Asserts the the green tick mark (V) as success indicator is displayed in footer legend
     */
    public void assertPassedLegendSymbolIsDisplayed() {
        testsPassedLegendIndicator.asserts().assertIsDisplayed();
    }

    /**
     * Asserts the the olive green tick mark (V) as success with retry indicator is displayed in footer legend
     */
    public void assertRetryPassedLegendSymbolIsDisplayed() {
        testsRetriedLegendIndicator.asserts().assertIsDisplayed();
    }

    /**
     * Asserts the the red mark (X) as failed indicator is displayed in footer legend
     */
    public void assertFailedLegendSymbolIsDisplayed() {
        testsFailedLegendIndicator.asserts().assertIsDisplayed();
    }

    /**
     * Asserts all the footer legend labels displayed in footer legend
     */
    public void assertAllLegendSymbolsAreDisplayed() {
        assertPassedLegendSymbolIsDisplayed();
        assertRetryPassedLegendSymbolIsDisplayed();
        assertFailedLegendSymbolIsDisplayed();
    }

    /**
     * Goes to Classes Detail Page by clicking on the given class name
     *
     * @param className
     * @return ClassesDetailsPage
     */
    public ClassesDetailsPage gotoClassesDetailsPageForClass(String className) {
        GuiElement classTableRowLink = new GuiElement(driver, By.partialLinkText(className), mainFrame);
        classTableRowLink.setName(className + "ClassTableRowLink");
        classTableRowLink.click();
        return PageFactory.create(ClassesDetailsPage.class, driver);
    }
}
