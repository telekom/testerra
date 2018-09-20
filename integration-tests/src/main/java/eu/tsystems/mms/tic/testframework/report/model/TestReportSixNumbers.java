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
        midCorridorLimit = 3;
        lowCorridorActual = 5;
        lowCorridorLimit = 5;
        highMatched = "fcMatched";
        midMatched = "fcMatched";
        lowMatched = "fcMatched";
        all = 54;
        allPassed = 20;
        passed = 8;
        passedMinor = 12;
        //passedInherited = 4;
        allFailed = 34;
        failed = 17;
        failedMinor = 17;
        //failedInherited = 6;
        allSkipped = 0;
        //skipped = 9;
        //skippedInherited = 5;
        overallDelta = -9;
        passedDelta = 1;
        failedDelta = 4;
        skippedDelta = -14;
        failureAspects = 10;
        exitPoints = 32;
        expectedFailed = 1;
        percentage = "37%";
        percentageDelta = "7%";
        numberOfStateChanges = 33;
        failureCorridorMatchingColor = "green";
        isDelta = true;
        isInherited = false;
        isSkipped = false;
        isExpectedToFail = false;
    }

}
