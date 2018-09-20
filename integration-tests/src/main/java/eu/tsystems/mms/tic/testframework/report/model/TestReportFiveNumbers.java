package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportFiveNumbers extends AbstractTestReportNumbers {

    public TestReportFiveNumbers() {
        highCorridorActual = 20;
        highCorridorLimit = 19;
        midCorridorActual = 4;
        midCorridorLimit = 3;
        lowCorridorActual = 6;
        lowCorridorLimit = 5;
        highMatched = "fcNotMatched";
        midMatched = "fcNotMatched";
        lowMatched = "fcNotMatched";
        all = 63;
        allPassed = 19;
        passed = 7;
        passedMinor = 12;
        passedInherited = 0;
        allFailed = 30;
        failed = 10;
        failedMinor = 20;
        failedInherited = 0;
        allSkipped = 14;
        skipped = 14;
        skippedInherited = 0;
        overallDelta = 2;
        passedDelta = 0;
        failedDelta = 2;
        skippedDelta = 0;
        failureAspects = 3;
        exitPoints = 28;
        expectedFailed = 1;
        percentage = "30%";
        percentageDelta = "-1%";
        numberOfStateChanges = 2;
        failureCorridorMatchingColor = "red";
        isDelta = true;
        isInherited = false;
        isSkipped = false;
        isExpectedToFail = false;
    }

}
