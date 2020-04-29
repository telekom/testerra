package eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractFramePage extends Page {

    //field for GuiElement creation (as a parameter), therefore specifiying other gui elements
    public final GuiElement mainFrame;

    /**
     * Constructor called bei PageFactory
     *
     * @param driver Webdriver to use for this Page
     */
    public AbstractFramePage(WebDriver driver) {
        super(driver);
        mainFrame = new GuiElement(driver, By.cssSelector("frame[name='main']"));
    }
}
