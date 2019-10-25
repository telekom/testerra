package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FluentTestPage extends FluentPage<FluentTestPage> {

    @Check
    private GuiElementFacade input = findById("11");

    @Check
    private GuiElementFacade button = findByQa("action/submit", input);

    private GuiElementFacade text = find(By.className("affe"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public FluentTestPage(WebDriver driver) {
        super(driver);
    }

    public GuiElementFacade input() {
        return input;
    }

    public GuiElementFacade submit() {
        return button;
    }

    @Override
    protected FluentTestPage self() {
        return this;
    }
}
