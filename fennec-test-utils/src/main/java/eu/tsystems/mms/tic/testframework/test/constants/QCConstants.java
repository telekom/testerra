/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.test.constants;

/**
 * User: rnhb Date: 13.12.13
 */
public final class QCConstants {

    private QCConstants() {
    }

    /** QC Path where all TestSets are placed. */
    public static final String QC_TESTSUNDERTEST_FOLDER = "Root\\Xeta\\TestSetsUnderTest\\";

    /**
     * Maximum of time that has been gone between QC Synchronization of tests and control by integration tests. Tests
     * take about 15 minutes now, so we chose 20 minutes as max. interval.
     */
    public static final long SYSTEMTEST_SYNCINTERVAL = 3000000;

    public static final long QCREST_SYNCINTERVAL = 15000;
    public static final long QCWEBSERVICE_SYNCINTERVAL = 60000;

    /** Path that does not exist in QC */
    public static final String NOT_EXISTING_FOLDER = "Root\\Xeta\\Not\\Existing\\";
    public static final String NOT_EXISTING_TESTSET = "NotExistingTestSet";
    public static final String NOT_EXISTING_PATH = NOT_EXISTING_FOLDER + NOT_EXISTING_TESTSET;

    /** Name of the TestSets for QC Sync Tests, for both System- and UnitTests */
    public static final String QCSYNC1_TESTSET_NAME = "QCSync1";
    public static final String QCSYNC2_TESTSET_NAME = "QCSync2";
    public static final String QCSYNC3_TESTSET_NAME = "QCSync3";

    /** Constants for the TA_scriptanmes of TestSetTests in QC for Sync type 2 */
    public static final String QCSYNC2_TASCRIPTNAME_NOCLASS = "eu.tsystems.mms.tic.testframework.connectors." +
            "testsundertest.qcsync2.NotExistingClass.irrelevantMethod";
    public static final String QCSYNC2_TASCRIPTNAME_NOMETHOD = "eu.tsystems.mms.tic.testframework.connectors." +
            "qcsync2.testundertest.Tests.notExistingMethod";

    public static final String QCCONNECTOR_QCSYNC1_PACKAGE = "eu.tsystems.mms.tic.testframework.qcconnector.testsundertest";
    public static final String QC11CONNECTOR_QCSYNC1_PACKAGE = "eu.tsystems.mms.tic.testframework.qc11connector.testsundertest";
}
