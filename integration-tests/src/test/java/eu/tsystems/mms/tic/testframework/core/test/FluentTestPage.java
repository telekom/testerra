package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import org.openqa.selenium.WebDriver;

public class FluentTestPage extends FluentPage<FluentTestPage> {

    @Check
    private GuiElementFacade visibleElement = findById("11");

    @Check
    private GuiElementFacade subElement = findByQa("action/submit", visibleElement);

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public FluentTestPage(WebDriver driver) {
        super(driver);
    }

    public GuiElementFacade input() {
        return visibleElement;
    }

    public GuiElementFacade submit() {
        return subElement;
    }

    @Override
    protected FluentTestPage self() {
        return this;
    }
}
