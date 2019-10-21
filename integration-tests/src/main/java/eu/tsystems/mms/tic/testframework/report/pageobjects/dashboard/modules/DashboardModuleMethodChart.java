package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.LinkedList;
import java.util.List;

public class DashboardModuleMethodChart extends AbstractFramePage {

    @Check
    public final IGuiElement methodChart = new GuiElement(this.driver, By.id("detailsView"), mainFrame);

    //method chart elements
    public final IGuiElement methodChartRepairedFailsIndication = new GuiElement(this.driver, By.xpath("//a[contains(@href, 'test_TestStatePassed2')]//div[@class='skipped']"), mainFrame);
    public final IGuiElement methodChartFailedRetried1 = new GuiElement(this.driver, By.xpath("//tr[@class='header broken']/following-sibling::tr[1]"), mainFrame);
    public final IGuiElement methodChartFailedRetried2 = new GuiElement(this.driver, By.xpath("//tr[@class='header broken']/following-sibling::tr[2]"), mainFrame);
    public final IGuiElement methodChartFailedRetried3 = new GuiElement(this.driver, By.xpath("//tr[@class='header broken']/following-sibling::tr[3]"), mainFrame);
    public final IGuiElement methodChartFailedRetried4 = new GuiElement(this.driver, By.xpath("//tr[@class='header broken']/following-sibling::tr[4]"), mainFrame);
    public final IGuiElement methodChartSuccessfulRetried = new GuiElement(this.driver, By.xpath("//tr[@class='header passed']/following-sibling::tr[1]"), mainFrame);
    public final IGuiElement methodChartTable = methodChart.getSubElement(By.xpath("./table[@class='textleft resultsTable']"));
    public final IGuiElement methodChartFailedMethodsTable = methodChart.getSubElement(By.className("filterFailed"));
    public final IGuiElement methodChartPassedMethodsTable = methodChart.getSubElement(By.className("filterPassed"));
    public final IGuiElement methodChartSkippedMethodsTable = methodChart.getSubElement(By.className("filterSkipped"));

    public DashboardModuleMethodChart(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns all currently displayed test methods.
     *
     * @return a List of GuiElements containing displayed test methods
     */
    public List<IGuiElement> getCurrentMethods() {
        List<IGuiElement> methods = new LinkedList<>();
        int methodCount = methodChartTable.getSubElement(By.xpath(".//a[contains(@href, 'methods')]")).getNumberOfFoundElements();
        for (int i = 1; i <= methodCount; i++) {
            methods.add(methodChartTable.getSubElement(By.xpath("(.//a[contains(@href, 'methods')])[" + i + "]")));
        }
        return methods;
    }

    /**
     * Returns the method chart IGuiElement by a given method name.
     *
     * @param methodName the name of a method
     * @return a method chart IGuiElement
     */
    public IGuiElement getMethodChartElementRowByMethodName(String methodName) {
        IGuiElement methodElement = new GuiElement(driver, By.linkText(methodName), mainFrame);
        methodElement.setName("methodElementFor_" + methodName);
        return methodElement.getSubElement(By.xpath("./../.."));
    }

    /**
     * Checks the display status of a method chart by a given test result.
     *
     * @param testResult representing Failed, Passed or Skipped
     */
    public void assertMethodChartIsDisplayedForTestResult(TestResultHelper.TestResult testResult) {
        switch (testResult) {
            case FAILED:
                methodChartFailedMethodsTable.asserts().assertIsDisplayed();
                break;
            case PASSED:
                methodChartPassedMethodsTable.asserts().assertIsDisplayed();
                break;
            case SKIPPED:
                methodChartSkippedMethodsTable.asserts().assertIsDisplayed();
                break;
            default:
                throw new TesterraRuntimeException("Method not implemented for TestResult: " + testResult);
        }
    }

    /**
     * Returns the method quantity for a method chart by a given test result.
     *
     * @param testResult representing Failed, Passed or Skipped
     * @return the method count
     */
    public int getNumberMethodsInMethodChartForTestResult(TestResultHelper.TestResult testResult) {
        switch (testResult) {
            case FAILED:
                return methodChartFailedMethodsTable.getWebElement().findElements(By.tagName("tr")).size() - 1;
            case PASSED:
                return methodChartPassedMethodsTable.getWebElement().findElements(By.tagName("tr")).size() - 1;
            case SKIPPED:
                return methodChartSkippedMethodsTable.getWebElement().findElements(By.tagName("tr")).size() - 1;
            default:
                throw new TesterraRuntimeException("Method not implemented for TestResult: " + testResult);
        }
    }
}
