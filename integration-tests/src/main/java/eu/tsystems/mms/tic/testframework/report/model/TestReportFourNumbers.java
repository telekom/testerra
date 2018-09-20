package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportFourNumbers extends AbstractTestReportNumbers {

    public TestReportFourNumbers() {
        highCorridorActual = 19;
        highCorridorLimit = 19;
        midCorridorActual = 3;
        midCorridorLimit = 3;
        lowCorridorActual = 6;
        lowCorridorLimit = 5;
        highMatched = "fcMatched";
        midMatched = "fcMatched";
        lowMatched = "fcNotMatched";
        all = 61;
        allPassed = 19;
        passed = 7;
        passedMinor = 12;
        passedInherited = 0;
        allFailed = 28;
        failed = 10;
        failedMinor = 18;
        failedInherited = 0;
        allSkipped = 14;
        skipped = 14;
        skippedInherited = 0;
        overallDelta = -12;
        passedDelta = -5;
        failedDelta = -5;
        skippedDelta = -2;
        failureAspects = 3;
        exitPoints = 26;
        expectedFailed = 1;
        percentage = "31%";
        percentageDelta = "-1%";
        numberOfStateChanges = 1;
        failureCorridorMatchingColor = "red";
        isDelta = true;
        isInherited = false;
        isSkipped = true;
        isExpectedToFail = false;
    }
}
