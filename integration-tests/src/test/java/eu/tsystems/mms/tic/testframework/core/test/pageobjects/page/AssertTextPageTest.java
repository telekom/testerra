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

import eu.tsystems.mms.tic.testframework.core.test.pageobjects.IPageFactoryTest;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestFramedPage;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

/**
 * Created by rnhb on 19.06.2015.
 *
 * Tests for correct execution of checkpage().
 * To test that checkpage() is executed, a not existing, check-annotated element is used.
 *
 */
public class AssertTextPageTest extends AbstractAssertTextPageTest implements IPageFactoryTest {

    @Override
    public WebTestFramedPage getFramePage() {
        return new WebTestFramedPage(WebDriverManager.getWebDriver());
    }
}
