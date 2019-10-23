package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.testng.Assert;

public class AssertableBinaryValue<T> implements IAssertableBinaryValue {

    protected final T actual;
    protected final String property;
    protected final Object object;

    public AssertableBinaryValue(final T actual, String propertyName, Object object) {
        this.actual = actual;
        this.property = propertyName;
        this.object = object;
    }

    public T actual() {
        return actual;
    }

    @Override
    public IAssertableBinaryValue isTrue() {
        return isTrue(null);
    }

    @Override
    public IAssertableBinaryValue isFalse() {
        return isFalse(null);
    }

    @Override
    public IAssertableBinaryValue isTrue(String errorMessage) {
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
            Assert.fail(String.format("%s %s %s", object, property, errorMessage));
        }
        return this;
    }

    @Override
    public IAssertableBinaryValue isFalse(String errorMessage) {
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
            Assert.fail(String.format("%s %s %s", object, property, errorMessage));
        }
        return this;
    }
}
