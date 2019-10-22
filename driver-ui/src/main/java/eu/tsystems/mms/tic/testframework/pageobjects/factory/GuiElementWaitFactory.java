package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import com.google.inject.Singleton;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementStatusCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;

@Singleton
public class GuiElementWaitFactory implements IGuiElementWaitFactory {
    @Override
    public GuiElementWait create(GuiElementStatusCheck guiElementStatusCheck, GuiElementData guiElementData) {
        return null;
    }
}
