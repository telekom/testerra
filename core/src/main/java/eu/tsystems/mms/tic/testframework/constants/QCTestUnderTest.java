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
package eu.tsystems.mms.tic.testframework.constants;

import java.util.LinkedList;

/**
 * User: rnhb Date: 19.12.13
 */
public enum QCTestUnderTest {

    QCSYNC1_SUCCESSFULTESTNGTEST("successfulTestNGTest"),
    QCSYNC1_FAILINGTESTNGTEST("failingTestNGTest"),
    QCSYNC1_CUTWITHUNDERSCORE("cutTestPrefixFromMethodNameWithUnderscore",
            "test_cutTestPrefixFromMethodNameWithUnderscore"),
    QCSYNC1_CUTWITHOUTUNDERSCORE("cutTestPrefixFromMethodNameWithoutUnderscore",
            "testcutTestPrefixFromMethodNameWithoutUnderscore"),
    QCSYNC1_MULTIPLEMETHOD("multipleMethodOccurances"),
    QCSYNC1_NOTEXISTINGTESTMETHOD("notExistingTestMethod", null),

    QCSYNC2_SUCCESSFULTESTNGTEST("successfulTestNGTest"),
    QCSYNC2_FAILINGTESTNGTEST("failingTestNGTest"),
    QCSYNC2_NOCLASS("notExistingClass", null),
    QCSYNC2_NOMETHOD("notExistingMethod", null),
    QCSYNC2_NOSCRIPTNAME("notExistingScriptName", null),

    QCSYNC3_SUCCESSFULTEST("successfulTest"),
    QCSYNC3_FAILINGTEST("failingTest"),
    QCSYNC3_METHODCORRECTOVERRIDE("correctMethodAnnotationOverridesWrongClassAnnotation"),
    QCSYNC3_METHODWRONGOVERRIDE("wrongMethodAnnotationOverridesCorrectClassAnnotation"),
    QCSYNC3_CORRECTMETHOD("correctMethodAnnotation"),
    QCSYNC3_WRONGMETHOD("wrongMethodAnnotation"),
    QCSYNC3_CORRECTTESTNAME("correctTestNameAnnotation", "someNotKnownTestname"),
    QCSYNC3_WRONGTESTNAME("notExistingTestNameAnnotation", "someNotKnownTestnameWithWrongAnnotation"),
    QCSYNC3_CORRECTCLASS("correctClassAnnotation"),
    QCSYNCPROG_TEST1("testTest1"),
    QCSYNCPROG_TEST2("test3"),
    QCSYNC3_WRONGCLASS("wrongClassAnnotation");

    /** The name of the TestSetTest in QC. */
    public final String testName;
    /** The name of the method that is connected to the test. */
    public final String methodName;

    private QCTestUnderTest(String testName, String methodName) {
        this.testName = testName;
        this.methodName = methodName;
    }

    private QCTestUnderTest(String testName) {
        this(testName, testName);
    }

    @Override
    public String toString() {
        return getTestName();
    }

    /**
     * gets the test name for the webservice
     *
     * @return .
     */
    public String toStringWebService() {
        return "[1]" + getTestName();
    }

    /**
     * gets the test names for the test sets
     *
     * @param type .
     * @return test names
     */
    public static LinkedList<QCTestUnderTest> getTestSetTestNames(QCSyncType type) {
        String startsWith = type.toString().toUpperCase();
        LinkedList<QCTestUnderTest> testNames = new LinkedList<QCTestUnderTest>();
        for (QCTestUnderTest test : QCTestUnderTest.values()) {
            if (test.name().startsWith(startsWith)) {
                testNames.add(test);
            }
        }
        return testNames;
    }

    /**
     * checks the different qc syncs
     *
     * @return .
     */
    public QCSyncType getQCSyncType() {
        if (this.name().startsWith("QCSYNC1")) {
            return QCSyncType.QCSYNC1;
        }
        if (this.name().startsWith("QCSYNC2")) {
            return QCSyncType.QCSYNC2;
        }
        if (this.name().startsWith("QCSYNC3")) {
            return QCSyncType.QCSYNC3;
        }
        return null;
    }

    /**
     * checks test name
     *
     * @return .
     */
    public boolean hasTestSetTest() {
        if (getTestName() == null) {
            return false;
        }
        return true;
    }

    /**
     * checks method name
     * 
     * @return .
     */
    public boolean hasTestMethod() {
        if (methodName == null) {
            return false;
        }
        return true;
    }

    /**
     * checks names for equality
     *
     * @return .
     */
    public boolean differentNaming() {
        if (getTestName().equals(methodName)) {
            return false;
        }
        return true;
    }

    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }
}
