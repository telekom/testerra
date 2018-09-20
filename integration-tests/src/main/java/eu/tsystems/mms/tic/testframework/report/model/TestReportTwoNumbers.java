package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportTwoNumbers extends AbstractTestReportNumbers {

    public TestReportTwoNumbers() {
        highCorridorActual = 26;
        highCorridorLimit = 19;
        midCorridorActual = 3;
        midCorridorLimit = 3;
        lowCorridorActual = 5;
        lowCorridorLimit = 5;
        highMatched = "fcNotMatched";
        midMatched = "fcMatched";
        lowMatched = "fcMatched";
        all = 76;
        allPassed = 26;
        passed = 13;
        passedMinor = 13;
        passedInherited = 0;
        allFailed = 34;
        failed = 13;
        failedMinor = 21;
        failedInherited = 0;
        allSkipped = 16;
        skipped = 16;
        skippedInherited = 0;
        overallDelta = -24;
        passedDelta = -10;
        failedDelta = -8;
        skippedDelta = -6;
        failureAspects = 6;
        exitPoints = 32;
        expectedFailed = 6;
        percentage = "34%";
        percentageDelta = "\u2193 -2%"; // u2193 == arrow down
        numberOfStateChanges = 22;
        failureCorridorMatchingColor = "red";
        isDelta = true;
        isInherited = true;
        isSkipped = true;
        isExpectedToFail = true;
    }


}
