package io.testerra.report.test.pages.utils;

public enum LogLevel {
    ERROR("ERROR"),
    WARN("WARN"),
    INFO("INFO");

    private final String title;

    LogLevel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
