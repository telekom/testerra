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

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.testng.annotations.Test;

public class AssertionFormatTests extends AbstractTestSitesTest implements Loggable, PageFactoryTest {

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class, getClassExclusiveWebDriver());
    }

    @Test
    public void test_Page_url_format() {
        WebTestPage page = getPage();
        try {
            Control.withTimeout(0, () -> page.expect().url().endsWith("nonexistingfile.html").is(true));
        } catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "Expected that WebTestPage > @url=\"http://localhost/Input/input.html\" > endsWith(\"nonexistingfile.html\") is true");
        }
    }

    @Test()
    public void test_Page_url_subject_format() {
        WebTestPage page = getPage();
        try {
            Control.withTimeout(0, () -> page.expect().url().endsWith("nonexistingfile.html").is(true, "URL ends with proper file name"));
        } catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "Expected that URL ends with proper file name is true");
        }
    }

    @Test()
    public void test_Page_url_length_fails() {
        WebTestPage page = getPage();
        try {
            Control.withTimeout(0, () -> page.expect().title().startsWith("Hallo").is(true));
        } catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "Expected that WebTestPage > @title=\"Input test\" > startsWith(\"Hallo\") is true");
        }
    }
}
