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
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractReportPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleClassBarChart;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleFailureCorridor;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleHistoryAreaChart;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleInformationCorridor;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleMethodChart;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleTestResultNumberBreakdown;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleTestResultPieChart;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class DashboardPage extends AbstractReportPage implements IReportAnnotationVerifier {

//    @Check
    public final GuiElement testerraLogo = new GuiElement(this.getWebDriver(), By.cssSelector("img[alt='testerra']"), mainFrame);
    public final DashboardModuleTestResultPieChart dashboardModuleTestResultPieChart = PageFactory.create(DashboardModuleTestResultPieChart.class, this.getWebDriver());
    public final DashboardModuleTestResultNumberBreakdown dashboardModuleTestResultNumberBreakdown = PageFactory.create(DashboardModuleTestResultNumberBreakdown.class, this.getWebDriver());
    public final DashboardModuleFailureCorridor dashboardModuleFailureCorridor = PageFactory.create(DashboardModuleFailureCorridor.class, this.getWebDriver());
    public final DashboardModuleInformationCorridor dashboardModuleInformationCorridor = PageFactory.create(DashboardModuleInformationCorridor.class, this.getWebDriver());
    public final DashboardModuleClassBarChart dashboardModuleClassBarChart = PageFactory.create(DashboardModuleClassBarChart.class, this.getWebDriver());
    public final GuiElement screenShotInfoButton = new GuiElement(this.getWebDriver(), By.xpath("//*[contains(text(),'test_testMidCorridorFailed1')]"), mainFrame);

    public DashboardPage(WebDriver driver) {
        super(driver);
        dashboardModuleClassBarChart.barChartBars.waits().waitForIsDisplayed();
    }

    /**
     * Returns the history chart module, assuming it is displayed.
     *
     * @return the module to be worked with
     */
    public DashboardModuleHistoryAreaChart getHistoryAreaChartModule() {
        return PageFactory.create(DashboardModuleHistoryAreaChart.class, this.getWebDriver());
    }

    /**
     * Resturns the method chart module, assuming it is displayed.
     *
     * @return the module to be worked with
     */
    public DashboardModuleMethodChart getMethodChartModule() {
        return PageFactory.create(DashboardModuleMethodChart.class, this.getWebDriver());
    }

    /**
     * Performs the click action on a given GuiElement.
     *
     * @param element a specific GuiElement
     * @return an updated DashboardPage Object
     */
    public DashboardPage click(GuiElement element) {
        element.click();
        return PageFactory.create(DashboardPage.class, this.getWebDriver());
    }

    /*

    public void clickerTest() {

        // for selenium action a WebElement element is needed.

        // WebElement knownElement = driver.findElement(By.xpath("//*[@id='methodsPie']"));
        // declaration doesnt work, location of element fails


        GuiElement knownElement;
        Actions builder = new Actions(driver);

        knownElement = new GuiElement(driver, By.xpath("//*[@id='methodsPie']"), mainFrame);
        knownElement.waitForIsDisplayed();
        builder.moveToElement(knownElement.getWebElement(), 2, 5).click().build().perform();
    }

    */

    /**
     * Performs the click action on a given GuiElement.
     *
     * @param methodDetail a specific GuiElement
     * @return a new TesterraReportMethodDetailPage
     */
    public MethodDetailsPage clickMethodDetail(GuiElement methodDetail) {
        methodDetail.click();
        return PageFactory.create(MethodDetailsPage.class, this.getWebDriver());
    }

    /**
     * Returns the test result table header GuiElement for a given test result category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return the table header GuiElement
     */
    public GuiElement getResultTableHeaderForTestResult(TestResultHelper.TestResult testResult) {
        String testState = testResult.getTestState();
        GuiElement testResultTableHeader = new GuiElement(this.getWebDriver(), By.xpath("(//*[@class='header']/../tr[@class='test testbar" + testState + " filterMethods filter" + testState + "'])[1]"), mainFrame);
        testResultTableHeader.setName("testResultTableHeader");

        return testResultTableHeader;
    }

    public void assertSecondRetryMethodIsDisplayed(String actualDisplayedMethodName, String methodName) {
        final String expectedRetrySecondRun = methodName + " (2/2)";
        //Pattern retryPatternSecondRun = Pattern.compile(methodName + "\\s\\(2\\/2\\)");
        Assert.assertEquals(actualDisplayedMethodName, expectedRetrySecondRun, "Second Retry Run method name has the expected pattern");
    }

    /**
     * Check for the visibility of a annotation type for a method name.
     *
     * @param annotationType the type to be checked.
     * @param methodName     a given method which is assumed to be annotated.
     */
    @Override
    public void assertAnnotationMarkIsDisplayed(ReportAnnotationType annotationType, String methodName) {
        GuiElement annotationRow = getMethodChartModule().getMethodChartElementRowByMethodName(methodName);
        annotationRow.asserts().assertIsDisplayed();
        annotationRow.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, annotationType.getAnnotationDisplayedName()))).asserts().assertIsDisplayed();
    }

    /**
     * Check for the visibility of any annotation type for a method name.
     *
     * @param methodName a given method which is assumed to be annotated.
     */
    @Override
    public void assertAllAnnotationMarksAreDisplayed(String methodName) {
        for (ReportAnnotationType annotationType : ReportAnnotationType.values()) {
            assertAnnotationMarkIsDisplayed(annotationType, methodName);
        }
    }

    //@Override
    /*public void assertRetryMarkerIsDisplayed(String methodName) {
        GuiElement annotationRow = getMethodChartModule().getMethodChartElementRowByMethodName(methodName);
        annotationRow.asserts().assertIsDisplayed();
        annotationRow.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, RETRIED_NAME))).asserts().assertIsDisplayed();
    }*/

    public MethodDetailsPage goToMethodDetailsPageUsingPieChartFilter(TestResultHelper.TestResult testResult, String className, String methodName) throws Exception {
        dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        GuiElement barChartElement = dashboardModuleClassBarChart.getBarChartElementByClassName(className);
        barChartElement.click();
        GuiElement methodChartElement = getMethodChartModule().getMethodChartElementRowByMethodName(methodName);
        methodChartElement.click();
        return PageFactory.create(MethodDetailsPage.class, this.getWebDriver());
    }

    public MethodDetailsPage goToMethodDetailsPageUsingTestNumberFilter(TestResultHelper.TestResult testResult, String className, String methodName) throws Exception {
        dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        GuiElement barChartElement = dashboardModuleClassBarChart.getBarChartElementByClassName(className);
        barChartElement.click();
        GuiElement methodChartElement = getMethodChartModule().getMethodChartElementRowByMethodName(methodName);
        methodChartElement.click();
        return PageFactory.create(MethodDetailsPage.class, this.getWebDriver());
    }

}


