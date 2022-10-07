/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractReportMethodPage extends AbstractReportPage {

    //TODO: better locator?
    @Check
    protected final UiElement testMethodCard = pageContent.find(By.xpath("(//mdc-card)[1]"));
    //TODO: mandatory?
    // protected final GuiElement testLastScreenshot = pageContent.getSubElement(By.xpath("//mdc-card[contains(text(),'Last Screenshot')]"));
    @Check
    protected final UiElement testDurationCard = pageContent.find(By.xpath("//test-duration-card"));
    @Check
    protected final UiElement testTabBar = pageContent.find(By.xpath("//mdc-tab-bar"));
    @Check
    protected final UiElement tabPagesContent = find(By.xpath("//router-view[./mdc-layout-grid]/router-view"));
    @Check
    private final UiElement testPrimeCardHeadline = testMethodCard.find(By.xpath("/div"));
    @Check
    private final UiElement testClassText = testMethodCard.find(By.xpath("//li[.//span[contains(text(), 'Class')]]//a"));
    @Check
    private final UiElement testThreadLink = testMethodCard.find(By.xpath("//li[.//span[contains(text(), 'Thread')]]//a"));
    @Check
    private final UiElement testStepsTab = testTabBar.find(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Steps')]]"));
    private final UiElement testSessionsTab = testTabBar.find(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Sessions')]]"));


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
        return createPage(ReportStepsTab.class);
    }

    public ReportSessionsTab navigateToSessionsTab() {
        testSessionsTab.click();
        return createPage(ReportSessionsTab.class);
    }

    private void assertMethodNamesAreCorrect(String methodName) {
        // testPrimeCardHeadline.asserts("Displayed method name should match the corresponding link").assertTextContains(methodName);
        testPrimeCardHeadline.expect().text().contains(methodName).is(true, "Displayed method name should match the corresponding link");

    }

    private void assertMethodStateIsCorrect(String statusName) {
        //testPrimeCardHeadline.asserts("Displayed status should match the corresponding link").assertTextContains(statusName);
        testPrimeCardHeadline.expect().text().contains(statusName).is(true, "Displayed status should match the corresponding link");
    }

    private void assertMethodClassesAreCorrect(String className) {
        //testClassText.asserts("Displayed class name should equal to given class name at test overview!").assertText(className);
        testClassText.expect().text().is(className, "Displayed class name should equal to given class name at test overview!");
    }

    public void assertMethodOverviewContainsCorrectContent(String methodeClass, String status, String name) {
        assertMethodStateIsCorrect(status);
        assertMethodClassesAreCorrect(methodeClass);
        assertMethodNamesAreCorrect(name);
    }

    public ReportThreadsPage clickThreadLink() {
        testThreadLink.click();
        return createPage(ReportThreadsPage.class);
    }

    public String getTestDuration() {
        UiElement durationGuiElement = testDurationCard.find(By.xpath("//div[contains(@class,'card-content')]"));
        return RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.LINE_BREAK, durationGuiElement.expect().text().getActual());
    }

    public void assertTestMethodeReportContainsFailsAnnotation() {
        UiElement failsAnnotation = testMethodCard.find(By.xpath("//*[contains(@class,'status-failed-expected')]"));
        //failsAnnotation.asserts("Test page should display @Failed annotation").assertIsDisplayed();
        failsAnnotation.expect().displayed().is(true, "Test page should display @Failed annotation");
    }
}
