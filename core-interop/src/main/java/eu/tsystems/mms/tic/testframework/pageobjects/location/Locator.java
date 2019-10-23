package eu.tsystems.mms.tic.testframework.pageobjects.location;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import org.openqa.selenium.By;

public interface Locator {
    GuiElementFacade findById(final String id);
    GuiElementFacade findByQa(final String qa);
    GuiElementFacade find(final Locate locator);
    GuiElementFacade find(final By by);
}
