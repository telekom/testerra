package io.testerra.report.test.pages.utils;

public enum FailureAspectType {
    MAJOR("Major"), MINOR("Minor");

    private final String value;

    FailureAspectType(final String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
