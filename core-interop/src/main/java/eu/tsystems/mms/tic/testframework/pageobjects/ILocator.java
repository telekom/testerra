package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;

public interface ILocator {
    IGuiElement by(final String cssSelector);
    IGuiElement by(final Locate locator);
    IGuiElement by(final By by);
}
