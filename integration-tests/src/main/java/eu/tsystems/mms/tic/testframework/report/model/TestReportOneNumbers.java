package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by jlma on 30.05.2017.
 */
public class TestReportOneNumbers extends AbstractTestReportNumbers {

    public TestReportOneNumbers() {
        highCorridorActual = 34;
        highCorridorLimit = 34;
        midCorridorActual = 3;
        midCorridorLimit = 3;
        lowCorridorActual = 5;
        lowCorridorLimit = 5;
        highMatched = "fcMatched";
        midMatched = "fcMatched";
        lowMatched = "fcMatched";
        all = 100;
        allPassed = 36;
        passed = 23;
        passedMinor = 13;
        passedInherited = 0;
        allFailed = 42;
        failed = 23;
        failedMinor = 19;
        failedInherited = 0;
        allSkipped = 22;
        skipped = 22;
        skippedInherited = 0;
        overallDelta = 0;
        passedDelta = 0;
        failedDelta = 0;
        skippedDelta = 0;
        failureAspects = 8;
        exitPoints = 41;
        expectedFailed = 3;
        percentage = "36%";
        percentageDelta = "%";
        numberOfAcknowledgmentMethods = 4;
        numberOfStateChanges = 115;
        failureCorridorMatchingColor = "red";
        isDelta = false;
        isInherited = false;
        isSkipped = true;
        isExpectedToFail = false;
    }

}
