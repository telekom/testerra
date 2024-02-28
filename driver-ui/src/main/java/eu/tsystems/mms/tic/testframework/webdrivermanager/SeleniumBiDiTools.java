/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.LogInspector;
import org.openqa.selenium.bidi.Network;
import org.openqa.selenium.bidi.log.LogEntry;
import org.openqa.selenium.bidi.network.ResponseDetails;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created on 2024-02-28
 *
 * @author mgn
 */
public interface SeleniumBiDiTools extends BiDiTools {

    Network getNetwork(WebDriver webDriver);

    LogInspector getLogInsepctor(WebDriver webDriver);

    default void registerForResponseCompleted(WebDriver webDriver, Consumer<ResponseDetails> consumer) {
        this.getNetwork(webDriver).onResponseCompleted(consumer);
    }

    default void registerGenericLogListener(WebDriver webDriver, Consumer<LogEntry> consumer) {
        this.getLogInsepctor(webDriver).onLog(consumer);
    }

    default List<String> getSupportedBrowsers() {
        return List.of(Browsers.chrome, Browsers.chromeHeadless, Browsers.firefox, Browsers.edge);
    }
}
