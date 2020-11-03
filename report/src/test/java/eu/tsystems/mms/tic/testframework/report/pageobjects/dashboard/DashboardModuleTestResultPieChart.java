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
package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.MouseAction;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractFramePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleTestResultPieChart extends AbstractFramePage {

    // Actual Run Pie Chart
    @Check
    private GuiElement actualPieChart = mainFrame.getSubElement(By.id("methodsPie"));
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
                throw new RuntimeException("Unsupported Pie for TestResult: " + testResult);
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
                throw new RuntimeException("Unsupported mouseAction for GuiElement " + guiElement.getName());
        }
    }
}
