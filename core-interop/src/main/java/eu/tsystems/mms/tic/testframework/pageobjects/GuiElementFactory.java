package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.WebDriver;

public interface GuiElementFactory {
    IGuiElement create(
        final Locate locator,
        final WebDriver webDriver,
        final IGuiElement parent
    );
    IGuiElement create(
        final Locate locator,
        final WebDriver webDriver
    );
}
