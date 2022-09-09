/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsDisplayed;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsNotDisplayed;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsNotPresent;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsPresent;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithOptionalElement;
import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.testng.annotations.Test;

public class CheckPageTest extends AbstractTestSitesTest implements PageFactoryProvider {

    @Test
    public void testT01_checkExistingElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithExistingElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = PageFactoryException.class)
    public void testT02_checkNotExistingElement() throws Throwable {
        PAGE_FACTORY.createPage(PageWithNotExistingElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testT03_checkNullElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithNullElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testT04_checkStaticElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithExistingStaticElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testT05_checkNonCheckableElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithNonCheckableCheck.class, getClassExclusiveWebDriver());
    }

    @Test()
    public void testT06_checkCheckRule_IsNotDislayed() {
        PAGE_FACTORY.createPage(PageWithCheckRuleIsNotDisplayed.class, getClassExclusiveWebDriver());
    }

    @Test
    public void testT07_checkCheckRule_IsPresent() {
        PAGE_FACTORY.createPage(PageWithCheckRuleIsPresent.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = PageFactoryException.class)
    public void testT08_checkCheckRule_IsDisplayed() {
        PAGE_FACTORY.createPage(PageWithCheckRuleIsDisplayed.class, getClassExclusiveWebDriver());
    }

    @Test()
    public void testT09_checkCheckRule_IsNotPresent() {
        PAGE_FACTORY.createPage(PageWithCheckRuleIsNotPresent.class, getClassExclusiveWebDriver());
    }

    @Test
    public void testT10_checkOptionalElement_IsNotPresent() {
        PAGE_FACTORY.createPage(PageWithOptionalElement.class, getClassExclusiveWebDriver());
    }

}
