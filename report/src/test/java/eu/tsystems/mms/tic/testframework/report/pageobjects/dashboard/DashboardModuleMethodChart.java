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

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractFramePage;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleMethodChart extends AbstractFramePage {

    @Check
    public final GuiElement methodChart = mainFrame.getSubElement(By.id("detailsView"));

    //method chart elements
    public final GuiElement methodChartRepairedFailsIndication = mainFrame.getSubElement(By.xpath("//a[contains(@href, 'test_TestStatePassed2')]//div[@class='skipped']"));
    public final GuiElement methodChartFailedRetried1 = mainFrame.getSubElement(By.xpath("//tr[@class='header broken']/following-sibling::tr[1]"));
    public final GuiElement methodChartFailedRetried2 = mainFrame.getSubElement(By.xpath("//tr[@class='header broken']/following-sibling::tr[2]"));
    public final GuiElement methodChartFailedRetried3 = mainFrame.getSubElement(By.xpath("//tr[@class='header broken']/following-sibling::tr[3]"));
    public final GuiElement methodChartFailedRetried4 = mainFrame.getSubElement(By.xpath("//tr[@class='header broken']/following-sibling::tr[4]"));
    public final GuiElement methodChartSuccessfulRetried = mainFrame.getSubElement(By.xpath("//tr[@class='header passed']/following-sibling::tr[1]"));
    public final GuiElement methodChartTable = methodChart.getSubElement(By.xpath("./table[@class='textleft resultsTable']"));
    public final GuiElement methodChartFailedMethodsTable = methodChart.getSubElement(By.className("filterFailed"));
    public final GuiElement methodChartPassedMethodsTable = methodChart.getSubElement(By.className("filterPassed"));
    public final GuiElement methodChartSkippedMethodsTable = methodChart.getSubElement(By.className("filterSkipped"));

    public DashboardModuleMethodChart(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns all currently displayed test methods.
     *
     * @return a List of GuiElements containing displayed test methods
     */
    public List<GuiElement> getCurrentMethods() {
        List<GuiElement> methods = new LinkedList<>();
        int methodCount = methodChartTable.getSubElement(By.xpath(".//a[contains(@href, 'methods')]")).getNumberOfFoundElements();
        for (int i = 1; i <= methodCount; i++) {
            methods.add(methodChartTable.getSubElement(By.xpath("(.//a[contains(@href, 'methods')])[" + i + "]")));
        }
        return methods;
    }

    /**
     * Returns the method chart GuiElement by a given method name.
     *
     * @param methodName the name of a method
     * @return a method chart GuiElement
     */
    public GuiElement getMethodChartElementRowByMethodName(String methodName) {
        GuiElement methodElement = mainFrame.getSubElement(By.linkText(methodName));
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
