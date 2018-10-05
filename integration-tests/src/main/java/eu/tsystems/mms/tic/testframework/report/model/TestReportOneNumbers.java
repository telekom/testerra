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
        all = 100;
        allSuccessful = 36;
        passed = 23;
        passedMinor = 13;
        allSkipped = 22;
        skipped = 22;
        allBroken = 42;
        failed = 23;
        failedMinor = 19;
        failedRetried = 5;
        failedExpected = 3;
        failureAspects = 8;
        exitPoints = 41;
        percentage = 36;
        //TODO what does this value is for
        isSkipped = true;
        //TODO what does this value is for
        isExpectedToFail = false;
    }

}
