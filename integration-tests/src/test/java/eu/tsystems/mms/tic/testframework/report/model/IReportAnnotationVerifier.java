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

public interface IReportAnnotationVerifier {


    String LOCATOR_FONT_ANNOTATION = "//font[contains(text(),'%s')]";
    //String RETRIED_NAME = "Retried";

    /**
     * Asserts that an expected annotation mark is displayed for a given method
     *
     * @param annotationType
     * @param methodName
     */
    void assertAnnotationMarkIsDisplayed(ReportAnnotationType annotationType, String methodName);

    /**
     * Asserts that all annotation marks are displayed for a given method
     *
     * @param methodName
     */
    void assertAllAnnotationMarksAreDisplayed(String methodName);

    /**
     * Asserts that the 'Retried' mark is displayed for a given method
     *
     * @param methodName
     */
    //void assertRetryMarkerIsDisplayed(String methodName);
}
