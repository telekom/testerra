package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractMethodStateEntry;

/**
 * Created by fakr on 19.09.2017
 */
public class MethodStateChangesEntry extends AbstractMethodStateEntry {

    public MethodStateChangesEntry(TestResultHelper.TestResultChangedMethodState previousState, TestResultHelper.TestResultChangedMethodState actualState, String methodName, String testUnderTestClass) {
        super(previousState, actualState, methodName, testUnderTestClass);
    }
}
