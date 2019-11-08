package eu.tsystems.mms.tic.testframework.execution.testng;

public interface AssertionFactory {
    /**
     * Sets a new default assertion class
     * @return Returns the previously configured assertion class
     */
    Class<? extends Assertion> setDefault(Class<? extends Assertion> newClass);
    Assertion create();
}
