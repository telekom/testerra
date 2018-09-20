package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Created by jlma on 17.05.2017.
 */
public interface TestNumberHelper {

    int getHighCorridorActual();
    int getHighCorridorLimit();
    int getMidCorridorActual();
    int getMidCorridorLimit();
    int getLowCorridorActual();
    int getLowCorridorLimit();
    String getHighMatched();
    String getMidMatched();
    String getLowMatched();
    int getAll();
    int getAllPassed();
    int getPassed();
    int getPassedMinor();
    int getPassedInherited();
    int getAllFailed();
    int getFailed();
    int getFailedMinor();
    int getFailedInherited();
    int getAllSkipped();
    int getSkipped();
    int getSkippedInherited();
    int getOverallDelta();
    int getPassedDelta();
    int getFailedDelta();
    int getSkippedDelta();
    int getFailureAspects();
    int getExitPoints();
    int getExpectedFailed();
    String getPercentage();
    String getPercentageDelta();
    int getNumberOfAcknowledgementMethods();
    int getNumberOfStateChanges();
    String getFailureCorridorMatchingColor();
    boolean isDelta();
    boolean isInherited();
    boolean isSkipped();
    boolean isExpectedToFail();
}
