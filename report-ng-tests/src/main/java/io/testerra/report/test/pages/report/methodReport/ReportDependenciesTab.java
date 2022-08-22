package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportDependenciesTab extends AbstractReportMethodPage {

    @Check
    private final GuiElement testCanvas = tabPagesContent.getSubElement(By.xpath("//canvas"));

    public ReportDependenciesTab(WebDriver driver) {
        super(driver);
    }

}
