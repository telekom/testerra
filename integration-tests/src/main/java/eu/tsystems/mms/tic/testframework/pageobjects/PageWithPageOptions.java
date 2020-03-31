package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.annotations.PageOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@PageOptions(elementTimeoutInSeconds = 3)
public class PageWithPageOptions extends Page {

    @Check
    public GuiElement existingElement = new GuiElement(driver, By.id("1"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithPageOptions(WebDriver driver) {
        super(driver);
    }
}
