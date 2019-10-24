package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementWaitFactory;

public class TesterraDriverUiModule extends AbstractModule {

    protected void configure() {
        bind(GuiElementAssertFactory.class).to(DefaultGuiElementAssertFactory.class);
        bind(GuiElementWaitFactory.class).to(DefaultGuiElementWaitFactory.class);
        bind(GuiElementFactory.class).to(DefaultGuiElementFactory.class);
    }
}
