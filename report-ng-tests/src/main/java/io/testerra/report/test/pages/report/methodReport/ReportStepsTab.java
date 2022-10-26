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
import io.testerra.report.test.pages.utils.RegExUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class ReportStepsTab extends AbstractReportMethodPage {


    @Check
    private final UiElement testSteps = tabPagesContent.find(By.xpath("//section[@class='step']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportStepsTab(WebDriver driver) {
        super(driver);
    }

    public void assertSeveralTestStepsAreListed() {
        int amountOfSections = testSteps.list().size();
        Assert.assertTrue(amountOfSections > 1, "There should be at least 2 sections: setup and teardown!");
    }

    public void assertsTestStepsContainFailureAspectMessage(String failureAspectMessage) {
        UiElement errorMessage = testSteps.find(By.xpath("//expandable-error-context//class-name-markup"));

        errorMessage.expect().displayed().is(true, "Steps tab should contain an error message");
        errorMessage.expect().text().is(failureAspectMessage, "Error message on steps tab should contain correct failureAspect-message");
    }

    public void assertEachFailureAspectContainsExpectedStatement(String expectedStatement) {
        testSteps.find(By.xpath("//expandable-error-context")).list().forEach(UiElement::click);
        List<UiElement> errorCodes = testSteps.find(
                By.xpath("//*[contains(@class,'mdc-expandable__content-container')]//*[@class='code-view']")).list().stream().collect(Collectors.toList());

        for (UiElement code : errorCodes) {
            List<String> statements = code.find(By.xpath("//div[contains(@class,'line')]")).list()
                    .stream()
                    .map(uiElement -> uiElement.expect().text().getActual())
                    .collect(Collectors.toList());

            log().info("Found {} statements", statements.size());
            Assert.assertTrue(statements.stream().anyMatch(i -> i.contains(expectedStatement)),
                    String.format("Failure Aspect code should contain expected Statement [%s].", expectedStatement));
        }
    }

    public void assertDurationIsValid(int lowerBound, int upperBound) {
        String duration = getTestDuration();
        String secondsString = RegExUtils.getRegExpResultOfString(RegExUtils.RegExp.DIGITS_ONLY, duration);
        System.out.println(secondsString);
        int seconds = Integer.parseInt(secondsString.trim());
        Assert.assertTrue(lowerBound <= seconds && seconds < upperBound, "Run duration should be in valid interval");
    }

    public void assertCertainTestStepsAreListed(String[] certainTestSteps) {
        UiElement timeline = find(By.xpath("//div[@class='timeline']"));
        List<String> testStepTexts = timeline.find(By.xpath("//section/div")).list()
                .stream()
                .map(uiElement -> uiElement.waitFor().text().getActual())
                .collect(Collectors.toList());

        for (String step : certainTestSteps) {
            boolean actualStepsContainCertainStep = testStepTexts.stream().anyMatch(testStepString -> testStepString.contains(step));
            Assert.assertTrue(actualStepsContainCertainStep, "Steps-Tab should contain step:" + step + "!");
        }
    }
}
