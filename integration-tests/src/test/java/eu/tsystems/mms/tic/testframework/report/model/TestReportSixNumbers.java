package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;

public class TestReportSixNumbers extends AbstractTestReportNumbers {

    public TestReportSixNumbers() {
        highCorridorActual = 26;
        highCorridorLimit = 26;
        midCorridorActual = 3;
        lowCorridorActual = 5;
        all = 56;
        allSuccessful = 22;
        passed = 8;
        passedMinor = 12;
        passedRetry = 2;
        allSkipped = 0;
        skipped = 0;
        allBroken = 34;
        failed = 17;
        failedMinor = 17;
        failedRetried = 8;
        failureAspects = 10;
        exitPoints = 32;
        percentage = 37;
    }

}
