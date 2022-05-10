package io.testerra.report.test.helper;

public enum TestState {

    Failed("Failed"),
    ExpectedFailed("Expected Failed"),
    Skipped("Skipped"),
    Passed("Passed");

    private final String stateName;

    TestState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getStateNameWithReplacement(){
        return stateName.replace(" ", "x");
    }

}
