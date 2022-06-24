package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import io.testerra.report.test.pages.ReportMethodPageType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportDependenciesTab extends AbstractReportMethodPage {

    @Check
    private final GuiElement testCanvas = tabPagesContent.getSubElement(By.xpath("//canvas"));

    private final ReportMethodPageType reportMethodPageType = ReportMethodPageType.DEPENDENCIES;

    public ReportDependenciesTab(WebDriver driver) {
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
