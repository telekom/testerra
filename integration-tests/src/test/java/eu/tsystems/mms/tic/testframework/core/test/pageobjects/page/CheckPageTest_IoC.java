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

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.IPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Created by rnhb on 29.12.2015.
 */
public class CheckPageTest_IoC extends AbstractTestSitesTest {

    private static final IPageFactory pageFactory = Testerra.ioc().getInstance(IPageFactory.class);

    @BeforeMethod
    public void before(Method method) {
        WebDriver webDriver = WebDriverManager.getWebDriver();
        webDriver.get(TestPage.INPUT_TEST_PAGE.getUrl());
    }

    @Test
    public void testT01_checkExistingElement() throws Exception {
        pageFactory.createPage(PageWithExistingElement.class);
    }

    @Test(expectedExceptions = PageNotFoundException.class)
    public void testT02_checkNotExistingElement() throws Exception {
        pageFactory.createPage(PageWithNotExistingElement.class);
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT03_checkNullElement() throws Exception {
        pageFactory.createPage(PageWithNullElement.class);
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT04_checkStaticElement() throws Exception {
        pageFactory.createPage(PageWithExistingStaticElement.class);
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT05_checkNonCheckableElement() throws Exception {
        pageFactory.createPage(PageWithNonCheckableCheck.class);
    }
}
