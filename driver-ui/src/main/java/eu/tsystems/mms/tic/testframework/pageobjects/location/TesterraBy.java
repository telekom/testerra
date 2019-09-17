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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public abstract class TesterraBy extends By {

    /**
     * tt. by coordinates
     *
     * @param x .
     * @param y .
     * @return .
     */
    @Deprecated
    public static TesterraBy coordinates(final int x, final int y) {
        return new ByCoordinates(x, y);
    }

    /**
     * tt. by coordinates using webdriver
     * @param driver .
     * @param x .
     * @param y .
     * @return .
     */
    public static TesterraBy coordinates(final WebDriver driver, final int x, final int y) {
        return new ByCoordinates(driver, x, y);
    }

}
