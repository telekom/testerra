package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportConcreteMethodPage extends AbstractMethodReportPage {

    @Check
    private final GuiElement testPrimeCardHeadline = testMethodCard.getSubElement(By.xpath("/div"));
    @Check
    private final GuiElement testClassText = testMethodCard.getSubElement(By.xpath("//li[.//span[contains(text(), 'Class')]]//a"));
    @Check
    private final GuiElement testThreadLink = testMethodCard.getSubElement(By.xpath("//li[.//span[contains(text(), 'Thread')]]//a"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportConcreteMethodPage(WebDriver driver) {
        super(driver);
    }

    private void assertMethodNamesAreCorrect(String methodName) {
        testPrimeCardHeadline.asserts("Displayed method name should match the corresponding link").assertTextContains(methodName);
    }

    private void assertMethodStatesAreCorrect(String methodName) {
        testPrimeCardHeadline.asserts("Displayed status should match the corresponding link").assertTextContains(methodName);
    }

    private void assertMethodClassesAreCorrect(String className) {
        testClassText.asserts("Displayed class name should equal to given class name at test overview!").assertText(className);
    }

    public void assertMethodePrimeCardContainsCorrectContent(String[] methodContent) {
        assertMethodStatesAreCorrect(methodContent[0]);
        assertMethodClassesAreCorrect(methodContent[1]);
        assertMethodNamesAreCorrect(methodContent[3]);
    }

    public ReportThreadsPage clickThreadLink() {
        testThreadLink.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }
}
