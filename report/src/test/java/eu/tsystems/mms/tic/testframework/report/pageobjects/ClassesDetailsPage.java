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

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.ReportConfigMethodStateHelper;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractReportPage;
import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ClassesDetailsPage extends AbstractReportPage implements IReportAnnotationVerifier, ReportConfigMethodStateHelper {

    private String testResultLocatorPattern = "//th[text()='%s']";

    @Check
    private GuiElement headLine = mainFrame.getSubElement(By.xpath("//div[@class='dashboardTextBig']"));

    private GuiElement configMethodsButton = mainFrame.getSubElement(By.id("toggleSuccessfulConfigMethodsView"));

    private GuiElement successfulConfigMethodsHeader = mainFrame.getSubElement(By.id("successfulConfigMethods"));

    public ClassesDetailsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Page flow method to change the class for the classes details
     *
     * @param testUnderTestClassName
     * @return
     */
    public ClassesDetailsPage changeTestUnderTestClass(String testUnderTestClassName) {
        ClassesPage classesPage = this.goToClasses();
        return classesPage.gotoClassesDetailsPageForClass(testUnderTestClassName);
    }

    private GuiElement getTestResultTableHeaderForTestResult(TestResultHelper.TestResult testResult) {
        GuiElement testResultTableHeader = mainFrame.getSubElement(By.xpath(String.format(testResultLocatorPattern, testResult.getXpathClassesDetailsHeader())));
        testResultTableHeader.setName("testResultTableHeader");
        return testResultTableHeader;
    }

    public void assertColorIsDisplayedForTestResult(TestResultHelper.TestResult testResult) {
        GuiElement testResultTableHeader = getTestResultTableHeaderForTestResult(testResult);
        testResultTableHeader.asserts().assertIsDisplayed();
        String colorString = testResultTableHeader.getCssValue("background-color");
        Color actualColor = toColorFromCSSColorString(colorString);
        Color expectedColor = TestResultHelper.convertColorForTestResult(testResult);
        Assert.assertEquals(actualColor, expectedColor, "The actual color for TestResult " + testResult.toString() + " is correct");
    }

    public void assertMethodNameIsDisplayedForTestMethod(String expectedMethodName, String... expectedSuiteNames) {
        GuiElement actualMethodNameElement = getInformationMethodBodyForTestMethodName(expectedMethodName);
        actualMethodNameElement.asserts().assertIsDisplayed();
        String actualMethodName = actualMethodNameElement.getText();
        AssertCollector.assertTrue(actualMethodName.contains(expectedMethodName), "The displayed method name is equal to the expected method name");
        if (expectedSuiteNames != null) {
            for (String expectedSuiteName : expectedSuiteNames)
                AssertCollector.assertTrue(actualMethodName.contains(expectedSuiteName), "The displayed suite name is equal to the expected one.");
        }
    }

    public void assertMethodIsDisplayedInTheCorrectTestResultCategory(String testundertestMethodName, TestResultHelper.TestResult expectedTestResultCategory) {
        assertMethodIsDisplayedInTheCorrectTestResultCategory(testundertestMethodName, null, expectedTestResultCategory);
    }

    public void assertMethodIsDisplayedInTheCorrectTestResultCategory(String testundertestMethodName, String controlMethodName, TestResultHelper.TestResult expectedTestResultCategory) {

        final String expectedHeaderClassAttribute = expectedTestResultCategory.getXpathClassesDetailsHeader();

        GuiElement testResultTableHeader = getTestResultTableHeaderForTestResult(expectedTestResultCategory);
        testResultTableHeader.asserts("The table header of the demanded test result should be displayed.").assertIsDisplayed();
        GuiElement methodNameElement = getInformationMethodBodyForTestMethodName(testundertestMethodName, controlMethodName);
        methodNameElement.asserts("The requested method name of the element should be displayed").assertIsDisplayed();
        GuiElement actualHeader = methodNameElement.getSubElement(By.xpath("./../tr/th"));
        String actualHeaderClassAttribute = actualHeader.getText();
        AssertCollector.assertEquals(actualHeaderClassAttribute, expectedHeaderClassAttribute,
                "The Test method should be in the correct test result category for test result + " + expectedTestResultCategory);
    }

    public void assertMethodIsDisplayedInTheCorrectTestResultCategoryWithTag(String testundertestMethodName, TestResultHelper.TestResult expectedTestResultCategory, String tagName) {

        final String expectedHeaderClassAttribute = expectedTestResultCategory.getXpathClassesDetailsHeader();

        GuiElement testResultTableHeader = getTestResultTableHeaderForTestResult(expectedTestResultCategory);
        testResultTableHeader.asserts("The table header of the demanded test result should be displayed.").assertIsDisplayed();

        GuiElement methodTagNameElement = getInformationMethodBodyForTestMethodNameWithTag(tagName);
        methodTagNameElement.asserts("The requested method tag name should be displayed").assertIsDisplayed();

        GuiElement actualMethodName = methodTagNameElement.getSubElement(By.xpath("./../../td[contains(text(),'" + testundertestMethodName + "')]"));
        actualMethodName.asserts("The requested method name of the element should be displayed").assertIsDisplayed();

        GuiElement actualHeader = methodTagNameElement.getSubElement(By.xpath("./../../..//th"));
        String actualHeaderClassAttribute = actualHeader.getText();
        AssertCollector.assertEquals(actualHeaderClassAttribute, expectedHeaderClassAttribute,
                "The Test method should be in the correct test result category for test result + " + expectedTestResultCategory);
    }

    /**
     * Method to get the left info column for a given test method name
     *
     * @param testMethodName
     * @return the left test method info column as GuiElement
     */
    private GuiElement getInformationMethodBodyForTestMethodName(String testMethodName, String configMethodName) {
//        GuiElement informationMethodBody = new GuiElement(driver, By.xpath(String.format("//*[@id='%s']", testMethodName)));
        GuiElement informationMethodBody = mainFrame.getSubElement(By.xpath(String.format("//*[contains(@id,'%s-')]", testMethodName)));
        if (null != configMethodName) {
            informationMethodBody = mainFrame.getSubElement(By.xpath(String.format("//*[contains(@id,'%s-') and .//div[contains(text(), '%s')]]", testMethodName, configMethodName)));
        }
        informationMethodBody.setName("informationMethodBody");
        return informationMethodBody;
    }

    /**
     * Method to get the left info column for a given test method name
     *
     * @param testMethodName
     * @return the left test method info column as GuiElement
     */
    private GuiElement getInformationMethodBodyForTestMethodName(String testMethodName) {
        GuiElement informationMethodBody = mainFrame.getSubElement(By.xpath(String.format("//*[contains(@id,'%s-')]", testMethodName)));
        informationMethodBody.setName("informationMethodBody");
        return informationMethodBody;
    }

    /**
     * Method to get the left info column for a given test method tag name
     *
     * @param testMethodTagName
     * @return the left test method info column as GuiElement
     */
    private GuiElement getInformationMethodBodyForTestMethodNameWithTag(String testMethodTagName) {

        GuiElement informationMethodBody = mainFrame.getSubElement(By.xpath("//font[contains(text(),'" + testMethodTagName + "')]"));
        informationMethodBody.setName("informationMethodBody");
        return informationMethodBody;
    }

    public void assertRetrySymbolIsDisplayedForMethod(String testundertestMethodName) {
        GuiElement methodInfoBody = getInformationMethodBodyForTestMethodName(testundertestMethodName);
        GuiElement retrySymbol = methodInfoBody.getSubElement(By.xpath("./..//img[@title='Retry']"));
        retrySymbol.setName("retrySymbol");
        retrySymbol.asserts().assertIsDisplayed();
    }

    public void assertErrorMessageIsDisplayedForTestMethod(String testMethodName) {
        GuiElement resultInfoBody = getResultInfoBody(testMethodName);
        GuiElement exceptionMessageElement = resultInfoBody.getSubElement(By.className("message"));
        exceptionMessageElement.setName("exceptionMessageElement");
        exceptionMessageElement.asserts().assertIsDisplayed();
        AssertCollector.assertFalse(exceptionMessageElement.getText().isEmpty(), "The exception message is empty");

    }

    public void assertStackTraceLinkIsDisplayedForTestMethod(String testMethodName) {
        GuiElement resultInfoBody = getResultInfoBody(testMethodName);
        GuiElement stackTraceLink = resultInfoBody.getSubElement(By.xpath(".//a[@title='Stacktrace']"));
        stackTraceLink.setName("stackTraceLink");
        stackTraceLink.asserts().assertIsDisplayed();
        stackTraceLink.click();
        GuiElement stackTrace = stackTraceLink.getSubElement(By.xpath("./../..//div[@class='stackTrace']"));
        stackTrace.setName("stackTrace");
        stackTrace.asserts().assertIsDisplayed();
        Assert.assertTrue(stackTrace.getText().contains(testMethodName), "The StackTrace contains the testmethod name: " + testMethodName);
    }

    public void assertDetailsLinkIsDisplayedAndWorks(String testMethodName) {
        GuiElement detailsLink = getDetailsLinkByMethodName(testMethodName);
        detailsLink.asserts().assertIsDisplayed();
        gotoMethodDetailsPage(detailsLink);
    }

    public void assertScreenshotIsNotDisplayedForMethod(String testundertestMethod) {
        GuiElement screenshotElement = getScreenShotOfMethod(testundertestMethod, 1);
        screenshotElement.asserts().assertIsNotDisplayed();
    }

    @Override
    public void assertHidingAndShowingOfConfigMethodSection() {
        final String configMethodsButtonLabelShow = ConfigMethodState.SHOWING.getLabel().toUpperCase();
        final String configMethodsButtonLabelHide = ConfigMethodState.HIDING.getLabel().toUpperCase();
        AssertCollector.assertEquals(configMethodsButton.getText(), configMethodsButtonLabelHide, "The showConfigMethod Button has the correct state");
        switchConfigMethodDisplayState();
        AssertCollector.assertEquals(configMethodsButton.getText(), configMethodsButtonLabelShow, "The showConfigMethod Button has the correct state");
        AssertCollector.assertTrue(isSuccessfulConfigurationMethodsHeaderDisplayed(), "The Header for successful Configuration Methods is displayed");
    }

    @Override
    public boolean changeConfigMethodDisplayStateTo(ConfigMethodState stateToChangeTo) {
        if (hasExpectedState(stateToChangeTo)) {
            return true;
        }
        configMethodsButton.click();
        return hasExpectedState(stateToChangeTo);
    }

    @Override
    public void switchConfigMethodDisplayState() {
        configMethodsButton.click();
    }

    @Override
    public boolean hasExpectedState(ConfigMethodState expectedState) {
        return configMethodsButton.getText().contains(expectedState.getStateIndicator());
    }

    public GuiElement getDetailsLinkByMethodName(String methodName) {
        GuiElement informationMethodBody = getInformationMethodBodyForTestMethodName(methodName);
        GuiElement detailsLink = informationMethodBody.getSubElement(By.xpath(".//a[@title='Details']"));
        detailsLink.setName("detailsLink");
        return detailsLink;
    }

    public GuiElement getDetailsLinkByMethodNameWithTag(String methodTagName) {
        GuiElement resultInfoBody = getResultInfoBodyWithTag(methodTagName);
        GuiElement detailsLink = resultInfoBody.getSubElement(By.xpath(".//a[@title='Details']"));
        detailsLink.setName("detailsLink");
        return detailsLink;
    }

    public boolean isSuccessfulConfigurationMethodsHeaderDisplayed() {
        return successfulConfigMethodsHeader.isDisplayed();
    }

    private boolean isElementTextDisplayedAndParsableToDate(GuiElement element) {

        final String durationIndicator = "duration";
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
        final DateFormat durationFormat = new SimpleDateFormat("HH'h' mm'm' ss's'", Locale.GERMAN);

        element.asserts().assertIsDisplayed();
        String elementText = element.getText();
        String datePart = elementText.substring(elementText.indexOf(": ") + 1);
        try {
            if (elementText.contains(durationIndicator)) {
                durationFormat.parse(datePart);
                return true;
            }
            dateFormat.parse(datePart);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Method to get the right result column for a given test method name
     *
     * @param testMethodName
     * @return the right test method result column as GuiElement
     */
    private GuiElement getResultInfoBody(String testMethodName) {
        GuiElement methodInfoBody = getInformationMethodBodyForTestMethodName(testMethodName);
        GuiElement resultInfoBody = methodInfoBody.getSubElement(By.xpath("./..//td[@class='result']"));
        resultInfoBody.setName("resultInfoBody");
        return resultInfoBody;
    }

    /**
     * Method to get the right result column for a given test method tag name
     *
     * @param testMethodTagName
     * @return the right test method result column as GuiElement
     */
    private GuiElement getResultInfoBodyWithTag(String testMethodTagName) {
        GuiElement methodInfoBody = getInformationMethodBodyForTestMethodNameWithTag(testMethodTagName);
        GuiElement resultInfoBody = methodInfoBody.getSubElement(By.xpath("./..//*[@class='result']"));
        resultInfoBody.setName("resultInfoBody");
        return resultInfoBody;
    }

    /**
     * Returns the Screenshot GuiElement for a given method in report. The index specify the screenshot if the method has more then one
     *
     * @param testundertestMethodName the simple method name of the testundertest method in report
     * @param screenShotIndex specify the screenShot since there could be more then one. 1 == First, 2 == Second, ...
     * @return
     */
    private GuiElement getScreenShotOfMethod(String testundertestMethodName, int screenShotIndex) {
        GuiElement resultInfoBody = getResultInfoBody(testundertestMethodName);
        resultInfoBody.asserts().assertIsDisplayed();
        GuiElement screenShotElement = resultInfoBody.getSubElement(By.xpath(".//div[@class='spacy shadow'][" + screenShotIndex + "]"));
        screenShotElement.setName("screenShotElement#" + screenShotIndex);
        return screenShotElement;
    }

    public MethodDetailsPage gotoMethodDetailsPage(GuiElement detailsLink) {
        detailsLink.click();
        return PageFactory.create(MethodDetailsPage.class, this.getWebDriver());
    }

    /**
     * Utility method to convert the CSS report representation of a color to java.awt Color Object.
     *
     * @param cssColorString the css value of 'background-color' in report CSS. Example cssColorString: "rgb(255, 136, 136)"
     * @return java.awt.Color with four channels
     */
    private static Color toColorFromCSSColorString(String cssColorString) {
        // Example cssColorString: "rgb(255, 136, 136)"
        String[] colorValues = cssColorString.split(",");
        int redChannel = Integer.parseInt(colorValues[0].substring(colorValues[0].indexOf("(") + 1).trim());
        int greenChannel = Integer.parseInt(colorValues[1].trim());
        int blueChannel = Integer.parseInt(colorValues[2].replace(")", "").trim());
        return new Color(redChannel, greenChannel, blueChannel);
    }

    @Override
    public void assertAnnotationMarkIsDisplayed(ReportAnnotationType annotationType, String methodName) {
        GuiElement methodBody = getInformationMethodBodyForTestMethodName(methodName);
        GuiElement annotationElement = methodBody.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, annotationType.getAnnotationDisplayedName())));
        annotationElement.setName("annotationElementFor_" + annotationType.getAnnotationDisplayedName());
        annotationElement.asserts("The demanded annotation mark for the element should be displayed.").assertIsDisplayed();
    }

    @Override
    public void assertAllAnnotationMarksAreDisplayed(String methodName) {
        for (ReportAnnotationType annotationType : ReportAnnotationType.values()) {
            assertAnnotationMarkIsDisplayed(annotationType, methodName);
        }
    }

    public void assertMethodExecutionOrder() {
        Map<Integer, Date> allExecutionNumbers = getAllExecutionEntries();
        int uppermostExecutionNumber = Collections.max(allExecutionNumbers.keySet());
        int lowestExecutionNumber = Collections.min(allExecutionNumbers.keySet());
        Date lastDate = Date.from(Instant.EPOCH);
        for (int i = lowestExecutionNumber; i < uppermostExecutionNumber; i++) {
            Date entryDate = allExecutionNumbers.get(i);
            if (entryDate == null || entryDate.getTime() <= 0) {
                continue;
            }
            Assert.assertTrue(entryDate.after(lastDate) || entryDate.equals(lastDate), "Date " + entryDate + " is NOT equal to or after date" + lastDate);
            lastDate = entryDate;
        }


    }

    public Map<Integer, Date> getAllExecutionEntries() {
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
        Map<Integer, Date> executionEntries = new HashMap<>();
        List<GuiElement> testMethodInfos = mainFrame.getSubElement(By.xpath(".//table[contains(@class,'resultsTable')]//div[5]")).getList();
        for (GuiElement testMethodInfo : testMethodInfos) {
            if (!testMethodInfo.isDisplayed()) {
                continue;
            }
            String executionNumberString = testMethodInfo.getText().replace("(", "").replace(")", "");
            String startDateString = testMethodInfo.getSubElement(By.xpath("./../div[1]")).getText().replace("Start time: ", "");
            if (startDateString.contains("-")) {
                startDateString = "01.01.1970 00:00:00";
            }
            Date startDate = null;
            try {
                startDate = dateFormat.parse(startDateString);
            } catch (ParseException e) {
                throw new RuntimeException("Could not parse start date " + startDateString + " of method with execution number " + executionNumberString);
            }
            int executionNumber = Integer.parseInt(executionNumberString);
            executionEntries.put(executionNumber, startDate);
        }
        return executionEntries;
    }

}
