package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.Status;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.testerra.report.test.pages.AbstractReportPage;

public class ReportFailureAspectsPage extends AbstractReportPage {

    @Check
    private final GuiElement failureAspectsTable = new GuiElement(getWebDriver(), By.xpath("//table//tbody"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportFailureAspectsPage(WebDriver driver) {
        super(driver);
    }

    public boolean getFailedStateExistence() {
        List<GuiElement> tableRows = failureAspectsTable.getList();
        boolean statusContainsFailed = false;
        for (GuiElement row : tableRows) {
            statusContainsFailed = false;
            for (GuiElement status : row.getSubElement(By.xpath("(//td)[3]/div")).getList()) {
                if (status.getText().contains(Status.FAILED.title)) {
                    if (status.getText().split(" ").length > 2)
                        continue;    //skips expected fails
                    statusContainsFailed = true;
                }
            }
        }
        return statusContainsFailed;
    }

    public List<String> getOrderListOfTopFailureAspects() {
        List<GuiElement> tableRows = failureAspectsTable.getList();
        List<String> failureAspects = new ArrayList<>();
        for (GuiElement row : tableRows) {
            failureAspects.add(row.getSubElement(By.xpath("//td")).getList().get(1).getText());
        }
        return failureAspects;
    }
}