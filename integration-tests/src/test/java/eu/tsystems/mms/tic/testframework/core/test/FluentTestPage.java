package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.core.test.components.HeaderComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FluentTestPage extends FluentPage<FluentTestPage> {

    @Check
    private IGuiElement input = findOneById("11");

    @Check
    private IGuiElement button = withAncestor(input).findOneByQa("action/submit");

    private IGuiElement text = findOne(By.className("affe"));

    private IGuiElement frame = findOne(By.tagName("iframe"));

    private IGuiElement btnInFrame = findOne(By.tagName("iframe"));
    private IGuiElement newDocRoot = inFrame(frame).findOne(By.tagName("body"));
    private IGuiElement btnInFrame3 = withAncestor(newDocRoot).findOne(By.tagName("button"));

    private HeaderComponent header = createComponent(HeaderComponent.class, findOne(By.id("header")));

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

    public IGuiElement notDisplayedElement() {
        return findOneById("notDisplayedElement");
    }

    public IGuiElement notVisibleElement() {
        return findOneById("notVisibleElement");
    }

    public IGuiElement nonExistentElement() {
        return findOneById("schnullifacks");
    }

    public HeaderComponent header() {
        return header;
    }

    @Override
    protected FluentTestPage self() {
        return this;
    }

    public FluentTestPage navigateToSomeWhere() {
        return createPage(FluentTestPage.class);
    }
}
