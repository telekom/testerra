/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

public class TestReportFiveNumbers extends AbstractTestReportNumbers {

    public TestReportFiveNumbers() {
        highCorridorActual = 20;
        highCorridorLimit = 19;
        midCorridorActual = 4;
        lowCorridorActual = 6;
        all = 63;
        allSuccessful = 19;
        passed = 7;
        passedMinor = 12;
        allSkipped = 14;
        skipped = 14;
        allBroken = 30;
        failed = 10;
        failedMinor = 20;
        failureAspects = 3;
        exitPoints = 30;
        percentage = 30;
    }
}
