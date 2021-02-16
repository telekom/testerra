/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */

package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.testng.annotations.BeforeClass;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractExclusiveTestSitesTest<T extends Page> extends AbstractTestSitesTest implements PageFactoryTest, PageFactoryProvider {
    private T page;

    @BeforeClass
    public void createPage() {
        this.page = PageFactory.createPage(getPageClass(), getClassExclusiveWebDriver());
    }

    abstract public Class<T> getPageClass();

    @Override
    public T getPage() {
        return this.page;
    }
}
