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
package eu.tsystems.mms.tic.testframework.core.playground.webdriver;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.Test;

/**
 * Created by pele on 09.07.2014.
 */
public class BrowserTest extends AbstractTest {

    @Test
    public void testChrome() throws Exception {
        WebDriverManager.config().browser = Browsers.chrome;
        WebDriverManager.setGlobalExtraCapability("nativeEvents", false); // todo: seems to have no effect :(
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.findElement(By.xpath("//huhu"));
    }

    @Test
    public void testFirefoxWithProfile() throws Exception {
        WebDriverManager.config().browser = Browsers.firefox;
        WebDriverManager.setGlobalExtraCapability(FirefoxDriver.PROFILE, new FirefoxProfile());
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.findElement(By.xpath("//huhu"));
    }


}
