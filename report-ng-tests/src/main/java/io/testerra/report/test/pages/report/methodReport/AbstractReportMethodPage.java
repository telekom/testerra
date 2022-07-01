package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractReportMethodPage extends AbstractReportPage {

    //TODO: better locator?
    @Check
    protected final GuiElement testMethodCard = pageContent.getSubElement(By.xpath("(//mdc-card)[1]"));
    //TODO: mandatory?
    //protected final GuiElement testLastScreenshot = pageContent.getSubElement(By.xpath("//mdc-card[contains(text(),'Last Screenshot')]"));
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
    private final GuiElement testDetailsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Details')]]"));
    private final GuiElement testStepsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Steps')]]"));
    private final GuiElement testSessionsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Sessions')]]"));
    private final GuiElement testDependenciesTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Dependencies')]]"));


    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public AbstractReportMethodPage(WebDriver driver) {
        super(driver);
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

    public void assertMethodOverviewContainsCorrectContent(String[] methodContent) {
        assertMethodStateIsCorrect(methodContent[0]);
        assertMethodClassesAreCorrect(methodContent[1]);
        assertMethodNamesAreCorrect(methodContent[3]);
    }

    public ReportThreadsPage clickThreadLink() {
        testThreadLink.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public String getTestDuration() {
        GuiElement durationGuiElement = testDurationCard.getSubElement(By.xpath("//div[contains(@class,'card-content')]"));
        // TODO try using fitting regex
        return durationGuiElement.getText().split("\n")[1];
    }

    public GuiElement getTestDetailsTab() {
        return testDetailsTab;
    }

    public GuiElement getTestStepsTab() {
        return testStepsTab;
    }

    public GuiElement getTestSessionsTab() {
        return testSessionsTab;
    }

    public GuiElement getTestDependenciesTab() {
        return testDependenciesTab;
    }

    public abstract ReportMethodPageType getCurrentPageType();

    protected abstract void assertPageIsValid();

    public void assertTestMethodeReportContainsFailsAnnotation() {
        GuiElement failsAnnotation = testMethodCard.getSubElement(By.xpath("//*[contains(@class,'status-failed-expected')]"));
        failsAnnotation.asserts("Test page should display @Failed annotation").assertIsDisplayed();
    }
}
