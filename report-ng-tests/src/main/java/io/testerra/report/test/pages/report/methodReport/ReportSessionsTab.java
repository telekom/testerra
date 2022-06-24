package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import io.testerra.report.test.pages.ReportMethodPageType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportSessionsTab extends AbstractReportMethodPage {

    @Check
    private final GuiElement sessionCard = tabPagesContent.getSubElement(By.tagName("mdc-layout-grid-cell"));
    @Check
    private final GuiElement headline = sessionCard.getSubElement(By.xpath("//*[contains(text(),'Session')]"));
    @Check
    private final GuiElement id = sessionCard.getSubElement(By.xpath("/ul//span[contains(text(),'ID')]"));
    @Check
    private final GuiElement userAgent = sessionCard.getSubElement(By.xpath("/ul//span[contains(text(),'User agent')]"));
    @Check
    private final GuiElement node = sessionCard.getSubElement(By.xpath("/ul//span[contains(text(),'Node')]"));
    @Check
    private final GuiElement capabilityHeadline = sessionCard.getSubElement(By.xpath("/div[contains(text(),'Capabilities')]"));
    @Check
    private final GuiElement capabilities = sessionCard.getSubElement(By.xpath("/div[contains(@class,'capabilities-view')]"));

    private final ReportMethodPageType reportMethodPageType = ReportMethodPageType.SESSIONS;

    public ReportSessionsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void assertPageIsValid() {
    }

    @Override
    public ReportMethodPageType getCurrentPageType() {
        return reportMethodPageType;
    }
}
