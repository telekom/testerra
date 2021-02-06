/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.test;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import org.testng.annotations.Test;

public class AssertionFormatTests extends AbstractExclusiveTestSitesTest<WebTestPage> {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > @url=\"http://localhost/Input/input.html\" > endsWith\\(\"nonexistingfile.html\"\\) is true")
    public void test_Page_url_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.expect().url().endsWith("nonexistingfile.html").is(true));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that URL ends with proper file name is true")
    public void test_Page_url_subject_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.expect().url().endsWith("nonexistingfile.html").is(true, "URL ends with proper file name"));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > @title=\"Input test\" > startsWith\\(\"Hallo\"\\) is true")
    public void test_Page_title_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.expect().title().startsWith("Hallo").is(true));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > UiElement\\(By.id: notDisplayedElement\\) > displayed is true")
    public void test_UiElement_displayed_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.notDisplayedElement().expect().displayed(true));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > UiElement\\(By.id: notDisplayedElement\\) > css \\{ display: none \\} > contains\\(\"block\"\\) is true")
    public void test_UiElement_css_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.notDisplayedElement().expect().css("display").contains("block").is(true));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > @title=\"Input test\" > length \\[10\\] is between \\[3000\\] and \\[1\\]")
    public void test_Page_title_length_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.expect().title().length().isBetween(3000,1));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > UiElement\\(By.id: notDisplayedElement\\) > style=\"display: none;\" > endsWith\\(\"block\"\\) is true")
    public void test_UiElement_value_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.notDisplayedElement().expect().attribute("style").endsWith("block").is(true));
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that WebTestPage > inputForm\\(UiElement\\(By.className: box\\)\\) > button\\(By.className: component-btn\\) > value=\"Button1\" equals \\[Glickmisch\\]")
    public void test_Component_text_format() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> page.inputForm().button().expect().value("Glickmisch"));
    }
}
