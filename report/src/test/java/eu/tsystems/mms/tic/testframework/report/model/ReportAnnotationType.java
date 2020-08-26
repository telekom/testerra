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

public enum ReportAnnotationType {

    SUPPORT_METHOD("Support Method", "SupportMethod"),
    NEW("New", "New"),
    READY_FOR_APPROVAL("Ready For Approval", "ReadyForApproval"),
    IN_DEVELOPMENT("In Development","InDevelopment"),
    NO_RETRY("No Retry","NoRetry"),
    RETRIED("Retried", "Retried"),
    MINOR("Minor Errors", "Minor Errors");

    private String annotationName;

    private String shortcut;

    ReportAnnotationType(String annotationName, String shortcut) {
        this.annotationName = annotationName;
        this.shortcut = shortcut;
    }

    public String getAnnotationDisplayedName() {
        return this.annotationName;
    }

    public String getTestMethodShortcut() {
        return this.shortcut;
    }
}
