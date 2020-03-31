package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

public class TestReportNineNumbers extends AbstractTestReportNumbers {

    public TestReportNineNumbers() {
        highCorridorLimit = 0;
        midCorridorLimit = 0;
        lowCorridorLimit = 0;

        highCorridorActual = 1;

        all = 10;
        allSuccessful = 1;
        passed = 1;
        allSkipped = 8;
        skipped = 8;
        allBroken = 1;
        failed = 1;
        exitPoints = 1;
        failureAspects = 1;
    }
}
