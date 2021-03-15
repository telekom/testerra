/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.report.model;

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
