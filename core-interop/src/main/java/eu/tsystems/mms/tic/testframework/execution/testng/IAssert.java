package eu.tsystems.mms.tic.testframework.execution.testng;

public interface IAssert {
    void fail(final String message, final Throwable realCause);
    void fail(final String message);
    void assertTrue(final boolean condition, final String message);
    void assertFalse(final boolean condition, final String message);
    void assertSame(final Object actual, final Object expected, final String message);
    void assertNotSame(Object actual, Object expected, String message);
    void assertNull(Object object, String message);
    void assertNotNull(Object object, String message);
}
