package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;

public interface IPage extends IWebDriverRetainer {
    IGuiElement by(final String cssSelector);
    IGuiElement by(final Locate locator);
    IGuiElement by(final By by);
    IAssertableValue title();
    IAssertableValue url();
    IPage navigate(final String to);
}
