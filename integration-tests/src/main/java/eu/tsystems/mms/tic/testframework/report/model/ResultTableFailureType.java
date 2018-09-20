package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.pageobjects.ExitPointsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.FailureAspectsPage;

/**
 * Created by fakr on 16.08.2017
 */
public enum ResultTableFailureType {

    FAILURE_ASPECT("Failure Aspect", FailureAspectsPage.class),
    EXIT_POINT("Exit Point", ExitPointsPage.class);

    private String label;
    private Class landingPageClass;

    ResultTableFailureType(String label, Class landingPageClass) {
        this.label = label;
        this.landingPageClass = landingPageClass;
    }

    public Class getLandingPageClass() {
        return landingPageClass;
    }

    public String getLabel() {
        return label;
    }
}
