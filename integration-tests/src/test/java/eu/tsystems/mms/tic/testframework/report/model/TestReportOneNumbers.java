package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractTestReportNumbers;

public class TestReportOneNumbers extends AbstractTestReportNumbers {

    public TestReportOneNumbers() {
        highCorridorLimit = 34;
        highCorridorActual = 34;
        midCorridorActual = 3;
        lowCorridorActual = 5;
        all = 105;
        allSuccessful = 39;
        passed = 22;
        passedMinor = 13;
        passedRetry = 3;
        allSkipped = 22;
        skipped = 22;
        allBroken = 44;
        failed = 26;
        failedMinor = 18;
        failedRetried = 5;
        failedExpected = 3;
        failureAspects = 11;
        exitPoints = 42;
        percentage = 37;
    }

}
