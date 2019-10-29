package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;

public class DefaultAssertionFactory implements AssertionFactory {

    private Class<? extends IAssertion> currentAssertionClass;

    private final Injector injector;

    @Inject
    DefaultAssertionFactory(Injector injector) {
        this.injector = injector;
        if (IGuiElement.Properties.DEFAULT_ASSERT_IS_COLLECTOR.asBool()) {
            currentAssertionClass = CollectedAssertion.class;
        } else {
            currentAssertionClass = InstantAssertion.class;
        }
    }

    @Override
    public Class<? extends IAssertion> setDefault(Class<? extends IAssertion> newClass) {
        Class<? extends IAssertion> prevClass = currentAssertionClass;
        currentAssertionClass = newClass;
        return prevClass;
    }

    @Override
    public IAssertion create() {
        return injector.getInstance(currentAssertionClass);
    }
}
