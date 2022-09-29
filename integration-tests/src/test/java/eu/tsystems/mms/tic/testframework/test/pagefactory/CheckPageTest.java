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
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsDisplayed;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsNotDisplayed;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsNotPresent;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckRuleIsPresent;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCollectedAndOptionalElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCollectedElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithOptionalElement;
import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test(priority = 1)
    @Fails(description = "The test itself passes, but collected assertions will always fail")
    public void testT11_checkCollectedElements_IsNotPresent() {
        PAGE_FACTORY.createPage(PageWithCollectedElement.class, getClassExclusiveWebDriver());
    }

    @Test(priority = 2)
    public void testT11a_checkCollectedElements_verifyResult() {
        IExecutionContextController instance = Testerra.getInjector().getInstance(IExecutionContextController.class);
        List<String> errorList = ((ClassContext) instance.getCurrentMethodContext().get().getParentContext())
                .readMethodContexts()
                .filter(context -> "testT11_checkCollectedElements_IsNotPresent".equals(context.getName()))
                .flatMap(MethodContext::readErrors)
                .map(context -> context.getThrowable().getMessage())
                .collect(Collectors.toList());
        Assert.assertEquals(errorList.size(), 2, "Method context should contains 2 errors.");

        Assert.assertTrue(errorList.contains("Expected that PageWithCollectedElement -> collectedElement1 displayed is true"));
        Assert.assertTrue(errorList.contains("Expected that PageWithCollectedElement -> collectedElement2 displayed is true"));
    }

    @Test
    public void testT12_checkCollectedAndOptionalElements_IsNotPresent() {
        // The used page is bad practice, but possible:
        // In case of 'optional' AND 'collected' elements the 'optional' wins.
        PAGE_FACTORY.createPage(PageWithCollectedAndOptionalElement.class, getClassExclusiveWebDriver());
    }

}
