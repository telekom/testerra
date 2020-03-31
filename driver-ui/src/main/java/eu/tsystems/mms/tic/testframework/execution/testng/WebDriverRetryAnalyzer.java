/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.execution.testng;

import org.openqa.selenium.WebDriverException;

import java.util.Arrays;

public class WebDriverRetryAnalyzer implements AdditionalRetryAnalyzer {

    String[] messages = {
            "Error communicating with the remote browser. It may have died",
            "was terminated due to TIMEOUT",
            "was terminated due to SO_TIMEOUT",
            "The requested URL could not be retrieved",
            "Squid is unable to create a TCP socket"
        };

    @Override
    public Throwable analyzeThrowable(Throwable throwable, String tMessage) {
        if (throwable instanceof WebDriverException) {
            if (tMessage != null) {
                final String tMessageLC = tMessage.toLowerCase();
                boolean anyMatch = Arrays.stream(messages).anyMatch(m -> tMessageLC.contains(m.toLowerCase()));
                if (anyMatch) {
                    return throwable;
                }
            }
        }

        return null;
    }
}
