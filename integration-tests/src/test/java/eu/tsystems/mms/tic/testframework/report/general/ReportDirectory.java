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
package eu.tsystems.mms.tic.testframework.report.general;

public enum ReportDirectory {
    REPORT_DIRECTORY_1("reportDirectoryExecution1"),
    REPORT_DIRECTORY_2("reportDirectoryExecution2"),
    REPORT_DIRECTORY_3("reportDirectoryExecution3"),
    REPORT_DIRECTORY_4("reportDirectoryExecution4"),
    REPORT_DIRECTORY_5("reportDirectoryExecution5"),
    REPORT_DIRECTORY_6("reportDirectoryExecution6"),
    REPORT_DIRECTORY_7("reportDirectoryExecution7"),
    REPORT_DIRECTORY_8("reportDirectoryExecution8"),
    REPORT_DIRECTORY_9("reportDirectoryExecution9");

    private String reportDirectory;

    ReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String getReportDirectory(){
        return reportDirectory;
    }
}
