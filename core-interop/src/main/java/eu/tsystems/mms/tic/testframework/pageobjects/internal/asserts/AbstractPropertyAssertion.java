package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract property assertion without any test implementations.
 * Extend this class if you don't need to perform assertions, but compose others.
 * @author Mike Reiche
 */
public abstract class AbstractPropertyAssertion<T> implements PropertyAssertion<T> {

    protected static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);

    protected final AssertionProvider<T> provider;
    protected final AbstractPropertyAssertion parent;

    public AbstractPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        this.parent = (AbstractPropertyAssertion)parentAssertion;
        this.provider = provider;
    }

    public T getActual() {
        return provider.getActual();
    }

    public String traceSubjectString() {
        final List<String> subjects = new ArrayList<>();
        String subject = provider.getSubject();
        if (subject!=null) subjects.add(subject);

        AbstractPropertyAssertion assertion = parent;
        while (assertion != null) {
            subject = assertion.provider.getSubject();
            if (subject!=null) subjects.add(subject);
            assertion = assertion.parent;
        }
        Collections.reverse(subjects);
        return String.join(".", subjects);
    }

    public void failedRecursive() {
        provider.failed(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.failed(this);
            parentAssertion = parentAssertion.parent;
        }
    }

    public void failedFinallyRecursive() {
        provider.failedFinally(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.failedFinally(this);
            parentAssertion = parentAssertion.parent;
        }
    }
}
