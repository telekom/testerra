package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import org.openqa.selenium.WebDriver;

public interface IGuiElement extends GuiElementCore, Nameable {
    WebDriver getWebDriver();
    IFrameLogic getFrameLogic();
    GuiElementAssert asserts();
    GuiElementWait waits();
}
