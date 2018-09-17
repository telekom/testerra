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

/**
 * User: rnhb Date: 10.10.13 Time: 14:44
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    /**
     * no property set for browser
     *
     * @return error message
     */
    public static String browserNotSetInAnyProperty() {
        return "Browser must be set through SystemProperty 'browser' or in test.properties file!";
    }

    /**
     * browser properties are invalid
     *
     * @param browserString .
     * @return .
     */
    public static String browserNotSupportedHere(String browserString) {
        return browserNotSetInAnyProperty() + "is: " + browserString;
    }

    /**
     * selenium server host missing
     *
     * @return error message
     */
    public static String seleniumServerHostMissing() {
        return "selenium.server.host missing! Using localhost.";
    }

    /**
     * selenium server port missing
     *
     * @return error message
     */
    public static String seleniumServerPortMissing() {
        return "selenium.server.port missing! Using 4444.";
    }

    /**
     * Info when a test is skipped by the qc execution filter.
     * 
     * @return error message.
     */
    public static String skippedByQcExecutionFilter() {
        return "Skipped by fennec QC execution filter!";
    }

}
