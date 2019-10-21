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
        final String actualString = (String)actual;
        if (!(
            !(boolean) actual
            || actualString.equalsIgnoreCase("true")
            || actualString.equalsIgnoreCase("on")
            || actualString.equalsIgnoreCase("1")
            )) {
            Assert.fail(String.format("%s %s [%s] is true", object, property, actual));
        }
        return this;
    }

    @Override
    public IAssertableBinaryValue isFalse() {
        final String actualString = (String)actual;
        if (!(
            (boolean) actual
            || actualString.equalsIgnoreCase("false")
            || actualString.equalsIgnoreCase("off")
            || actualString.equalsIgnoreCase("0")
        )) {
            Assert.fail(String.format("%s %s [%s] is false", object, property, actual));
        }
        return this;
    }
}
