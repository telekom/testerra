package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import io.testerra.report.test.pages.ReportMethodPageType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class ReportStepsTab extends AbstractReportMethodPage {


    @Check
    private final GuiElement testSteps = tabPagesContent.getSubElement(By.xpath("//section[@class='step']"));

    private final ReportMethodPageType reportMethodPageType = ReportMethodPageType.STEPS;

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportStepsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public ReportMethodPageType getCurrentPageType() {
        return reportMethodPageType;
    }

    @Override
    public void assertPageIsValid() {
        assertSeveralTestStepsAreListed();
    }

    private void assertSeveralTestStepsAreListed() {
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
        for(GuiElement code : errorCodes){
            List<String> statements = code.getSubElement(By.xpath("//div[contains(@class,'line')]")).getList()
                    .stream()
                    .map(GuiElement::getText)
                    .collect(Collectors.toList());
            System.out.println(statements.size());
            statements.forEach(System.out::println);
            Assert.assertTrue(statements.stream().anyMatch(i -> i.contains(expectedStatement)),
                    String.format("Failure Aspect code should contain expected Statement [%s].", expectedStatement));
        }
    }

}
