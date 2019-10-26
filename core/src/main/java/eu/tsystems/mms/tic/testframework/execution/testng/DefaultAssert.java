package eu.tsystems.mms.tic.testframework.execution.testng;

import static org.testng.internal.EclipseInterface.ASSERT_LEFT;
import static org.testng.internal.EclipseInterface.ASSERT_LEFT2;
import static org.testng.internal.EclipseInterface.ASSERT_MIDDLE;
import static org.testng.internal.EclipseInterface.ASSERT_RIGHT;

public class DefaultAssert implements IAssert {
    public void failSame(Object actual, Object expected, String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + ASSERT_LEFT2 + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT);
    }

    public void failNotSame(Object actual, Object expected, String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT);
    }

    public void failNotEquals(Object actual, Object expected, String message) {
        fail(format(actual, expected, message));
    }

    protected String format(Object actual, Object expected, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        return formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT;
    }

    @Override
    public void fail(String message, Throwable realCause) {

    }

    @Override
    public void fail(String message) {
        fail(message, null);
    }

    @Override
    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            failNotEquals(condition, Boolean.TRUE, message);
        }
    }

    @Override
    public void assertFalse(boolean condition, String message) {
        if (condition) {
            failNotEquals(condition, Boolean.FALSE, message); // TESTNG-81
        }
    }
    @Override
    public void assertSame(Object actual, Object expected, String message) {
        if (expected == actual) {
            return;
        }
        failNotSame(actual, expected, message);
    }
    @Override
    public void assertNotSame(Object actual, Object expected, String message) {
        if (expected == actual) {
            failSame(actual, expected, message);
        }
    }

    @Override
    public void assertNull(Object object, String message) {
        if (object != null) {
            failNotSame(object, null, message);
        }
    }

    @Override
    public void assertNotNull(Object object, String message) {
        if (object == null) {
            String formatted = "";
            if (message != null) {
                formatted = message + " ";
            }
            fail(formatted + "expected object to not be null");
        }
    }
}
