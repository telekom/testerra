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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DesktopGuiElementCoreFactory extends AbstractGuiElementCoreFactory implements GuiElementCoreFactory {

    @Override
    public GuiElementCore create(
        String browser,
        By by,
        WebDriver webDriver,
        GuiElementData guiElementData
    ) {
        GuiElementCore core;
        switch (browser) {
            case Browsers.safari:
            case Browsers.ie:
            case Browsers.chrome:
            case Browsers.chromeHeadless:
            case Browsers.edge:
            case Browsers.firefox:
            case Browsers.phantomjs:
                core = new DesktopGuiElementCore(by, webDriver, guiElementData);
                break;
            default: throw new TesterraSystemException("No factory for browser: " + browser);
        }

        return super.decorate(core, browser, by, webDriver, guiElementData);
    }
}
