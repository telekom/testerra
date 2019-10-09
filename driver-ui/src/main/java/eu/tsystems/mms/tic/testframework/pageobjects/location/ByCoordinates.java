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
/*
 * Created on 10.02.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects.location;

import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ByCoordinates extends By {

    private final int x;
    private final int y;
    private WebDriver driver;

    /**
     * checks driver for inequality and decides if its used or webdriver
     *
     * @param driver .
     * @param x .
     * @param y .
     */
    public ByCoordinates(WebDriver driver, int x, int y) {
        if (driver != null) {
            this.driver = driver;
        }
        else {
            this.driver = WebDriverManager.getWebDriver();
        }

        this.x = x;
        this.y = y;
    }

    /**
     *
     *
     * @param x .
     * @param y .
     */
    public ByCoordinates(int x, int y) {
        this(null, x, y);
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        WebElement elementByLocation = WebDriverUtils.findElementByLocation(driver, x, y);

        List<WebElement> webElements = new ArrayList<WebElement>(1);
        if (elementByLocation != null) {
            webElements.add(elementByLocation);
        }
        return webElements;
    }
}
