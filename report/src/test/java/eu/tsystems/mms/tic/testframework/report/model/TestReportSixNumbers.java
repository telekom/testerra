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


import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractTestReportNumbers;

public class TestReportSixNumbers extends AbstractTestReportNumbers {

    public TestReportSixNumbers() {
        highCorridorActual = 26;
        highCorridorLimit = 26;
        midCorridorActual = 3;
        lowCorridorActual = 5;
        all = 56;
        allSuccessful = 22;
        passed = 8;
        passedMinor = 12;
        passedRetry = 2;
        allSkipped = 0;
        skipped = 0;
        allBroken = 34;
        failed = 17;
        failedMinor = 17;
        failedRetried = 8;
        failureAspects = 10;
        exitPoints = 30;
        percentage = 37;
    }

}
