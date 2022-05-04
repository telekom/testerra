/*
 * Testerra
 *
 * (C) 2022, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.UiElementAssertion;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.annotations.Test;

/**
 * Created on 04.05.2022
 *
 * @author mgn
 */
public class UiElementExpectAttributeTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void testT01_UiElement_disabled_attribute_present() {
        WebTestPage page = getPage();
        UiElementAssertion expect = page.getRadioBtn().expect();
        StringAssertion<String> disabled = expect.attribute(Attribute.DISABLED);
        disabled.isNot(null);
        disabled.isNot(false);
        disabled.isNot(Boolean.FALSE);
        disabled.isNot("false");
        disabled.is(true);
        disabled.is(Boolean.TRUE);
        disabled.is("true");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_UiElement_attribute_present_fails() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute(Attribute.DISABLED).is(null);
    }

    @Test
    public void testT03_UiElement_inexistent_attribute_present() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute("not-existent-attribute").is(null);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT04_UiElement_inexistent_attribute_present_fails() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute("not-existent-attribute").isNot(null);
    }

    @Test
    public void testT05_UiElement_inexistent_attribute_mapped() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute("not-existent-attribute").map(String::trim).is(null);
    }

    @Test
    public void testT06_UiElement_existent_attribute_contains_text_true() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute(Attribute.NAME).contains("radioBtn").is(true);
    }

    @Test
    public void testT07_UiElement_existent_attribute_isContaining_text() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute(Attribute.NAME).isContaining("radioBtn");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT08_UiElement_existent_attribute_isNotContaining_text() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute(Attribute.NAME).isNotContaining("radioBtn");
    }

    //    @Test
//    public void test_Attributes() {
//        WebTestPage page = getPage();
//        UiElement attributes = page.getFinder().findByQa("section/attributeTest");
//
//        //attributes.value("ariaExpanded").is("true");
//        attributes.expectThat().value("aria-expanded").is("true");
//        //attributes.value("dataCompletelyCustomAttribute").is("true");
//        attributes.expectThat().value("data-completely-custom-attribute").is("yes");
//    }
}
