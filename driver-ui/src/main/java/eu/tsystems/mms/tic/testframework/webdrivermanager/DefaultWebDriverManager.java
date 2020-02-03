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
/*
 * Created on 09.01.2012
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import org.openqa.selenium.WebDriver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultWebDriverManager implements IWebDriverManager {
    private final Map<WebDriver, TimerWrapper>TIMER_WRAPPERS = new ConcurrentHashMap<>();

    public TimerWrapper getTimerWrapper(WebDriver webDriver) {
        if (!TIMER_WRAPPERS.containsKey(webDriver)) {
            TIMER_WRAPPERS.put(webDriver, new TimerWrapper(webDriver));
        }
        return TIMER_WRAPPERS.get(webDriver);
    }

    @Override
    public WebDriver getWebDriver() {
        return WebDriverManager.getWebDriver();
    }
}
