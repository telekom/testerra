package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportThreadsPage extends AbstractReportPage {

    @Check
    private final GuiElement testMethodSearchbar = pageContent.getSubElement(By.xpath("//label[@label='Method']//input"));
    @Check
    private final GuiElement testMethodDropDownList = pageContent.getSubElement(By.xpath("//div[./label[@label='Method']]//mdc-lookup"));
    @Check
    private final GuiElement testThreadReport = pageContent.getSubElement(By.xpath("//div[@class='vis-foreground']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportThreadsPage(WebDriver driver) {
        super(driver);
    }

    public ReportThreadsPage search(String filter) {
        testMethodSearchbar.type(filter);
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public ReportThreadsPage selectMethod(String method) {
        GuiElement methodAsGuiElement = testMethodDropDownList.getSubElement(By.xpath(String.format("//mdc-list-item[.//span[text()='%s']]", method)));
        methodAsGuiElement.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public void assertMethodBoxIsSelected(String method) {
        GuiElement subElement = testThreadReport.getSubElement(
                By.xpath("//div[contains(@class, 'vis-item') and contains(@class, 'vis-range') and .//div[text()='" + method + "']]"));
        subElement.asserts("method box is selected").assertAttributeContains("class", "vis-selected");
    }

    public ReportThreadsPage clickSearchBar(){
        testMethodSearchbar.click();
        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }
}
