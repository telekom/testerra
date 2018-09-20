package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;

/**
 * Created by fakr on 19.09.2017
 */
public abstract class AbstractMethodStateEntry {

    private TestResultHelper.TestResultChangedMethodState previousState;
    private TestResultHelper.TestResultChangedMethodState actualState;
    private String methodName;
    private String testUnderTestClassName;

    /**
     * Constructor for an abstract method state entry
     *
     * @param previousState previous test result
     * @param actualState actual test result
     * @param methodName name of the concerning method
     * @param testUnderTestClassName name of the concerning class
     */
    public AbstractMethodStateEntry(TestResultHelper.TestResultChangedMethodState previousState, TestResultHelper.TestResultChangedMethodState actualState, String methodName, String testUnderTestClassName) {
        this.previousState = previousState;
        this.actualState = actualState;
        this.methodName = methodName;
        this.testUnderTestClassName = testUnderTestClassName;
    }

    public TestResultHelper.TestResultChangedMethodState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(TestResultHelper.TestResultChangedMethodState previousState) {
        this.previousState = previousState;
    }

    public TestResultHelper.TestResultChangedMethodState getActualState() {
        return actualState;
    }

    public void setActualState(TestResultHelper.TestResultChangedMethodState actualState) {
        this.actualState = actualState;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTestUnderTestClassName() {
        return testUnderTestClassName;
    }

    public void setTestUnderTestClassName(String testUnderTestClassName) {
        this.testUnderTestClassName = testUnderTestClassName;
    }

}
