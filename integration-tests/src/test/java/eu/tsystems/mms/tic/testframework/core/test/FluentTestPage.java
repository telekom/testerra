package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.core.test.components.InputForm;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FluentTestPage extends FluentPage<FluentTestPage> {

    @Check
    private IGuiElement textElement = findOneById("11");

    @Check
    private InputForm inputForm = withAncestor(findOne(By.className("className"))).createComponent(InputForm.class);

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public FluentTestPage(WebDriver driver) {
        super(driver);
    }

    public IGuiElement textElement() {
        return textElement;
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

    public InputForm inputForm() {
        return inputForm;
    }

    @Override
    protected FluentTestPage self() {
        return this;
    }

    public FluentTestPage navigateToSomeWhere() {
        return createPage(FluentTestPage.class);
    }
}
