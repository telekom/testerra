package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FluentTestPage extends FluentPage<FluentTestPage> {

    @Check
    private IGuiElement input = findOneById("11");

    @Check
    private IGuiElement button = findOneByQa("action/submit", input);

    private IGuiElement text = findOne(By.className("affe"));

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

    public FluentTestPage navigateToSomeWhere() {
        return pageFactory.create(FluentTestPage.class);
    }
}
