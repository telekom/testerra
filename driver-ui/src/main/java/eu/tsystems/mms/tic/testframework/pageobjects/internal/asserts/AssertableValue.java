package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;

public class AssertableValue<T> extends AssertableQuantifiedValue implements IAssertableValue {

    public AssertableValue (final T actual, String propertyName, Object object) {
        super(actual, propertyName, object);
    }

    @Override
    public AssertableValue contains(final String expected) {
        AssertUtils.assertContains((String)actual, expected);
        return this;
    }

    @Override
    public AssertableValue containsNot(final String expected) {
        AssertUtils.assertContainsNot((String)actual, expected);
        return this;
    }

    @Override
    public AssertableValue beginsWith(String expected) {
        final String actualString = (String)actual;
        if (!actualString.startsWith(expected)) {
            Assert.fail(String.format("%s %s [%s] begins with [%s]", object, property, actual, expected));
        }
        return this;
    }

    @Override
    public AssertableValue endsWith(String expected) {
        final String actualString = (String)actual;
        if (!actualString.endsWith(expected)) {
            Assert.fail(String.format("%s %s [%s] ends with [%s]", object, property, actual, expected));
        }
        return this;
    }
}
