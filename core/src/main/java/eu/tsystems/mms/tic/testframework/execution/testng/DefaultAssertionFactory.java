package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;

public class DefaultAssertionFactory implements AssertionFactory {
    private ThreadLocal<Class<? extends IAssertion>> threadLocalAssertionClass = new ThreadLocal<>();

    private final Injector injector;

    @Inject
    DefaultAssertionFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Class<? extends IAssertion> setDefault(Class<? extends IAssertion> newClass) {
        Class<? extends IAssertion> prevClass = threadLocalAssertionClass.get();
        threadLocalAssertionClass.set(newClass);
        return prevClass;
    }

    @Override
    public IAssertion create() {
        Class<? extends IAssertion> assertionClass = threadLocalAssertionClass.get();
        if (assertionClass==null) {
            if (IGuiElement.Properties.DEFAULT_ASSERT_IS_COLLECTOR.asBool()) {
                setDefault(CollectedAssertion.class);
            } else {
                setDefault(InstantAssertion.class);
            }
        }
        return injector.getInstance(threadLocalAssertionClass.get());
    }
}
