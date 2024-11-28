/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.utils;

import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.Date;

/**
 * General formatter for everything
 *
 * @author Mike Reiche
 */
public interface Formatter {
    default String cutString(String string, int maxLength) {
        return cutString(string, maxLength, "...");
    }

    default String cutString(String string, int maxLength, String replacement) {
        if (string.length() > maxLength) {
            int first = Math.round((maxLength - replacement.length()) / 2);
            int second = string.length() - first;
            return String.format("%s%s%s", string.substring(0, first), replacement, string.substring(second));
        } else {
            return string;
        }
    }

    default String DATE_TIME_FORMAT() {
        return "dd.MM.yyyy-HH:mm:ss.SSS";
    }

    String logTime(Date date);

    default String getMethodName(ITestResult testResult) {
        ITestNGMethod testMethod = testResult.getMethod();
        Object[] parameters = testResult.getParameters();
        String parameterString = "";
        if (parameters != null) {
            parameterString = Arrays.toString(parameters)
                    .replace("[", "")
                    .replace("]", "");
        }

        return testMethod.getTestClass().getRealClass().getSimpleName()
                .concat(".")
                .concat(testMethod.getMethodName())
                .concat("(")
                .concat(parameterString)
                .concat(")");
    }

    default String toString(ITestResult testResult) {
        ITestNGMethod testMethod = testResult.getMethod();

        return (testMethod.isTest() ? "Test" : "Configuration")
                .concat(" ")
                .concat(this.getMethodName(testResult));
    }

}
