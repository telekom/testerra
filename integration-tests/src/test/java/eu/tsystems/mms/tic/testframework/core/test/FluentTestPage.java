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
    private IGuiElement button = forAncestor(input).findOneByQa("action/submit");
    //private IGuiElement section = withAncestor(button)

    private IGuiElement text = findOne(By.className("affe"));

    private IGuiElement frame = findOne(By.tagName("iframe"));

    private IGuiElement btnInFrame = findOne(By.tagName("iframe"));
    private IGuiElement newDocRoot = inFrame(frame).findOne(By.tagName("body"));
    private IGuiElement btnInFrame3 = forAncestor(newDocRoot).findOne(By.tagName("button"));

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

    @Override
    protected FluentTestPage self() {
        return this;
    }

    public FluentTestPage navigateToSomeWhere() {
        return createPage(FluentTestPage.class);
    }
}
