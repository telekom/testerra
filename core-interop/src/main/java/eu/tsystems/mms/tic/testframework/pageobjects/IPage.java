package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;

public interface IPage extends IWebDriverRetainer {
    GuiElementFacade byId(final String id);
    GuiElementFacade byQa(final String qa);
    GuiElementFacade by(final Locate locator);
    GuiElementFacade by(final By by);
    IAssertableValue title();
    IAssertableValue url();
    IPage navigate(final String to);
}
