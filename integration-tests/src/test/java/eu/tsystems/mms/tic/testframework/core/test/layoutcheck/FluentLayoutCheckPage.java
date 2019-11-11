package eu.tsystems.mms.tic.testframework.core.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.WebDriver;

public class FluentLayoutCheckPage extends FluentPage<FluentLayoutCheckPage> {
    public FluentLayoutCheckPage(WebDriver driver) {
        super(driver);
    }
    @Override
    protected FluentLayoutCheckPage self() {
        return this;
    }
    public IGuiElement getGuiElementQa(String qa) {
        return findByQa(qa);
    }
}
