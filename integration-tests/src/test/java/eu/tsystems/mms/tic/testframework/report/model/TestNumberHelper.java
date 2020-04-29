package eu.tsystems.mms.tic.testframework.report.model;

public interface TestNumberHelper {

    int getHighCorridorActual();
    int getHighCorridorLimit();
    int getMidCorridorActual();
    int getMidCorridorLimit();
    int getLowCorridorActual();
    int getLowCorridorLimit();
    String getFailureCorridorMatched();
    String getHighMatched();
    String getMidMatched();
    String getLowMatched();

    int getAll();
    int getAllSuccessful();
    int getPassed();
    int getPassedMinor();
    int getPassedRetry();
    int getAllSkipped();
    int getSkipped();
    int getAllBroken();
    int getFailed();
    int getFailedMinor();
    int getFailedRetried();
    int getFailedExpected();
    int getFailureAspects();
    int getExitPoints();
    String getPercentage();

}
