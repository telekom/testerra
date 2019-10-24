package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import org.openqa.selenium.WebDriver;

public class TestPageObject extends Page {

    @Check
    private GuiElementFacade visibleElement = findById("11");

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public TestPageObject(WebDriver driver) {
        super(driver);
    }

    public GuiElementFacade visibleElement() {
        return this.visibleElement;
    }
}
