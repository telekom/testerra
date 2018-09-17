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
 * Date: 26.09.13
 * Time: 12:59
 */
public final class ErrorMessages {

    private ErrorMessages() {}

    /**
     * Error message when the specified TestSet couldn't be found
     *
     * @param testSetName .
     * @return formatted error message
     */
    public static String cannotFindTestSet(String testSetName) {
        return "An error occurred while reading the testSetTests for testSet "
                + testSetName
                + ".";
    }

    /**
     * Error message when a method from in QC was not found in the package that
     * is specified in the qcconnection property file.
     *
     * @param methodName .
     * @param packageName .
     * @return formatted error message
     */
    public static String cannotFindMethodInPackage(String methodName, String packageName) {
        return "Cannot find the method \""
                + methodName
                + "\" in any class in package \""
                + packageName
                + "\". Test will not run!";
    }

    /**
     * Error message when a class defined in ta_scriptname in QC was not found in the package that
     * is specified in the qcconnection property file.
     *
     * @param className .
     * @return formatted error message
     */
    public static String cannotFindClass(String className) {
        return "Cannot find the class "
                + className
                + ".";
    }

    /**
     * Error message when a method defined in ta_scriptname in QC was not found in the specified class
     *
     * @param className .
     * @param methodName .
     * @return formatted error message
     */
    public static String cannotFindMethodInClass(String methodName, String className) {
        return "Cannot find the test method "
                + methodName
                + " in class "
                + className
                + ".";
    }

    /**
     * Error message when ta_scriptname in QC was not set
     *
     * @param testName .
     * @return formatted error message
     */
    public static String taScriptnameIsNull(String testName) {
        return "Test "
                + testName
                + " couldn't run! ta_scriptname for testSetTest "
                + testName
                + " was null.";
    }

    /**
     * Error message when ta_scriptname in QC was not set
     *
     * @param testSetPath .
     * @param annotatedClass .
     * @return formatted error message
     */
    public static String wrongQCTestSetAnnotation(String testSetPath, String annotatedClass) {
        return "Cannot find "
                + testSetPath
                + " in QC. Please correct the path in QCTestSet Annotation in Class "
                + annotatedClass
                + "!";
    }

    /**
     * Error message when no method with matching name was found in testset.
     *
     * @param methodname .
     * @param testSetPath .
     * @return formatted error message
     */
    public static String noTestMethodFoundInQC(String methodname, String testSetPath) {
        return "No method "
                + methodname
                + " found in Testset "
                + testSetPath
                + ". Could not synchronize with QC!";
    }

    /**
     * Error message when ta_scriptname in QC was not set
     *
     * @param qcTestMethodName .
     * @param packageName .
     * @return formatted error message
     */
    public static String multipleMethodsInPackage(String qcTestMethodName, String packageName) {
        return "Found multiple methods with name "
                + qcTestMethodName
                + " in package "
                + packageName
                + ". Test will not run! Please rename some methods to make Quality Center synchronization possible.";
    }
}
