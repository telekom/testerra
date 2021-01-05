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

package eu.tsystems.mms.tic.testframework.webdriver;

import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import java.util.List;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public interface IWebDriverFactory {
    /**
     * Use {@link #createWebDriver(WebDriverRequest, SessionContext)} instead
     */
    @Deprecated
    EventFiringWebDriver getWebDriver(WebDriverRequest r, SessionContext sessionContext);
    default EventFiringWebDriver createWebDriver(WebDriverRequest request, SessionContext sessionContext) {
        return getWebDriver(request, sessionContext);
    }

    List<String> getSupportedBrowsers();
    default boolean isBrowserSupported(String browser) {
        return getSupportedBrowsers().indexOf(browser) >= 0;
    }

    GuiElementCore createCore(GuiElementData guiElementData);
    default GuiElementCore createCoreFromParent(GuiElementData parent, Locator locator) {
        return createCore(new GuiElementData(parent, locator));
    }
}
