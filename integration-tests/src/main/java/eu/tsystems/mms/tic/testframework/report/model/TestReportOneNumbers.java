package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportOneNumbers extends AbstractTestReportNumbers {

    public TestReportOneNumbers() {
        highCorridorLimit = 34;
        highCorridorActual = 34;
        midCorridorActual = 3;
        lowCorridorActual = 5;
        all = 102;
        allSuccessful = 38;
        passed = 22;
        passedMinor = 13;
        passedRetry = 3;
        allSkipped = 22;
        skipped = 22;
        allBroken = 42;
        failed = 23;
        failedMinor = 19;
        failedRetried = 5;
        failedExpected = 3;
        failureAspects = 8;
        exitPoints = 40;
        percentage = 36;
    }

}
