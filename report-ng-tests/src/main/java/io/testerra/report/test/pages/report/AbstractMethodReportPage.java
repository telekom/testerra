package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportPageType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractMethodReportPage extends AbstractReportPage {

    //TODO: there is maybe a better locator
    @Check
    protected final GuiElement testMethodCard = pageContent.getSubElement(By.xpath("(//mdc-card)[1]"));

    protected final GuiElement testLastScreenshot = pageContent.getSubElement(By.xpath("//mdc-card[contains(text(),'Last Screenshot')]"));
    @Check
    protected final GuiElement testDurationCard = pageContent.getSubElement(By.xpath("//test-duration-card"));
    @Check
    protected final GuiElement testTabBar = pageContent.getSubElement(By.xpath("//mdc-tab-bar"));

    private final GuiElement testDetailsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Details')]]"));

    private final GuiElement testStepsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Steps')]]"));

    private final GuiElement testSessionsTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Sessions')]]"));

    private final GuiElement testDependenciesTab = testTabBar.getSubElement(By.xpath("//mdc-tab[.//span[@class='mdc-tab__text-label' and contains(text(),'Dependencies')]]"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public AbstractMethodReportPage(WebDriver driver) {
        super(driver);
    }

    protected <T extends AbstractMethodReportPage> T navigateBetweenTabs(ReportPageType reportPageType, Class<T> reportPageClass) {
        switch (reportPageType) {
            case DETAILS:
                testDetailsTab.click();
                break;
            case STEPS:
                testStepsTab.click();
                break;
            case SESSIONS:
                testSessionsTab.click();
                break;
            case DEPENDENCIES:
                testDependenciesTab.click();
                break;
        }
        return PageFactory.create(reportPageClass, getWebDriver());
    }


}
