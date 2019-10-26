package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AssertionProvider<T> implements IAssertionProvider<T> {
    protected final AbstractAssertion parent;

    public AssertionProvider() {
        this.parent = null;
    }
    public AssertionProvider(AbstractAssertion parentAssertion) {
        this.parent = parentAssertion;
    }

    @Override
    abstract public T actual();

    @Override
    abstract public Object subject();

    public String traceSubjectString() {
        final List<String> subjects = new ArrayList<>();
        Object subject = subject();
        if (subject!=null) subjects.add(subject.toString());

        AbstractAssertion assertion = parent;
        while (assertion != null) {
            subject = assertion.provider.subject();
            if (subject!=null) subjects.add(subject.toString());
            assertion = assertion.provider.parent;
        }
        Collections.reverse(subjects);
        return String.join(".", subjects);
    }

    @Override
    public void failed() {
    }

    @Override
    public void passed() {
    }
}
