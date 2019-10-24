package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.testng.Assert;

public class AssertableBinaryValue<T, E> implements IAssertableBinaryValue<T, E> {

    protected final T actual;
    protected final String property;
    protected final E subject;
    protected boolean nonFunctional=false;

    public AssertableBinaryValue(final T actual, String propertyName, final E subject) {
        this.actual = actual;
        this.property = propertyName;
        this.subject = subject;
    }

    public T actual() {
        return actual;
    }

    @Override
    public E isTrue() {
        return isTrue(null);
    }

    @Override
    public E isFalse() {
        return isFalse(null);
    }

    @Override
    public E isTrue(String errorMessage) {
        final String actualString = (String)actual;
        if (!(
            !(boolean) actual
            || actualString.equalsIgnoreCase("true")
            || actualString.equalsIgnoreCase("on")
            || actualString.equalsIgnoreCase("1")
        )) {
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = String.format("[%s] is true", actual);
            }
            Assert.fail(String.format("%s %s %s", subject, property, errorMessage));
        }
        return subject;
    }

    @Override
    public E isFalse(String errorMessage) {
        final String actualString = (String)actual;
        if (!(
            (boolean) actual
            || actualString.equalsIgnoreCase("false")
            || actualString.equalsIgnoreCase("off")
            || actualString.equalsIgnoreCase("0")
        )) {
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = String.format("[%s] is false", actual);
            }
            Assert.fail(String.format("%s %s %s", subject, property, errorMessage));
        }
        return subject;
    }

    @Override
    public INonFunctionalAssertableValue nonFunctional() {
        nonFunctional = true;
        return this;
    }
}
