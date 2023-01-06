/*
 * Testerra
 *
 * (C) 2023, Sebastian Kiehne, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.annotations.Test;

/**
 * Created on 06.01.2023
 *
 * @author sbke
 */
public class UiElementExpectTextTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    private String websiteText = "Small text";

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void testT01_UiElement_contains_text_ignore_case() {
        TestableUiElement textElement = getPage().smallTextElement();
        textElement.expect().text().withIgnoreCase().contains(websiteText).is(true);
        textElement.expect().text().withIgnoreCase().contains(websiteText.toLowerCase()).is(true);
        textElement.expect().text().withIgnoreCase().contains(websiteText.toUpperCase()).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_UiElement_contains_text_ignore_case_fail() {
        TestableUiElement textElement = getPage().smallTextElement();
        textElement.expect().text().withIgnoreCase().contains(websiteText + "fail").is(true);
    }


    @Test
    public void testT03_UiElement_startsWith_text_ignore_case() {
        TestableUiElement textElement = getPage().smallTextElement();
        textElement.expect().text().withIgnoreCase().startsWith(websiteText.substring(0, 4)).is(true);
        textElement.expect().text().withIgnoreCase().startsWith(websiteText.substring(0, 4).toLowerCase()).is(true);
        textElement.expect().text().withIgnoreCase().startsWith(websiteText.substring(0, 4).toUpperCase()).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT04_UiElement_startsWith_text_ignore_case_fail() {
        TestableUiElement textElement = getPage().smallTextElement();
        textElement.expect().text().withIgnoreCase().startsWith(("fail" + websiteText).substring(0, 4)).is(true);
    }

    @Test
    public void testT05_UiElement_endsWith_text_ignore_case() {
        TestableUiElement textElement = getPage().smallTextElement();
        textElement.expect().text().withIgnoreCase().endsWith(websiteText.substring(7)).is(true);
        textElement.expect().text().withIgnoreCase().endsWith(websiteText.substring(7).toLowerCase()).is(true);
        textElement.expect().text().withIgnoreCase().endsWith(websiteText.substring(7).toUpperCase()).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT06_UiElement_endsWith_text_ignore_case_fail() {
        TestableUiElement textElement = getPage().smallTextElement();
        textElement.expect().text().withIgnoreCase().endsWith((websiteText + "fail").substring(7)).is(true);
    }
}
