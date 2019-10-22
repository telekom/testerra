package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.IGuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.StandardGuiElementWait;

public class TesterraDriverUiModule extends AbstractModule {

    protected void configure() {
        bind(IGuiElement.class).to(GuiElement.class);
        //bind(GuiElementWait.class).to(StandardGuiElementWait.class);
        install(
            new FactoryModuleBuilder()
                .implement(GuiElementWait.class, StandardGuiElementWait.class)
                .build(IGuiElementWaitFactory.class)
        );
    }
}
