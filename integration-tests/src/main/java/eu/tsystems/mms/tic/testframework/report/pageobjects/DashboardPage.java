package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractReportPage;
import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

/**
 * Created by riwa on 25.10.2016.
 */
public class DashboardPage extends AbstractReportPage implements IReportAnnotationVerifier {
    @Check
    public final GuiElement fennecLogo = new GuiElement(this.driver, By.cssSelector("img[alt='fennec']"), mainFrame);
    public final DashboardModuleTestResultPieChart dashboardModuleTestResultPieChart = PageFactory.create(DashboardModuleTestResultPieChart.class, driver);
    public final DashboardModuleTestResultNumberBreakdown dashboardModuleTestResultNumberBreakdown = PageFactory.create(DashboardModuleTestResultNumberBreakdown.class, driver);
    public final DashboardModuleFailureCorridor dashboardModuleFailureCorridor = PageFactory.create(DashboardModuleFailureCorridor.class, driver);
    public final DashboardModuleInformationCorridor dashboardModuleInformationCorridor = PageFactory.create(DashboardModuleInformationCorridor.class, driver);
    public final DashboardModuleClassBarChart dashboardModuleClassBarChart = PageFactory.create(DashboardModuleClassBarChart.class, driver);
    public final GuiElement screenShotInfoButton = new GuiElement(this.driver, By.xpath("//*[contains(text(),'test_testMidCorridorFailed1')]"), mainFrame);

    public DashboardPage(WebDriver driver) {
        super(driver);
        dashboardModuleClassBarChart.barChartBars.waitForIsDisplayed();
    }

    /**
     * Returns the history chart module, assuming it is displayed.
     *
     * @return the module to be worked with
     */
    public DashboardModuleHistoryAreaChart getHistoryAreaChartModule() {
        return PageFactory.create(DashboardModuleHistoryAreaChart.class, driver);
    }

    /**
     * Resturns the method chart module, assuming it is displayed.
     *
     * @return the module to be worked with
     */
    public DashboardModuleMethodChart getMethodChartModule() {
        return PageFactory.create(DashboardModuleMethodChart.class, driver);
    }

    /**
     * Performs the click action on a given GuiElement.
     *
     * @param element a specific GuiElement
     * @return an updated DashboardPage Object
     */
    public DashboardPage click(GuiElement element) {
        element.click();
        return PageFactory.create(DashboardPage.class, this.driver);
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
     * @return a new FennecReportMethodDetailPage
     */
    public MethodDetailsPage clickMethodDetail(GuiElement methodDetail) {
        methodDetail.click();
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

    /**
     * Returns the test result table header GuiElement for a given test result category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return the table header GuiElement
     */
    public GuiElement getResultTableHeaderForTestResult(TestResultHelper.TestResult testResult) {
        String testState = testResult.getTestState();
        GuiElement testResultTableHeader = new GuiElement(driver, By.xpath("(//*[@class='header']/../tr[@class='test testbar"+testState+" filterMethods filter"+testState+"'])[1]"), mainFrame);
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
     * @param methodName a given method which is assumed to be annotated.
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
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

    public MethodDetailsPage goToMethodDetailsPageUsingTestNumberFilter(TestResultHelper.TestResult testResult, String className, String methodName) throws Exception{
        dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        GuiElement barChartElement = dashboardModuleClassBarChart.getBarChartElementByClassName(className);
        barChartElement.click();
        GuiElement methodChartElement = getMethodChartModule().getMethodChartElementRowByMethodName(methodName);
        methodChartElement.click();
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

}


