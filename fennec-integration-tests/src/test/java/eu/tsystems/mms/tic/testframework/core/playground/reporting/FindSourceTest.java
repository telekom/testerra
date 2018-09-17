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
 * Created on 02.05.2014
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 * 
 * @author pele
 */
public class FindSourceTest extends AbstractFindSourceTest {

    static {
        // local test with intellij
        System.setProperty(FennecProperties.MODULE_SOURCE_ROOT, "fennec-core-tests/src");
        System.setProperty(FennecProperties.ELEMENT_TIMEOUT_SECONDS, "5");
    }

    /**
     * T01_FindSource
     * <p/>
     * Description: T01 FindSource
     */
    @Test
    public void testT01_FindSource() {
        WebDriver driver = WebDriverManager.getWebDriver();
        // driver.get(WebTestPage.URL);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.gotoHell();
    }
}
