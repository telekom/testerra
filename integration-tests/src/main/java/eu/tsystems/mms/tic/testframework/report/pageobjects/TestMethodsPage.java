package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by jlma on 14.06.2017.
 */
public class TestMethodsPage extends AbstractReportPage {

    @Check
    private GuiElement headLine = new GuiElement(this.getWebDriver(), By.xpath("//div[@class='dashboardTextBig']"), mainFrame);

    public TestMethodsPage(WebDriver driver) {
        super(driver);
    }
}
