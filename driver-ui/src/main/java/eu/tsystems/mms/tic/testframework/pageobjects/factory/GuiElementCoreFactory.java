package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public interface GuiElementCoreFactory {
    GuiElementCore create(
        String browser,
        By by,
        WebDriver webDriver,
        GuiElementData guiElementData
    );
}
