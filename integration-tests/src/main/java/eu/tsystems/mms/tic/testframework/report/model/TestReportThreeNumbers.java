package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportThreeNumbers extends AbstractTestReportNumbers {

    public TestReportThreeNumbers() {
        highCorridorActual = 24;
        highCorridorLimit = 24;
        midCorridorActual = 4;
        midCorridorLimit = 3;
        lowCorridorActual = 5;
        lowCorridorLimit = 5;
        highMatched = "fcMatched";
        midMatched = "fcNotMatched";
        lowMatched = "fcMatched";
        all = 73;
        allPassed = 24;
        passed = 11;
        passedMinor = 13;
        passedInherited = 0;
        allFailed = 33;
        failed = 13;
        failedMinor = 20;
        failedInherited = 0;
        allSkipped = 16;
        skipped = 16;
        skippedInherited = 0;
        overallDelta = -3;
        passedDelta = -2;
        failedDelta = -1;
        skippedDelta = 0;
        failureAspects = 5;
        exitPoints = 32;
        expectedFailed = 3;
        percentage = "32%";
        percentageDelta = "-2%";
        numberOfStateChanges = 10;
        failureCorridorMatchingColor = "red";
        isDelta = true;
        isInherited = false;
        isSkipped = true;
        isExpectedToFail = true;
    }

}
