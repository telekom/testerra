package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportDetailsPage extends ReportMethodPage {

    @Check
    private final GuiElement pageContent = new GuiElement(getWebDriver(), By.xpath("//router-view[@class='au-target']//mdc-layout-grid"));
    @Check
    private final GuiElement testFailureAspect = pageContent.getSubElement(By.xpath("//mdc-card[./div[text()='Failure Aspect']]"));
    @Check
    private final GuiElement testOriginCard = pageContent.getSubElement(By.xpath("//mdc-card[./div[contains(text(), 'Origin')]]"));
    @Check
    private final GuiElement testStacktraceCard = pageContent.getSubElement(By.xpath("//mdc-card[./div[contains(text(), 'Stacktrace')]]"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportDetailsPage(WebDriver driver) {
        super(driver);
    }


}
