package io.testerra.report.test.pages.utils;

import eu.tsystems.mms.tic.testframework.report.Status;

public class TestData {

    private String method;
    private String methodClass;
    private Status status1;
    private Status status2;
    private String failureAspect;

    public TestData(String method, String methodClass, Status status, String failureAspect) {
        this.method = method;
        this.methodClass = methodClass;
        this.status1 = status;
        this.failureAspect = failureAspect;
    }

    public TestData(String method, String methodClass, Status status) {
        this.method = method;
        this.methodClass = methodClass;
        this.status1 = status;
    }

    public TestData(String failureAspect, Status status1, Status status2) {
        this.failureAspect = failureAspect;
        this.status1 = status1;
        this.status2 = status2;
    }

    public TestData(String method, String failureAspect) {
        this.method = method;
        this.failureAspect = failureAspect;
    }

    public TestData(String method, Status status) {
        this.method = method;
        this.status1 = status;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodClass() {
        return methodClass;
    }

    public Status getStatus1() {
        return status1;
    }

    public Status getStatus2() {
        return status2;
    }

    public String getFailureAspect() {
        return failureAspect;
    }

    @Override
    public String toString(){
        String status1Title;
        if (status1 == null){
            status1Title = "null";
        } else {
            status1Title = status1.title;
        }
        String status2Title;
        if (status2 == null){
            status2Title = "null";
        } else {
            status2Title = status2.title;
        }

        return String.format("%s, %s, %s, %s, %s", method, methodClass, status1Title, status2Title, failureAspect);
    }

}
