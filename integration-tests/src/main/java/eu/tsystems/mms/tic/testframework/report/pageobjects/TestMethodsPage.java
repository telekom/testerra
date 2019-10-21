package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by jlma on 14.06.2017.
 */
public class TestMethodsPage extends AbstractReportPage {

    @Check
    private IGuiElement headLine = new GuiElement(this.driver, By.xpath("//div[@class='dashboardTextBig']"), mainFrame);

    public TestMethodsPage(WebDriver driver) {
        super(driver);
    }
}
