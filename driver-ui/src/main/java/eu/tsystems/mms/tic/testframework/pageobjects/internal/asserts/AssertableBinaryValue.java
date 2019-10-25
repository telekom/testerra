package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.testng.Assert;

public class AssertableBinaryValue<T, E> implements IAssertableBinaryValue<T, E> {

    protected final T actual;
    protected final String property;
    protected final E subject;
    protected boolean nonFunctional=false;

    public AssertableBinaryValue(final E subject, final T actual, final String propertyName) {
        this.actual = actual;
        this.property = propertyName;
        this.subject = subject;
    }

    public T actual() {
        return actual;
    }

    @Override
    public E isTrue() {
        final String actualString = (String)actual;
        if (!(
            !(boolean) actual
            || actualString.equalsIgnoreCase("true")
            || actualString.equalsIgnoreCase("on")
            || actualString.equalsIgnoreCase("1")
        )) {
            Assert.fail(String.format("%s.%s [%s] is true", subject, property, actual));
        }
        return subject;
    }

    @Override
    public E isFalse() {
        final String actualString = (String)actual;
        if (!(
            (boolean) actual
            || actualString.equalsIgnoreCase("false")
            || actualString.equalsIgnoreCase("off")
            || actualString.equalsIgnoreCase("0")
        )) {
            Assert.fail(String.format("%s.%s [%s] is false", subject, property, actual));
        }
        return subject;
    }

    @Override
    public INonFunctionalAssertableValue nonFunctional() {
        nonFunctional = true;
        return this;
    }
}
