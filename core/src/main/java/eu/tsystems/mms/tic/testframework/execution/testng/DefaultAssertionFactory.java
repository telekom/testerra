package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;

public class DefaultAssertionFactory implements AssertionFactory {
    private ThreadLocal<Class<? extends Assertion>> threadLocalAssertionClass = new ThreadLocal<>();

    private final Injector injector;

    @Inject
    DefaultAssertionFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Class<? extends Assertion> setDefault(Class<? extends Assertion> newClass) {
        Class<? extends Assertion> prevClass = threadLocalAssertionClass.get();
        threadLocalAssertionClass.set(newClass);
        return prevClass;
    }

    @Override
    public Assertion create() {
        Class<? extends Assertion> assertionClass = threadLocalAssertionClass.get();
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
