/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractReportMethodPage extends AbstractReportPage {

    //TODO: better locator?
    @Check
    protected final GuiElement testMethodCard = pageContent.getSubElement(By.xpath("(//mdc-card)[1]"));
    //TODO: mandatory?
    // protected final GuiElement testLastScreenshot = pageContent.getSubElement(By.xpath("//mdc-card[contains(text(),'Last Screenshot')]"));
    @Check
    protected final GuiElement testDurationCard = pageContent.getSubElement(By.xpath("//test-duration-card"));
    @Check
    protected final GuiElement testTabBar = pageContent.getSubElement(By.xpath("//mdc-tab-bar"));
    @Check
    protected final GuiElement tabPagesContent = new GuiElement(getWebDriver(), By.xpath("//router-view[./mdc-layout-grid]/router-view"));
    @Check
    private final GuiElement testPrimeCardHeadline = testMethodCard.getSubElement(By.xpath("/div"));
    @Check
    private final GuiElement testClassText = testMethodCard.getSubElement(By.xpath("//li[.//span[contains(text(), 'Class')]]//a"));
    @Check
    private final GuiElement testThreadLink = testMethodCard.getSubElement(By.xpath("//li[.//span[contains(text(), 'Thread')]]//a"));
    @Check
    private final GuiElement testStepsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Steps')]]"));
    private final GuiElement testSessionsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Sessions')]]"));


    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public AbstractReportMethodPage(WebDriver driver) {
        super(driver);
    }

    public ReportStepsTab navigateToStepsTab() {
        testStepsTab.click();
        return PageFactory.create(ReportStepsTab.class, getWebDriver());
    }

    public ReportSessionsTab navigateToSessionsTab() {
        testSessionsTab.click();
        return PageFactory.create(ReportSessionsTab.class, getWebDriver());
    }

    private void assertMethodNamesAreCorrect(String methodName) {
        testPrimeCardHeadline.asserts("Displayed method name should match the corresponding link").assertTextContains(methodName);
    }

    private void assertMethodStateIsCorrect(String statusName) {
        testPrimeCardHeadline.asserts("Displayed status should match the corresponding link").assertTextContains(statusName);
    }

    private void assertMethodClassesAreCorrect(String className) {
        testClassText.asserts("Displayed class name should equal to given class name at test overview!").assertText(className);
    }

    public void assertMethodOverviewContainsCorrectContent(String methodeClass, String status, String name) {
        assertMethodStateIsCorrect(status);
        assertMethodClassesAreCorrect(methodeClass);
        assertMethodNamesAreCorrect(name);
    }

    public ReportThreadsPage clickThreadLink() {
        testThreadLink.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public String getTestDuration() {
        GuiElement durationGuiElement = testDurationCard.getSubElement(By.xpath("//div[contains(@class,'card-content')]"));
        return RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.LINE_BREAK, durationGuiElement.getText());
    }

    public void assertTestMethodeReportContainsFailsAnnotation() {
        GuiElement failsAnnotation = testMethodCard.getSubElement(By.xpath("//*[contains(@class,'status-failed-expected')]"));
        failsAnnotation.asserts("Test page should display @Failed annotation").assertIsDisplayed();
    }
}
