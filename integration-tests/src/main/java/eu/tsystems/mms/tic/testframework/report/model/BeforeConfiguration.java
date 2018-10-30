package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Created by fakr on 14.11.2017
 */
public enum BeforeConfiguration {

    BEFORE_SUITE("ReportTestUnderTestBeforeScenarios", "beforeSuiteFailed", null),
    BEFORE_TEST("ReportTestUnderTestBeforeScenarios_Before Scenario_Before Test Failed", "beforeTestFailed", null),
    BEFORE_GROUPS("ReportTestUnderTestBeforeScenarios_Before Scenario_Before Groups Failed", "beforeGroupsFailed", null),
    BEFORE_CLASS("ReportTestUnderTestBeforeScenarios_Before Scenario_Before Class Failed", "beforeClassFailed", null),
    BEFORE_METHOD("ReportTestUnderTestBeforeScenarios_Before Scenario_Before Method Failed", "beforeMethodFailed", new String[]{"controlMethodAfterBeforeScenarioFailed", "controlMethodAfterBeforeScenarioPassed"}),
    ALL("ReportTestUnderTestBeforeScenarios_Before Scenario_Before Run All", "", null);

    private String reportClassName;

    private String reportMethodName;

    private String[] controlMethodNames;

    public String getReportClassName() {
        return reportClassName;
    }

    public String getReportMethodName() {
        return reportMethodName;
    }

    public String[] getControlMethodName(){return controlMethodNames;}

    BeforeConfiguration(String reportClassName, String reportMethodName, String[] controlMethodNames) {
        this.reportClassName = reportClassName;
        this.reportMethodName = reportMethodName;
        this.controlMethodNames = controlMethodNames;
    }

}
