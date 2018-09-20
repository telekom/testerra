package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.report.model.TestNumberHelper;

/**
 * Created by fakr on 12.09.2017
 */
public abstract class AbstractTestReportNumbers implements TestNumberHelper {

    protected int highCorridorActual;
    protected int highCorridorLimit;
    protected int midCorridorActual;
    protected int midCorridorLimit;
    protected int lowCorridorActual;
    protected int lowCorridorLimit;
    protected String highMatched;
    protected String midMatched;
    protected String lowMatched;

    protected int all;
    protected int allPassed;
    protected int passed;
    protected int passedMinor;
    protected int passedInherited;
    protected int allFailed;
    protected int failed;
    protected int failedMinor;
    protected int failedInherited;
    protected int allSkipped;
    protected int skipped;
    protected int skippedInherited;

    protected int overallDelta;
    protected int passedDelta;
    protected int failedDelta;
    protected int skippedDelta;

    protected int failureAspects;
    protected int exitPoints;
    protected int expectedFailed;

    protected String percentage;
    protected String percentageDelta;

    protected int numberOfAcknowledgmentMethods;
    protected int numberOfStateChanges;
    protected String failureCorridorMatchingColor;

    protected boolean isDelta;
    protected boolean isInherited;
    protected boolean isSkipped;
    protected boolean isExpectedToFail;

    public int getHighCorridorActual() {
        return highCorridorActual;
    }

    public int getHighCorridorLimit() {
        return highCorridorLimit;
    }

    public int getMidCorridorActual() {
        return midCorridorActual;
    }

    public int getMidCorridorLimit() {
        return midCorridorLimit;
    }

    public int getLowCorridorActual() {
        return lowCorridorActual;
    }

    public int getLowCorridorLimit() {
        return lowCorridorLimit;
    }

    public String getHighMatched() { return highMatched; }

    public String getMidMatched() { return midMatched; }

    public String getLowMatched() { return lowMatched; }

    public int getAll() {
        return all;
    }

    public int getAllPassed() {
        return allPassed;
    }

    public int getPassed() {
        return passed;
    }

    public int getPassedMinor() {
        return passedMinor;
    }

    public int getPassedInherited() { return passedInherited; }

    public int getAllFailed() {
        return allFailed;
    }

    public int getFailed() {
        return failed;
    }

    public int getFailedMinor() {
        return failedMinor;
    }

    public int getFailedInherited() { return failedInherited; }

    public int getAllSkipped() {
        return allSkipped;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getSkippedInherited() { return skippedInherited; }

    public int getOverallDelta() {
        return overallDelta;
    }

    public int getPassedDelta() {
        return passedDelta;
    }

    public int getFailedDelta() {
        return failedDelta;
    }

    public int getSkippedDelta() {
        return skippedDelta;
    }

    public int getFailureAspects() {
        return failureAspects;
    }

    public int getExitPoints() {
        return exitPoints;
    }

    public int getExpectedFailed() {
        return expectedFailed;
    }

    public String getPercentage() { return percentage; }

    public String getPercentageDelta() { return percentageDelta; }

    public int getNumberOfAcknowledgementMethods() {
        return numberOfAcknowledgmentMethods;
    }

    public int getNumberOfStateChanges() { return numberOfStateChanges; }

    public String getFailureCorridorMatchingColor() { return failureCorridorMatchingColor; }

    public boolean isDelta() { return isDelta; }

    public boolean isInherited() { return isInherited; }

    public boolean isSkipped() { return isSkipped; }

    public boolean isExpectedToFail() { return isExpectedToFail; }
}
