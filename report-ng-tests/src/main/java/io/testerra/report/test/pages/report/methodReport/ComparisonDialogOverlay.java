package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ComparisonDialogOverlay extends Page {
    @Check
    private final UiElement contrastIcon = findIconInDialog("compare");
    @Check
    private final UiElement switchIcon = findIconInDialog("compare_arrows");
    @Check
    private final UiElement leftSelection = find(By.xpath("//mdc-select[@label='Left']"));
    @Check
    private final UiElement rightSelection = find(By.xpath("//mdc-select[@label='Right']"));

    public ComparisonDialogOverlay(WebDriver webDriver) {
        super(webDriver);
    }

    private UiElement findIconInDialog(String icon){
        return find(By.xpath(String.format("//mdc-icon[text()='%s']",icon)));
    }

    public ReportDetailsTab closeDialog() {
        UiElement closeBtn = find(By.xpath("//button//span[text()='clear']"));
        closeBtn.click();
        return createPage(ReportDetailsTab.class);
    }
}
