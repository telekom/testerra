package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.annotations.PageOptions;
import eu.tsystems.mms.tic.testframework.core.test.components.InputForm;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TestableGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@PageOptions(elementTimeoutInSeconds = 1)
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

    public TestableGuiElement textElement() {
        return textElement;
    }

    public TestableGuiElement notDisplayedElement() {
        return findOneById("notDisplayedElement");
    }

    public TestableGuiElement notVisibleElement() {
        return findOneById("notVisibleElement");
    }

    public TestableGuiElement nonExistentElement() {
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
