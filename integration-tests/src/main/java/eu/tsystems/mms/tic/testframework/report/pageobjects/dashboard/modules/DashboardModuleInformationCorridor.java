package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleInformationCorridor extends AbstractFramePage {


    public final GuiElement repairedFailsIndicationButton = new GuiElement(this.driver, By.xpath("//div[@class='dashboardInfo']"), mainFrame);

    public DashboardModuleInformationCorridor(WebDriver driver) {
        super(driver);
    }
}
