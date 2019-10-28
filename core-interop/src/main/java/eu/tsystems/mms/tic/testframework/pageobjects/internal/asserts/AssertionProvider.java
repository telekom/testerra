package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AssertionProvider<T> implements IActualProperty<T> {
    protected final AbstractPropertyAssertion parent;

    public AssertionProvider() {
        this.parent = null;
    }
    public AssertionProvider(AbstractPropertyAssertion parentAssertion) {
        this.parent = parentAssertion;
    }

    @Override
    abstract public T actual();

    abstract public Object subject();

    public String traceSubjectString() {
        final List<String> subjects = new ArrayList<>();
        Object subject = subject();
        if (subject!=null) subjects.add(subject.toString());

        AbstractPropertyAssertion assertion = parent;
        while (assertion != null) {
            subject = assertion.provider.subject();
            if (subject!=null) subjects.add(subject.toString());
            assertion = assertion.provider.parent;
        }
        Collections.reverse(subjects);
        return String.join(".", subjects);
    }

    public void failedRecursive() {
        failed();
        AbstractPropertyAssertion assertion = parent;
        while (assertion != null) {
            assertion.provider.failed();
            assertion = assertion.provider.parent;
        }
    }

    public void failed() {
    }
}
