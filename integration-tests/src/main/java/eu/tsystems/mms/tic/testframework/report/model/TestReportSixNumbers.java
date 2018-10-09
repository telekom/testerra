package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportSixNumbers extends AbstractTestReportNumbers {

    public TestReportSixNumbers() {
        highCorridorActual = 26;
        highCorridorLimit = 26;
        midCorridorActual = 3;
        lowCorridorActual = 5;
        all = 54;
        allSuccessful = 20;
        passed = 8;
        passedMinor = 12;
        allSkipped = 0;
        skipped = 0;
        allBroken = 34;
        failed = 17;
        failedMinor = 17;
        failedRetried = 7;
        failureAspects = 10;
        exitPoints = 32;
        percentage = 37;
    }

}
