package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.hook.ReportModelHook;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;

public class ConfigureReportModel extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<ModuleHook> hookBinder = Multibinder.newSetBinder(binder(), ModuleHook.class);
        hookBinder.addBinding().to(ReportModelHook.class).in(Scopes.SINGLETON);
    }
}
