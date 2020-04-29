package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;

public class TestReportFourNumbers extends AbstractTestReportNumbers {

    public TestReportFourNumbers() {
        highCorridorLimit = 19;
        highCorridorActual = 19;
        midCorridorActual = 3;
        lowCorridorActual = 6;
        all = 61;
        allSuccessful = 19;
        passed = 7;
        passedMinor = 12;
        allBroken = 28;
        failed = 10;
        failedMinor = 18;
        allSkipped = 14;
        skipped = 14;
        failureAspects = 3;
        exitPoints = 28;
        percentage = 31;
    }
}
