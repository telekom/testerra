package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This class represents the Page Object for the expandable menu in the right upper corner of a TesterraReportPage
 */
public class BurgerMenu extends AbstractReportPage {

    @Check
    private IGuiElement exitPointsLink = new GuiElement(this.driver, By.id("ExitPoints"), mainFrame);
    private IGuiElement logsLink = new GuiElement(this.driver, By.id("Logs"), mainFrame);
    private IGuiElement timingsLink = new GuiElement(this.driver, By.id("Timings"), mainFrame);
    private IGuiElement memoryLink = new GuiElement(this.driver, By.id("Memory"), mainFrame);
    private IGuiElement exportLink = new GuiElement(this.driver, By.id("Export"), mainFrame);
    private IGuiElement metricsLink = new GuiElement(this.driver, By.id("Metrics"), mainFrame);

    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public BurgerMenu(WebDriver driver) {
        super(driver);
    }

    /**
     * Method to navigate to the ExitPointsPage
     *
     * @return
     */
    public ExitPointsPage openExitPointsPage() {
        exitPointsLink = exitPointsLink.getSubElement(By.xpath("./a"));
        exitPointsLink.click();
        return PageFactory.create(ExitPointsPage.class, this.driver);
    }

}
