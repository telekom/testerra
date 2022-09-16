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
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class ReportStepsTab extends AbstractReportMethodPage {


    @Check
    private final GuiElement testSteps = tabPagesContent.getSubElement(By.xpath("//section[@class='step']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportStepsTab(WebDriver driver) {
        super(driver);
    }

    public void assertSeveralTestStepsAreListed() {
        int amountOfSections = testSteps.getNumberOfFoundElements();
        Assert.assertTrue(amountOfSections > 1, "There should be at least 2 sections: setup and teardown!");
    }

    public void assertsTestStepsContainFailureAspectMessage(String failureAspectMessage) {
        GuiElement errorMessage = testSteps.getSubElement(By.xpath("//expandable-error-context//class-name-markup"));
        errorMessage.asserts("Steps tab should contain an error message").assertIsDisplayed();
        errorMessage.asserts("Error message on steps tab should contain correct failureAspect-message").assertText(failureAspectMessage);
    }

    public void assertEachFailureAspectContainsExpectedStatement(String expectedStatement){
        testSteps.getSubElement(By.xpath("//expandable-error-context")).getList().forEach(GuiElement::click);
        List<GuiElement> errorCodes = testSteps.getSubElement(
                By.xpath("//*[contains(@class,'mdc-expandable__content-container')]//*[@class='code-view']")).getList();

        for (GuiElement code : errorCodes){
            List<String> statements = code.getSubElement(By.xpath("//div[contains(@class,'line')]")).getList()
                    .stream()
                    .map(GuiElement::getText)
                    .collect(Collectors.toList());

            log().info("Found {} statements", statements.size());
            Assert.assertTrue(statements.stream().anyMatch(i -> i.contains(expectedStatement)),
                    String.format("Failure Aspect code should contain expected Statement [%s].", expectedStatement));
        }
    }

}
