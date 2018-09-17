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
package eu.tsystems.mms.tic.testframework.hooks;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.DesktopGuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

public class DesktopDriverHook implements ModuleHook {

    private static final String[] browsers = {
            Browsers.safari,
            Browsers.ie,
            Browsers.chrome,
            Browsers.chromeHeadless,
            Browsers.edge,
            Browsers.firefox,
            Browsers.phantomjs
    };

    @Override
    public void init() {
        WebDriverManager.registerWebDriverFactory(new DesktopWebDriverFactory(), browsers);
        GuiElement.registerGuiElementCoreFactory(new DesktopGuiElementCoreFactory(), browsers);
    }

    @Override
    public void terminate() {
    }
}
