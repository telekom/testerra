/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.execution.testng;

import java.util.Arrays;
import java.util.Optional;
import org.openqa.selenium.json.JsonException;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class SeleniumRetryAnalyzer implements AdditionalRetryAnalyzer {

    final String[] jsonExceptionMessages = {
            "Expected to read a START_MAP but instead have: END",
    };

    @Override
    public Optional<Throwable> analyzeThrowable(Throwable throwable) {
        if (throwable instanceof JsonException) {
            String message = throwable.getMessage();
            if (message != null) {
                String messageLc = message.toLowerCase();
                if (Arrays.stream(jsonExceptionMessages).anyMatch(m -> messageLc.contains(m.toLowerCase()))) {
                    return Optional.of(throwable);
                }
            }
        } else if (throwable instanceof UnreachableBrowserException) {
            return Optional.of(throwable);
        }
        return Optional.empty();
    }
}
