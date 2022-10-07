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
import eu.tsystems.mms.tic.testframework.report.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Locale;

public class ReportDetailsTab extends AbstractReportMethodPage {
    @Check
    private final UiElement pageContent = find(By.xpath("//router-view[@class='au-target']//mdc-layout-grid"));
    @Check
    private final UiElement testFailureAspect = pageContent.find(By.xpath("//mdc-card[./div[text()='Failure Aspect']]"));
    //TODO: mandatory?
    private final UiElement testOriginCard = pageContent.find(By.xpath("//mdc-card[./div[contains(text(), 'Origin')]]"));
    @Check
    private final UiElement testStacktraceCard = pageContent.find(By.xpath("//mdc-card[./div[contains(text(), 'Stacktrace')]]"));

    public ReportDetailsTab(WebDriver driver) {
        super(driver);
    }

    public void assertFailureAspectsCorrespondsToCorrectStatus(String expectedStatusTitle) {
        String expectedStatusTitleFormatted = expectedStatusTitle.toLowerCase();
        if (expectedStatusTitleFormatted.equals(Status.FAILED_EXPECTED.title.toLowerCase(Locale.ROOT)))
            expectedStatusTitleFormatted = "failed-expected";
        UiElement failureAspectColoredPart = testFailureAspect.find(By.xpath("//div[contains(@class, 'status')]"));
        //failureAspectColoredPart.asserts(String.format("Failure Aspect status [%s] should correspond to method state [%s]", failureAspectColoredPart.getAttribute("class"), expectedStatusTitleFormatted)).assertAttributeContains("class", expectedStatusTitleFormatted);

        failureAspectColoredPart.expect().attribute("class").contains(expectedStatusTitleFormatted).is(true,
                String.format("Failure Aspect status [%s] should correspond to method state [%s]", failureAspectColoredPart.expect().attribute("class").getActual(), expectedStatusTitleFormatted));
    }

    public void assertTestMethodContainsCorrectFailureAspect(String correctFailureAspect) {
        String failureAspectCodeLineXPath = "//div[contains(@class,'line') and contains(@class,'error')]/span[@class='au-target']";
        UiElement failureAspectCodeLine = testOriginCard.find(By.xpath(failureAspectCodeLineXPath));
        String failureAspectCodeLineAsString = failureAspectCodeLine.expect().text().getActual();
        Assert.assertTrue(failureAspectCodeLineAsString.contains(correctFailureAspect),
                "Given failure aspect should match the code-line in origin-card!");
    }

    public void assertSkippedTestContainsCorrespondingFailureAspect() {
        boolean skippedTestContainsCorrespondingFailureAspect = skippedTestDependsOnFailedMethod() || skippedTestContainsSkipException() || skippedTestFailsInBeforeMethod() || skippedTestFailsInDataProvider();
        //at least one condition should be true
        Assert.assertTrue(skippedTestContainsCorrespondingFailureAspect, "One skipped condition should correspond to skipped test!");
    }

    private boolean skippedTestContainsSkipException() {
        String failureAspectCodeLineXPath = "//div[contains(@class,'line') and contains(@class,'error')]/span[@class='au-target']";
        UiElement failureAspectCodeLine = testOriginCard.find(By.xpath(failureAspectCodeLineXPath));
        if (failureAspectCodeLine.waitFor().displayed(true))
            return failureAspectCodeLine.expect().text().getActual().contains("SkipException");
        return false;
    }

    private boolean skippedTestFailsInDataProvider() {
        //check whether code contains '@DataProvider'
        UiElement dataProviderCode = testOriginCard.find(By.xpath("//div[contains(@class,'line')]//span[contains(text(),'@DataProvider')]"));
        return dataProviderCode.waitFor().displayed(true);
    }

    private boolean skippedTestFailsInBeforeMethod() {
        //check whether code contains '@BeforeMethod()'
        UiElement beforeMethodCode = testOriginCard.find(By.xpath("//div[contains(@class,'line')]//span[contains(text(),'@BeforeMethod()')]"));
        return beforeMethodCode.waitFor().displayed(true);
    }

    private boolean skippedTestDependsOnFailedMethod() {
        String failureAspect = testFailureAspect.expect().text().getActual().split("\n")[1];
        String expectedContainedText = "depends on not successfully finished methods";
        return failureAspect.contains(expectedContainedText);
    }

    public void assertPageIsValid(){
        expandStacktrace();
        assertStacktraceIsDisplayed();
        assertContainsCodeLines();
        assertFailLureLineIsMarked();
    }

    private void expandStacktrace() {
        UiElement expander = testStacktraceCard.find(By.xpath("//mdc-expandable"));
        expander.expect().displayed().is(true);
        expander.click();
    }

    private void assertStacktraceIsDisplayed() {
        UiElement stacktrace = testStacktraceCard.find(By.xpath("//mdc-expandable//div[@class='code-view']"));
        stacktrace.expect().displayed().is(true);
        Assert.assertTrue(stacktrace.list().size() > 0, "There should be some line of stack trace displayed!");
    }

    private void assertContainsCodeLines() {
        UiElement failureOrigin = testOriginCard.find(By.xpath("//div[@class='code-view']"));
        failureOrigin.expect().displayed().is(true);
        Assert.assertTrue(failureOrigin.list().size() > 0, "There should be some line of code displayed!");
    }

    private void assertFailLureLineIsMarked() {
        UiElement markedFailureCodeLine = testOriginCard.find(By.xpath("//div[@class='code-view']//div[contains(@class,'error')]"));
        markedFailureCodeLine.expect().displayed().is(true);
    }

   public String getFailureAspect() {
        return testFailureAspect.expect().text().getActual().split("\n")[1];
    }
}
