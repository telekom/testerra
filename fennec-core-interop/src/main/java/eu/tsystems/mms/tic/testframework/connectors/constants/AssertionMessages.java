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
package eu.tsystems.mms.tic.testframework.connectors.constants;

/**
 * User: rnhb
 * Date: 18.12.13
 */
public final class AssertionMessages {

    private AssertionMessages() {
    }

    /**
     * shows the class not found and cant execute them
     *
     * @param className .
     * @return .
     */
    public static String classContainingTestsUnderTestNotFound(String className) {
        return "Class \""
                + className
                + "\" not found. Can't execute the supposedly contained TestsUnderTest.";
    }

    /** To use with assertEquals, so the difference is shown!
     *  @return .
     */
    public static String exceptionNotThrown() {
        return "The thrown exception did not contain the expected text.";
    }

    /**
     * warning that some tests give false results
     *
     * @param testName .
     * @param listener .
     * @return .
     */
    public static String testUnderTestNotRunOrHeard(String testName, String listener) {
        return "The TestUnderTest named \""
                + testName
                + "\" should be run by "
                + listener
                + " and heard by the Listener, but it wasn't. Some tests might give false results!";
    }

    /**
     * test does not run on jUnit
     *
     * @param testName .
     * @return .
     */
    public static String testUnderTestNotRunOrHeardJUnit(String testName) {
        return testUnderTestNotRunOrHeard(testName, "jUnit");
    }

    /**
     * test does not run on testNG
     *
     * @param testName .
     * @return .
     */
    public static String testUnderTestNotRunOrHeardTestNG(String testName) {
        return testUnderTestNotRunOrHeard(testName, "testNG");
    }

    /**
     * shows that test method isnt found in class
     *
     * @param testName .
     * @param className .
     * @return .
     */
    public static String testMethodNotFoundInClass(String testName, String className) {
        return "The test method named \""
                + testName
                + "\" should be in Class \""
                + className
                + "\", but wasn't found.";
    }

    /**
     * informs you that test is not found in testsetpath
     *
     * @param testName .
     * @param testSetPath .
     * @return .
     */
    public static String testFromConstantNotFoundInTestSet(String testName, String testSetPath) {
        return testNotFoundInTestSet(testName, testSetPath)
                + " It is expected because of a testName-constant in QCConstants.";
    }

    /**
     * proofs that test is in Qc-testset but wont be used
     *
     * @param testName .
     * @param testSetPath .
     * @return .
     */
    public static String testSetTestPresentWithoutQCConstant(String testName, String testSetPath) {
        return "The TestSetTest named \""
                + testName
                + "\" was found in QC-TestSet \""
                + testSetPath
                + "\", but it has no equivalent in QCConstants, so it won't be used in any test.";
    }

    /**
     * QC connecting error message is shown
     *
     * @return .
     */
    public static String errorConnectingToQC() {
        return "Error connecting to QC. Check if QC is reachable and the qcconnection.properties are valid!";
    }

    /** You might want to report the found and expected status - you should then use an assertEquals on the statusses.
     *  @param testName .
     *  @return .
     */
    public static String wrongStatus(String testName) {
        return "The Status of the Test \""
                + testName
                + "\" in QC is wrong.";
    }

    /**
     * informs when test synchronization was done
     *
     * @param testName .
     * @param syncInterval .
     * @return .
     */
    public static String staleSynchronizationSystemTest(String testName, long syncInterval) {
        return "Test Synchronization of \""
                + testName
                + "\" was done more than "
                + syncInterval
                + " minutes ago. Run the Integration Tests again or increase Constants.SYSTEMTEST_SYNCINTERVAL.";
    }

    /**
     * test not found in qc testset
     *
     * @param testName .
     * @param testSetName .
     * @return .
     */
    public static String testNotFoundInTestSet(String testName, String testSetName) {
        return "The Test \""
                + testName
                + "\" was not found in QC TestSet \""
                + testSetName
                + "\".";
    }

    /**
     * empty testset
     *
     * @param testSetName .
     * @return .
     */
    public static String testSetIsEmpty(String testSetName) {
        return "The TestSet \""
                + testSetName
                + "\" was empty.";
    }

    /**
     * test will be not found in path
     *
     * @param testSetName .
     * @param testSetPath .
     * @return .
     */
    public static String couldNotFindTestSet(String testSetName, String testSetPath) {
        return "The TestSet \""
                + testSetName
                + "\" was not found in path \""
                + testSetPath
                + "\".";
    }

    /**
     * testset not found in qc
     *
     * @param testSetPath .
     * @return .
     */
    public static String testSetNotExisting(String testSetPath) {
        return "The TestSet \""
                + testSetPath
                + "\" was not found in QC.";
    }

    /**
     * gives a report about the test synchronization
     *
     * @param testName .
     * @param syncInterval .
     * @param diff .
     * @return .
     */
    public static String staleSynchronization(String testName, long syncInterval, long diff) {
        return "Test Synchronization of \""
                + testName
                + "\" is stale. The difference is "
                + (diff / 1000)
                + "s, which is more than the allowed syncInterval of "
                + (syncInterval / 1000)
                + "s.";

    }

    /**
     * no successful test synchronization
     * @param testName .
     * @return .
     */
    public static String failedToSynchronizeTest(String testName) {
        return "Test \""
                + testName
                + "\" was not successfully synchronized.";
    }

    /**
     * providing test failed
     * @param testName .
     * @return .
     */
    public static String failedToProvideTest(String testName) {
        return "Failed to provide test \""
                + testName
                + "\".";
    }
}
