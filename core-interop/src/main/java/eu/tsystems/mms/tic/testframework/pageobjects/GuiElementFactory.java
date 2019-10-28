package eu.tsystems.mms.tic.testframework.pageobjects;

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
