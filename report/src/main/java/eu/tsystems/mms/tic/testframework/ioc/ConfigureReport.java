package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.hook.ReportHook;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;

public class ConfigureReport extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<ModuleHook> hookBinder = Multibinder.newSetBinder(binder(), ModuleHook.class);
        hookBinder.addBinding().to(ReportHook.class).in(Scopes.SINGLETON);
    }
}
