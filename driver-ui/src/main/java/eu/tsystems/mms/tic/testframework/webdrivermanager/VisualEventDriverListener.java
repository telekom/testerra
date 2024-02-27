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
 *
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementHighlighter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.awt.Color;

/**
 * Highlights some WebDriver events in Demo Mode
 */
public class VisualEventDriverListener implements WebDriverListener {

    private UiElementHighlighter elementHighlighter = Testerra.getInjector().getInstance(UiElementHighlighter.class);

    // Current driver is additional needed because WebDriverListener methods of before/after element actions only get WebElement, but no current driver.
    public WebDriver driver;

    @Override
    public void beforeClick(WebElement element) {
        if (element != null && Testerra.Properties.DEMO_MODE.asBool()) {
            elementHighlighter.highlight(driver, element, new Color(0, 0, 255));
        }
    }

    @Override
    public void beforeClear(WebElement element) {
        if (element != null && Testerra.Properties.DEMO_MODE.asBool()) {
            elementHighlighter.highlight(driver, element, new Color(0, 0, 255));
        }
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        if (element != null && Testerra.Properties.DEMO_MODE.asBool()) {
            elementHighlighter.highlight(driver, element, new Color(0, 0, 255));
        }
    }

}
