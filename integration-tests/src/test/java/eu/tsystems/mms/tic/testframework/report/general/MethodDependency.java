package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;

public enum MethodDependency {

    FAILED_ALWAYS("test_FailedAlways", TestResultHelper.TestResult.FAILED, ""),
    FAILED_DEPENDSON_PASSED("test_FailedDependsOnPassedMethod", TestResultHelper.TestResult.FAILED, ""),
    FAILED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN2("test_dependsOnFailedMethodButAlwaysRunWithDP", TestResultHelper.TestResult.FAILED, "(1) Run2"),
    FAILED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN3("test_dependsOnFailedMethodButAlwaysRunWithDP", TestResultHelper.TestResult.FAILED, "(1) Run3"),
    PASSED_ALWAYS("test_PassedAlways", TestResultHelper.TestResult.PASSED, ""),
    PASSED_DEPENDSON_FAILED_BUT_ALWAYS_RUN("test_PassedDependsOnFailedMethodButAlwaysRun", TestResultHelper.TestResult.PASSED, ""),
    PASSED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN1("test_dependsOnFailedMethodButAlwaysRunWithDP", TestResultHelper.TestResult.PASSED, "(1) Run1"),
    SKIPPED_DEPENDSON_FAILED_WITH_DATAPROVIDER("test_dependsOnFailedMethodWithDP", TestResultHelper.TestResult.SKIPPED, ""),
    SKIPPED_DEPENDSON_FAILED("test_SkippedDependsOnFailedMethod", TestResultHelper.TestResult.SKIPPED, ""),
    SKIPPED_DEPENDSON_FAILED_WITH_SECOND_DATAPROVIDER("test_dependsOnFailedMethodWithDP2", TestResultHelper.TestResult.SKIPPED, "");

    private String methodName;
    private TestResultHelper.TestResult testResult;
    private String tagName;

    MethodDependency(String methodName, TestResultHelper.TestResult testResult, String tagName) {
        this.methodName = methodName;
        this.testResult = testResult;
        this.tagName = tagName;
    }

    public String getMethodName() {
        return methodName;
    }

    public TestResultHelper.TestResult getTestResult() {
        return testResult;
    }

    public String getTagName() {
        return tagName;
    }


}
