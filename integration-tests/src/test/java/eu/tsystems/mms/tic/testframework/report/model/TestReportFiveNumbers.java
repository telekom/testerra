package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;

public class TestReportFiveNumbers extends AbstractTestReportNumbers {

    public TestReportFiveNumbers() {
        highCorridorActual = 20;
        highCorridorLimit = 19;
        midCorridorActual = 4;
        lowCorridorActual = 6;
        all = 63;
        allSuccessful = 19;
        passed = 7;
        passedMinor = 12;
        allSkipped = 14;
        skipped = 14;
        allBroken = 30;
        failed = 10;
        failedMinor = 20;
        failureAspects = 3;
        exitPoints = 30;
        percentage = 30;
    }
}
