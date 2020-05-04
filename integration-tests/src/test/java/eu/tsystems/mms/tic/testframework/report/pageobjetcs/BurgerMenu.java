package eu.tsystems.mms.tic.testframework.report.pageobjetcs;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This class represents the Page Object for the expandable menu in the right upper corner of a TesterraReportPage
 */
public class BurgerMenu extends AbstractReportPage {

    @Check
    private GuiElement exitPointsLink = new GuiElement(this.getWebDriver(), By.id("ExitPoints"), mainFrame);
    private GuiElement logsLink = new GuiElement(this.getWebDriver(), By.id("Logs"), mainFrame);
    private GuiElement timingsLink = new GuiElement(this.getWebDriver(), By.id("Timings"), mainFrame);
    private GuiElement memoryLink = new GuiElement(this.getWebDriver(), By.id("Memory"), mainFrame);
    private GuiElement exportLink = new GuiElement(this.getWebDriver(), By.id("Export"), mainFrame);
    private GuiElement metricsLink = new GuiElement(this.getWebDriver(), By.id("Metrics"), mainFrame);

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
        return PageFactory.create(ExitPointsPage.class, this.getWebDriver());
    }

}
