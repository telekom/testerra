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
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.annotations.Test;

/**
 * Created on 04.05.2022
 *
 * @author mgn
 */
public class UiElementPresentTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void test_inexistent_UiElement_present() {
        WebTestPage page = getPage();
        page.inexistentElement().expect().present().is(false);
    }

    @Test
    public void test_inexistent_UiElement_present_fails() {
        WebTestPage page = getPage();
        String msg = null;
        try {
            page.inexistentElement().expect().present().is(true);
        } catch (AssertionError e) {
            msg = e.getMessage();
        }
        ASSERT.assertEndsWith(msg, "is true", AssertionError.class.toString());
    }

    @Test
    public void test_inexistent_UiElement_present_fails_fast() {
        CONTROL.withTimeout(0, this::test_inexistent_UiElement_present_fails);
    }


}
