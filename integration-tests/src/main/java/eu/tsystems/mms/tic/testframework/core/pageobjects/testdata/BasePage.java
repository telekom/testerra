/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page base class for responsive page factory tests.
 */
public class BasePage extends Page implements UiElementFinder {
    /**
     * Constructor for existing sessions.
     *
     * @param driver The web driver.
     */
    public BasePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public UiElement find(Locator locator) {
        return super.find(locator);
    }

    @Override
    public UiElement findById(Object id) {
        return super.findById(id);
    }

    @Override
    public UiElement findByQa(String qa) {
        return super.findByQa(qa);
    }

    @Override
    public UiElement find(By by) {
        return super.find(by);
    }

    @Override
    public UiElement find(XPath xPath) {
        return super.find(xPath);
    }

    @Override
    public UiElement findDeep(Locator locator) {
        return super.findDeep(locator);
    }
}
