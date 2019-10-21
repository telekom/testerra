package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStackPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStepsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by riwa on 04.01.2017.
 */
public abstract class AbstractMethodDetailsPage extends AbstractReportPage {

    protected IGuiElement mainFrame = new GuiElement(driver, By.cssSelector("frame[name='main']"));
    /**
     * Buttons on Top
     */
    protected IGuiElement backButton = new GuiElement(this.driver, By.xpath("//div[@class='detailsmenu']"), mainFrame);
    protected IGuiElement detailsButton = new GuiElement(this.driver, By.id("buttondetails"), mainFrame);
    protected IGuiElement stepsButton = new GuiElement(this.driver, By.id("buttondlogs"), mainFrame);
    protected IGuiElement stackButton = new GuiElement(this.driver, By.id("buttondstack"), mainFrame);
    protected IGuiElement minorErrorButton = new GuiElement(this.driver, By.id("buttonminor"), mainFrame);

    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public AbstractMethodDetailsPage(WebDriver driver) {
        super(driver);
        driver.switchTo().defaultContent();
        driver.switchTo().frame(0);
    }

    /**
     * click Buttons on Top
     *
     * @return DashboardPage
     */
    public DashboardPage gotoDashboardPageByClickingBackButton() {
        backButton.click();
        return PageFactory.create(DashboardPage.class, driver);
    }

    public MethodDetailsPage gotoMethodDetailsPage() {
        detailsButton.click();
        return PageFactory.create(MethodDetailsPage.class, driver);
    }

    public MethodStepsPage gotoMethodStepsPage() {
        stepsButton.click();
        return PageFactory.create(MethodStepsPage.class, driver);
    }

    public MethodStackPage gotoMethodStackPage() {
        stackButton.click();
        return PageFactory.create(MethodStackPage.class, driver);
    }
}
