package eu.tsystems.mms.tic.testframework.report.abstracts;


import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjects.BurgerMenu;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ExitPointsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.FailureAspectsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by jlma on 26.10.2016.
 */
public abstract class AbstractReportPage extends AbstractFramePage {

    @Check
    private IGuiElement burgerMenu = new GuiElement(this.driver, By.id("menuToggle"), mainFrame);

    private IGuiElement dashBoardLink = new GuiElement(this.driver, By.linkText("DASHBOARD"), mainFrame);
    private IGuiElement classesLink = new GuiElement(this.driver, By.partialLinkText("CLASSES"), mainFrame);
    private IGuiElement failureAspectsLink = new GuiElement(this.driver, By.partialLinkText("FAILURE ASPECTS"), mainFrame);
    private IGuiElement threadsLink = new GuiElement(this.driver, By.partialLinkText("THREADS"), mainFrame);
    private IGuiElement stateChangesLink = new GuiElement(this.driver, By.partialLinkText("STATE CHANGES"), mainFrame);

    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public AbstractReportPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage goToDashboard() throws InterruptedException {
        this.dashBoardLink.click();
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    public ClassesPage goToClasses() {
        this.classesLink.click();
        return PageFactory.create(ClassesPage.class, this.driver);
    }

    public ExitPointsPage goToExitPoints() {
        return this.openBurgerMenu().openExitPointsPage();
    }

    public FailureAspectsPage goToFailureAspects() {
        this.failureAspectsLink.click();
        return PageFactory.create(FailureAspectsPage.class, this.driver);
    }

    public BurgerMenu openBurgerMenu() {
        IGuiElement burgerMenuButton = burgerMenu.getSubElement(By.xpath(".//input"));
        burgerMenuButton.setName("burgerMenuButton");
        burgerMenuButton.click();
        return PageFactory.create(BurgerMenu.class, this.driver);
    }
}
