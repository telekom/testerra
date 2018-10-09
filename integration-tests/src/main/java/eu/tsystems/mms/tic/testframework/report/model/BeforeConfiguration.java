package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Created by fakr on 14.11.2017
 */
public enum BeforeConfiguration {

    BEFORE_SUITE("ReportTestUnderTestBeforeScenarios", "beforeSuiteFailed"),
    BEFORE_TEST("ReportTestUnderTestBeforeScenarios_in_Before Scenario-Before Test Failed", "beforeTestFailed"),
    BEFORE_GROUPS("ReportTestUnderTestBeforeScenarios_in_Before Scenario-Before Groups Failed", "beforeGroupsFailed"),
    BEFORE_CLASS("ReportTestUnderTestBeforeScenarios_in_Before Scenario-Before Class Failed", "beforeClassFailed"),
    BEFORE_METHOD("ReportTestUnderTestBeforeScenarios_in_Before Scenario-Before Method Failed", "beforeMethodFailed"),
    ALL("ReportTestUnderTestBeforeScenarios_in_Before Scenario-Before Run All", "");

    private String reportClassName;

    private String reportMethodName;

    public String getReportClassName() {
        return reportClassName;
    }

    public String getReportMethodName() {
        return reportMethodName;
    }

    BeforeConfiguration(String reportClassName, String reportMethodName) {
        this.reportClassName = reportClassName;
        this.reportMethodName = reportMethodName;
    }

}
