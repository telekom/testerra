package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Move this interface to core-interop when {@link GuiElementData} has an interface
 */
public interface GuiElementCoreFactory {
    GuiElementCore create(
        String browser,
        By by,
        WebDriver webDriver,
        GuiElementData guiElementData
    );
}
