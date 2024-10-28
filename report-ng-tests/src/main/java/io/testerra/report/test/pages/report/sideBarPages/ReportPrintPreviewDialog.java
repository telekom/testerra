package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportPrintPreviewDialog extends AbstractReportPage {
    @Check
    private final UiElement iFrame = find(By.xpath("//mdc-dialog//mdc-layout-grid-cell//iframe"));
    @Check
    private final UiElement iFrameTitle = iFrame.find(By.xpath("//mdc-top-app-bar//mdc-top-app-bar-title"));

    public ReportPrintPreviewDialog(WebDriver driver) {
        super(driver);
    }

    public void checkIFrameTitle(){
        String titleText = title.expect().text().getActual();
        iFrameTitle.expect().text().isContaining(titleText);
    }
}