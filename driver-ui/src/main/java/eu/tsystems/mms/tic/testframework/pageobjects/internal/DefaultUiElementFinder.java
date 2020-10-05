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

package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import org.openqa.selenium.WebDriver;

/**
 * Default implementation of {@link UiElementFinder}
 * @author Mike Reiche
 */
public class DefaultUiElementFinder implements UiElementFinder {
    private static final UiElementFactory uiElementFactory = Testerra.injector.getInstance(UiElementFactory.class);
    private final WebDriver webDriver;

    public DefaultUiElementFinder(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public UiElement find(Locator locator) {
        return uiElementFactory.createWithWebDriver(this.webDriver, locator);
    }
}
