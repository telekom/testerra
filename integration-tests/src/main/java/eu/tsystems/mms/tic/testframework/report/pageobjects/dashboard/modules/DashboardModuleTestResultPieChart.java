package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import eu.tsystems.mms.tic.testframework.report.model.MouseAction;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleTestResultPieChart extends AbstractFramePage {

    // Actual Run Pie Chart
    @Check
    private GuiElement actualPieChart = new GuiElement(this.driver, By.id("methodsPie"), mainFrame);
    private GuiElement passedPieSegment = actualPieChart.getSubElement(By.id("Passed"));
    private GuiElement passedInheritedPieSegment = actualPieChart.getSubElement(By.id("InheritedPassed"));
    private GuiElement passedMinorPieSegment = actualPieChart.getSubElement(By.id("Minor"));
    private GuiElement failedPieSegment = actualPieChart.getSubElement(By.id("Failed"));
    private GuiElement failedInheritedPieSegment = actualPieChart.getSubElement(By.id("InheritedFailed"));
    private GuiElement failedMinorPieSegment = actualPieChart.getSubElement(By.id("Failed with Minor"));
    private GuiElement skippedPieSegment = actualPieChart.getSubElement(By.id("Skipped"));
    private GuiElement skippedInheritedPieSegment = actualPieChart.getSubElement(By.id("InheritedSkipped"));
    private GuiElement failedExpectedPieSegment = actualPieChart.getSubElement(By.id("Expected Failed"));

    // Last Run Pie Chart
    private GuiElement lastRunPieChart = new GuiElement(this.driver, By.id("lastRunMethodsPie"), mainFrame);
    private GuiElement lastRunPassedPieSegment = lastRunPieChart.getSubElement(By.id("Passed"));
    private GuiElement lastRunPassedInheritedPieSegment = lastRunPieChart.getSubElement(By.id("InheritedPassed"));
    private GuiElement lastRunPassedMinorPieSegment = lastRunPieChart.getSubElement(By.id("Minor"));
    private GuiElement lastRunFailedPieSegment = lastRunPieChart.getSubElement(By.id("Failed"));
    private GuiElement lastRunFailedInheritedPieSegment = lastRunPieChart.getSubElement(By.id("InheritedFailed"));
    private GuiElement lastRunFailedMinorPieSegment = lastRunPieChart.getSubElement(By.id("Failed with Minor"));
    private GuiElement lastRunSkippedPieSegment = lastRunPieChart.getSubElement(By.id("Skipped"));
    private GuiElement lastRunSkippedInheritedPieSegment = lastRunPieChart.getSubElement(By.id("InheritedSkipped"));

    private GuiElement showLastRunRadio = new GuiElement(this.driver, By.id("lastRunBarsVisibleCheckbox"), mainFrame);

    public DashboardModuleTestResultPieChart(WebDriver driver) {
        super(driver);
    }

    /**
     * Triggers the click Event for a pie chart segment (previous test) depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return an updated DashboardPage Object
     * @throws
     */
    public DashboardPage clickLastRunPieSegmentForTestResult(TestResultHelper.TestResult testResult) throws Exception {
        GuiElement pie = getLastRunPieSegment(testResult);
        triggerGuiElement(pie.getSubElement(By.xpath("./../*[@class='lightPie']")), MouseAction.CLICK);
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    /**
     * Triggers the hover Event for a pie chart segment (previous test) depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return an updated DashboardPage Object
     * @throws
     */
    public DashboardPage hoverLastRunPieSegment(TestResultHelper.TestResult testResult) throws Exception {
        GuiElement pie = getLastRunPieSegment(testResult);
        triggerGuiElement(pie, MouseAction.MOUSE_OVER);
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    /**
     * Triggers the click Event for a pie chart segment (actual test) depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return an updated DashboardPage Object
     * @throws
     */
    public DashboardPage clickActualRunPieSegmentForTestResult(TestResultHelper.TestResult testResult) throws Exception {
        GuiElement pie = getActualRunPieSegment(testResult);
        triggerGuiElement(pie.getSubElement(By.xpath("./../*[@class='lightPie']")), MouseAction.CLICK);
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    /**
     * Triggers the hover Event for a pie chart segment (previous test) depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return an updated DashboardPage Object
     * @throws
     */
    public DashboardPage hoverActualRunPieSegmentForTestResult(TestResultHelper.TestResult testResult) throws Exception {
        GuiElement pie = getActualRunPieSegment(testResult);
        triggerGuiElement(pie, MouseAction.MOUSE_OVER);
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    /**
     * Returns a pie chart segment (actual test) depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return the pie chart segment GuiElement
     * @throws
     */
    public GuiElement getActualRunPieSegment(TestResultHelper.TestResult testResult) throws Exception {
        GuiElement pie;
        switch (testResult) {
            case PASSED:
                pie = passedPieSegment;
                break;
            case PASSEDINHERITED:
                pie = passedInheritedPieSegment;
                break;
            case PASSEDMINOR:
                pie = passedMinorPieSegment;
                break;
            case FAILED:
                pie = failedPieSegment;
                break;
            case FAILEDINHERITED:
                pie = failedInheritedPieSegment;
                break;
            case FAILEDMINOR:
                pie = failedMinorPieSegment;
                break;
            case SKIPPED:
                pie = skippedPieSegment;
                break;
            case SKIPPEDINHERITED:
                pie = skippedInheritedPieSegment;
                break;
            case FAILEDEXPECTED:
                pie = failedExpectedPieSegment;
                break;
            default:
                throw new FennecRuntimeException("Unsupported Pie for TestResult: " + testResult);
        }
        return pie;
    }

    /**
     * Returns a pie chart segment (previous test) depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return the pie chart segment GuiElement
     * @throws
     */
    public GuiElement getLastRunPieSegment(TestResultHelper.TestResult testResult) throws Exception {
        GuiElement pie;
        switch (testResult) {
            case PASSED:
                pie = lastRunPassedPieSegment;
                break;
            case PASSEDINHERITED:
                pie = lastRunPassedInheritedPieSegment;
                break;
            case PASSEDMINOR:
                pie = lastRunPassedMinorPieSegment;
                break;
            case FAILED:
                pie = lastRunFailedPieSegment;
                break;
            case FAILEDINHERITED:
                pie = lastRunFailedInheritedPieSegment;
                break;
            case FAILEDMINOR:
                pie = lastRunFailedMinorPieSegment;
                break;
            case SKIPPED:
                pie = lastRunSkippedPieSegment;
                break;
            case SKIPPEDINHERITED:
                pie = lastRunSkippedInheritedPieSegment;
                break;
            default:
                throw new FennecRuntimeException("Unsupported last run pie for TestResult: " + testResult);
        }
        return pie;
    }

    /**
     * Triggers a given event on a given GuiElement.
     *
     * @param guiElement the element to be triggered depending on MouseAction
     * @param mouseAction     the mouseAction to be performed
     * @throws Exception
     */
    public void triggerGuiElement(GuiElement guiElement, MouseAction mouseAction) throws Exception {
        switch (mouseAction) {
            case CLICK:
                guiElement.click();
                break;
            case MOUSE_OVER:
                guiElement.mouseOver();
                break;
            default:
                throw new FennecRuntimeException("Unsupported mouseAction for GuiElement " + guiElement.getName());
        }
    }

    /**
     * Triggers the select/deselect Event for the last run checkbox.
     *
     * @param select the intend of selecting or deselcting
     * @return an updated DashboardPage Object
     */
    public DashboardPage selectShowLastRunRadio(boolean select) {
        if (showLastRunRadio.isSelected() && !select)
            showLastRunRadio.deselect();
        else if (!showLastRunRadio.isSelected() && select)
            showLastRunRadio.select();
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    public boolean isLastRunRadioDisplayed() {
        if (showLastRunRadio.isDisplayed())
            return true;
        else
            return false;
    }
}
