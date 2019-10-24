package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;

public class AssertableValue<T,E> extends AssertableQuantifiedValue<T, E> implements IAssertableValue<T, E> {

    public AssertableValue (final T actual, String propertyName, final E subject) {
        super(actual, propertyName, subject);
    }

    @Override
    public E contains(final String expected) {
        AssertUtils.assertContains((String)actual, expected, String.format("%s %s", subject, property));
        return subject;
    }

    @Override
    public E containsNot(final String expected) {
        AssertUtils.assertContainsNot((String)actual, expected, String.format("%s %s", subject, property));
        return subject;
    }

    @Override
    public E beginsWith(String expected) {
        final String actualString = (String)actual;
        if (!actualString.startsWith(expected)) {
            Assert.fail(String.format("%s %s [%s] begins with [%s]", subject, property, actual, expected));
        }
        return subject;
    }

    @Override
    public E endsWith(String expected) {
        final String actualString = (String)actual;
        if (!actualString.endsWith(expected)) {
            Assert.fail(String.format("%s %s [%s] ends with [%s]", subject, property, actual, expected));
        }
        return subject;
    }
}
