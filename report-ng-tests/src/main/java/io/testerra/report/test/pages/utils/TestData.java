package io.testerra.report.test.pages.utils;

import eu.tsystems.mms.tic.testframework.report.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestData {

    private String method;
    private String methodClass;

    private Status status;

    private Status[] states;
    private String failureAspect;

    public TestData(String method, String methodClass, Status status, String failureAspect) {
        this.method = method;
        this.methodClass = methodClass;
        this.status = status;
        this.failureAspect = failureAspect;
    }

    public TestData(String method, String methodClass, Status status) {
        this.method = method;
        this.methodClass = methodClass;
        this.status = status;
    }

    public TestData(String failureAspect, Status[] states) {
        this.failureAspect = failureAspect;
        this.states = states;
    }

    public TestData(String method, String failureAspect) {
        this.method = method;
        this.failureAspect = failureAspect;
    }

    public TestData(String method, Status status) {
        this.method = method;
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodClass() {
        return methodClass;
    }

    public Status getStatus1() {
        return status;
    }

    public List<Status> getStates() {
        return Arrays.stream(states).collect(Collectors.toList());
    }

    public String getFailureAspect() {
        return failureAspect;
    }

    @Override
    public String toString() {
        String status1Title;
        if (status == null) {
            status1Title = "null";
        } else {
            status1Title = status.title;
        }
        String statesTitle;
        if (states == null) {
            statesTitle = "null";
        } else {
            statesTitle = Arrays.toString(states);
        }

        return String.format("%s, %s, %s, %s, %s", method, methodClass, status1Title, statesTitle, failureAspect);
    }

}
