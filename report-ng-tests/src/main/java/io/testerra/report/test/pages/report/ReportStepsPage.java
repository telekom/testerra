package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ReportStepsPage extends AbstractMethodReportPage {

    @Check
    private final GuiElement testSteps = tabBarPagesContent.getSubElement(By.xpath("//section[@class='step']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportStepsPage(WebDriver driver) {
        super(driver);
    }

    public void assertSeveralTestStepsAreListed() {
        int amountOfSections = testSteps.getNumberOfFoundElements();
        Assert.assertTrue(amountOfSections > 1, "There should be at least 2 sections: setup and teardown!");
    }

    public void assertTestStepsContainFailureAspectMessage(String failureAspectMessage) {
        GuiElement errorMessage = testSteps.getSubElement(By.xpath("//expandable-error-context//class-name-markup"));
        errorMessage.asserts().assertIsDisplayed();
        errorMessage.asserts().assertText(failureAspectMessage);
    }
}
