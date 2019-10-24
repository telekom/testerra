package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.WebDriver;

public interface GuiElementFactory {
    GuiElementFacade create(
        final Locate locator,
        final WebDriver webDriver,
        final GuiElementFacade parent
    );
    GuiElementFacade create(
        final Locate locator,
        final WebDriver webDriver
    );
}
