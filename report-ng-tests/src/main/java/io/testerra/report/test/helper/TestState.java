package io.testerra.report.test.helper;

public enum TestState {

    Failed("Failed"),
    ExpectedFailed("ExpectedxFailed"),
    Skipped("Skipped"),
    Passed("Passed");

    private final String stateName;

    TestState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

}
