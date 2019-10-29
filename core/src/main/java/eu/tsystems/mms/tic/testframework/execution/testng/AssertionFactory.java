package eu.tsystems.mms.tic.testframework.execution.testng;

public interface AssertionFactory {
    /**
     * Sets a new default assertion class
     * @return Returns the previously configured assertion class
     */
    Class<? extends IAssertion> setDefault(Class<? extends IAssertion> newClass);
    IAssertion create();
}
