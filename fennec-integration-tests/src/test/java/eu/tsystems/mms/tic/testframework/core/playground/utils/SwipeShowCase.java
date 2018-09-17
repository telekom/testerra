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
 * Created on 03.03.2016
 *
 * Copyright(c) 1995 - 2007 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.utils;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.utils.MouseActions;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * SwipeShowCase
 * <p>
 * Date: 03.03.2016
 * Time: 12:35
 *
 * @author erku
 */
public class SwipeShowCase {

    private WebDriver getDriver() {
        return WebDriverManager.getWebDriver();
    }

    @Test
    public void testT03_SwipeElement() {

        WebDriverManager.config().browser = Browsers.firefox;
        WebDriverManager.config().webDriverMode = WebDriverMode.remote;

        final WebDriver driver = this.getDriver();
        driver.manage().window().setSize(new Dimension(544, 627));
        String url = "http://wum62033.mms-dresden.de:8081/startseite/unterwegs/tarife/9033310/magentamobil.html";

        driver.get(url);

        GuiElement elmToSwipe = new GuiElement(driver, By.xpath("//a[contains(text(), 'Ohne Endgerät')]"))
                .withWebElementFilter(WebElementFilter.DISPLAYED.is(true));

        elmToSwipe.highlight();
        elmToSwipe.assertIsDisplayed();
        MouseActions.swipeElement(elmToSwipe, -122, 0);
        elmToSwipe.assertIsNotDisplayed();
    }

}
