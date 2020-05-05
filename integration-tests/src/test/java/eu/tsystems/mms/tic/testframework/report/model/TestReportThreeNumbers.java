package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;

public class TestReportThreeNumbers extends AbstractTestReportNumbers {

    public TestReportThreeNumbers() {
        highCorridorActual = 24;
        midCorridorActual = 4;
        lowCorridorActual = 5;
        all = 73;
        allSuccessful = 24;
        passed = 9;
        passedMinor = 13;
        passedRetry = 2;
        allSkipped = 16;
        skipped = 16;
        allBroken = 33;
        failed = 13;
        failedMinor = 20;
        failedRetried = 4;
        failedExpected = 3;
        failureAspects = 5;
        exitPoints = 36;
        percentage = 32;
    }

}
