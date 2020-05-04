package eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.annotations.PageOptions;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@PageOptions(elementTimeoutInSeconds = 3)
public class PageWithPageOptions extends Page {

    @Check
    public GuiElement existingElement = new GuiElement(this.getWebDriver(), By.id("1"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithPageOptions(WebDriver driver) {
        super(driver);
    }
}
