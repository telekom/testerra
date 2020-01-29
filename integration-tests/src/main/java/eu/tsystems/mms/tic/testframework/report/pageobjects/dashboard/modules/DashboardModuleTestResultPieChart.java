package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
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
    private GuiElement actualPieChart = new GuiElement(this.getWebDriver(), By.id("methodsPie"), mainFrame);
    private GuiElement passedPieSegment = actualPieChart.getSubElement(By.id("Passed"));
    private GuiElement passedMinorPieSegment = actualPieChart.getSubElement(By.id("Minor"));
    private GuiElement failedPieSegment = actualPieChart.getSubElement(By.id("Failed"));
    private GuiElement failedMinorPieSegment = actualPieChart.getSubElement(By.id("Failed with Minor"));
    private GuiElement skippedPieSegment = actualPieChart.getSubElement(By.id("Skipped"));
    private GuiElement failedExpectedPieSegment = actualPieChart.getSubElement(By.id("Expected Failed"));

    public DashboardModuleTestResultPieChart(WebDriver driver) {
        super(driver);
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
        return PageFactory.create(DashboardPage.class, this.getWebDriver());
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
        return PageFactory.create(DashboardPage.class, this.getWebDriver());
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
            case PASSEDMINOR:
                pie = passedMinorPieSegment;
                break;
            case FAILED:
                pie = failedPieSegment;
                break;
            case FAILEDMINOR:
                pie = failedMinorPieSegment;
                break;
            case SKIPPED:
                pie = skippedPieSegment;
                break;
            case FAILEDEXPECTED:
                pie = failedExpectedPieSegment;
                break;
            default:
                throw new TesterraRuntimeException("Unsupported Pie for TestResult: " + testResult);
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
                throw new TesterraRuntimeException("Unsupported mouseAction for GuiElement " + guiElement.getName());
        }
    }
}
