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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.pageobjects.SubPageWithoutCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.SuperPageWithCheck;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 29.12.2015.
 *
 * Test that checkpage is not executed when called in the constructor of a super page
 */
public class CheckPageCallTest extends AbstractTest {

    /**
     * This tests that checkpage() called from the actual class of the instance is executed.
     */
    @Test(expectedExceptions = PageNotFoundException.class)
    public void testT01_checkPage_calledInActualInstanceClass() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        new SuperPageWithCheck(driver);
    }

    /**
     * This tests that checkpage() called from a super class of the actual class of the instance is not executed.
     */
    @Test
    public void testT02_checkPage_notCalledInSuperClass() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        new SubPageWithoutCheck(driver);
    }
}
