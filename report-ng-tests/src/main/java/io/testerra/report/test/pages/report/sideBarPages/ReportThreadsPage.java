package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Optional;

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
        // TODO: use explicit locator, like //div[contains(@class, 'vis-item') and contains(@class, 'vis-range') and .//div[text()='<method>']]
        // does not work, I do not know why?
        // GuiElement element = testMethodDropDownList.getSubElement(By.xpath(String.format("//mdc-list-item//mark[text()='%s']",method)));
        // element.waits().waitForIsDisplayed();
        // element.click();

        //this does
        testMethodDropDownList.getSubElement(By.xpath("//mdc-list-item")).waits().waitForIsDisplayed();
        Optional<GuiElement> optional = testMethodDropDownList.getSubElement(By.xpath("//mdc-list-item")).getList()
                .stream()
                .filter(i -> i.getText().equals(method))
                .findFirst();
        Assert.assertTrue(optional.isPresent(), String.format("expected sth for %s", method));
        optional.get().click();

        return PageFactory.create(ReportThreadsPage.class, getWebDriver());
    }

    public void assertMethodBoxIsSelected(String method) {
        // TODO: use explicit locator, like //div[contains(@class, 'vis-item') and contains(@class, 'vis-range') and .//div[text()='<method>']]

        // does not work, I do not know why not?
        // testThreadReport.getSubElement(By.xpath("//div[contains(@class,'vis-selected')]")).asserts().assertTextContains(method);

        // this does
        for (GuiElement guiElement : testThreadReport.getSubElement(By.xpath("div")).getList()) {
            if (guiElement.getText().split("\n")[0].equals(method.trim())) {
                guiElement.getSubElement(By.xpath("/div"))
                        .asserts("Searched element should marked as selected")
                        .assertAttributeContains("class", "vis-selected");
            }
        }
    }
}
