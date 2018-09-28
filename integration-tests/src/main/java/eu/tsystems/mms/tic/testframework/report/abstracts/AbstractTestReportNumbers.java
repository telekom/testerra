package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.report.model.TestNumberHelper;

/**
 * Created by fakr on 12.09.2017
 */
public abstract class AbstractTestReportNumbers implements TestNumberHelper {

    protected int highCorridorActual = 0;
    protected int midCorridorActual = 0;
    protected int lowCorridorActual = 0;

    protected int highCorridorLimit = 24;
    protected int midCorridorLimit = 3;
    protected int lowCorridorLimit = 5;

    private final String fcMatched = "fcMatched";
    private final String fcNotMatched = "fcNotMatched";

    protected int all = 0;
    protected int allSuccessful = 0;
    protected int passed = 0;
    protected int passedMinor = 0;
    protected int allSkipped = 0;
    protected int skipped = 0;
    protected int allBroken = 0;
    protected int failed = 0;
    protected int failedMinor = 0;
    protected int failedRetried = 0;
    protected int failedExpected = 0;

    protected int failureAspects = 0;
    protected int exitPoints = 0;

    //TODO add percentage sign in test
    protected int percentage = 0;

    //protected String failureCorridorMatchingColor = red;
    //protected String failureCorridorMatchingColor = "green";

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

    public String getHighMatched() {
        return getMatchedValue(highCorridorActual, highCorridorLimit);
    }


    public String getMidMatched() {
        return getMatchedValue(midCorridorActual, midCorridorLimit);
    }

    public String getLowMatched() {
        return getMatchedValue(lowCorridorActual, lowCorridorLimit);
    }

    private String getMatchedValue(int actual, int limit){
        if (actual < limit)
            return fcMatched;
        else
            return fcNotMatched;
    }

    public int getAll() {
        return all;
    }

    public int getAllSuccessful() {
        return allSuccessful;
    }

    public int getPassed() {
        return passed;
    }

    public int getPassedMinor() {
        return passedMinor;
    }


    public int getAllSkipped() {
        return allSkipped;
    }

    public int getSkipped() {
        return skipped;
    }


    public int getAllBroken() {
        return allBroken;
    }

    public int getFailed() {
        return failed;
    }

    public int getFailedMinor() {
        return failedMinor;
    }

    public int getFailedRetried() {return  failedRetried;}

    public int getFailureAspects() {
        return failureAspects;
    }

    public int getExitPoints() {
        return exitPoints;
    }

    public int getFailedExpected() {
        return failedExpected;
    }

    public String getPercentage() { return String.valueOf(this.percentage + "%"); }

    public boolean isSkipped() { return isSkipped; }

    public boolean isExpectedToFail() { return isExpectedToFail; }
}
