package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FluentTestPage extends FluentPage<FluentTestPage> {

    @Check
    private IGuiElement input = findById("11");

    @Check
    private IGuiElement button = findByQa("action/submit", input);

    private IGuiElement text = find(By.className("affe"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public FluentTestPage(WebDriver driver) {
        super(driver);
    }

    public IGuiElement input() {
        return input;
    }

    public IGuiElement submit() {
        return button;
    }

    @Override
    protected FluentTestPage self() {
        return this;
    }
}
