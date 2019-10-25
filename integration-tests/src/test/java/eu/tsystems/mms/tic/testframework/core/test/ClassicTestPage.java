package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ClassicTestPage extends Page {

    @Check
    private GuiElement input = new GuiElement(driver, By.id("11"));

    @Check
    private GuiElement button = input.getSubElement(Locate.by().qa("action/submit"));

    private GuiElement text = new GuiElement(driver, By.className("affe"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ClassicTestPage(WebDriver driver) {
        super(driver);
    }

    public GuiElement input() {
        return input;
    }

    public GuiElement submit() {
        return button;
    }
}
