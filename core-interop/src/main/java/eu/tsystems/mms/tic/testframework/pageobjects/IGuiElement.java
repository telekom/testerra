package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableBinaryValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableQuantifiedValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;

import java.util.List;

public interface IGuiElement extends
    GuiElementCore,
    Nameable,
    IWebDriverRetainer
{
    IFrameLogic getFrameLogic();
    GuiElementAssert asserts();
    GuiElementAssert asserts(String errorMessage);
    GuiElementWait waits();
    IAssertableValue text();
    IAssertableValue value();
    IAssertableValue value(final Attribute attribute);
    IGuiElement select(final Boolean select);
    IAssertableBinaryValue<Boolean> present();
    IAssertableBinaryValue<Boolean> visible(final boolean complete);
    IAssertableBinaryValue<Boolean> displayed();
    IAssertableBinaryValue<Boolean> enabled();
    IAssertableBinaryValue<Boolean> selected();
    IAssertableQuantifiedValue<Boolean> layout();
    Locate getLocator();

    int getTimeoutInSeconds();
    void setTimeoutInSeconds(int timeoutInSeconds);
    void restoreDefaultTimeout();

    @Deprecated
    List<IGuiElement> getList();
}
