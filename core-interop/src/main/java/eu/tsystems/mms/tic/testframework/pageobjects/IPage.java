package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;

public interface IPage extends IWebDriverRetainer {
    IGuiElement byId(final String id);
    IGuiElement byQa(final String qa);
    IGuiElement by(final Locate locator);
    IGuiElement by(final By by);
    IAssertableValue title();
    IAssertableValue url();
    IPage navigate(final String to);
}
