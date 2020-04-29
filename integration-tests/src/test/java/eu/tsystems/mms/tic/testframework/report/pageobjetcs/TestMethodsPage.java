package eu.tsystems.mms.tic.testframework.report.pageobjetcs;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TestMethodsPage extends AbstractReportPage {

    @Check
    private GuiElement headLine = new GuiElement(this.getWebDriver(), By.xpath("//div[@class='dashboardTextBig']"), mainFrame);

    public TestMethodsPage(WebDriver driver) {
        super(driver);
    }
}
