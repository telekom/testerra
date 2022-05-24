package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import io.testerra.report.test.pages.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportFailureAspectsPage extends AbstractReportPage {
    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportFailureAspectsPage(WebDriver driver) {
        super(driver);
    }

    public void assertEveryDisplayedFailureAspectGotFailedStatus() {
        List<GuiElement> tableRows = new GuiElement(getWebDriver(), By.xpath("//table//tbody")).getList();
        boolean statusContainsFailed;
        for (GuiElement row : tableRows) {
            statusContainsFailed = false;
            for (GuiElement status : row.getSubElement(By.xpath("(//td)[3]/div")).getList()) {
                if (status.getText().contains("Failed")) {
                    if(status.getText().split(" ").length > 2) continue;    //skips expected fails
                    statusContainsFailed = true;
                }
            }
            Assert.assertTrue(statusContainsFailed, "There should be at least one failed element in every aspect status!");
        }
    }

    public void assertNoDisplayedFailureAspectGotFailedStatus() {
        List<GuiElement> tableRows = new GuiElement(getWebDriver(), By.xpath("//table//tbody")).getList();
        boolean statusContainsFailed;
        for (GuiElement row : tableRows) {
            statusContainsFailed = false;
            for (GuiElement status : row.getSubElement(By.xpath("(//td)[3]/div")).getList()) {
                if (status.getText().contains("Failed")) {
                    System.out.println(Arrays.toString(status.getText().split(" ")));
                    if(status.getText().split(" ").length > 2) continue;    //skips expected fails
                    statusContainsFailed = true;
                }
            }
            Assert.assertFalse(statusContainsFailed, "There should not be a failed element in any aspect status!");
        }
    }

    public List<String> getOrderListOfTopFailureAspects() {
        List<GuiElement> tableRows = new GuiElement(getWebDriver(), By.xpath("//table//tbody/tr")).getList();
        List<String> failureAspects = new ArrayList<>();
        for (GuiElement row : tableRows) {
            failureAspects.add(row.getSubElement(By.xpath("//td")).getList().get(1).getText());
        }
        return failureAspects;
    }
}