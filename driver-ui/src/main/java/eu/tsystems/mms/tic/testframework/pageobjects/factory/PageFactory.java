/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.ResponsiveClassFinder;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.openqa.selenium.WebDriver;

/**
 * @deprecated Use {@link eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory} instead
 */
@Deprecated
public final class PageFactory implements PageFactoryProvider {

    private PageFactory() {

    }

    @Deprecated
    public static void setGlobalPagesPrefix(String prefix) {
        PAGE_FACTORY.setGlobalPagesPrefix(prefix);
    }

    @Deprecated
    public static void setThreadLocalPagesPrefix(String threadLocalPagesPrefix) {
        PAGE_FACTORY.setThreadLocalPagesPrefix(threadLocalPagesPrefix);
    }

    @Deprecated
    public static void clearThreadLocalPagesPrefix() {
        PAGE_FACTORY.clearThreadLocalPagesPrefix();
    }

    @Deprecated
    public static <T extends Page> T checkNot(Class<T> pageClass, WebDriver driver) {
        return PAGE_FACTORY.createPageWithCheckRule(pageClass, driver, CheckRule.IS_NOT_DISPLAYED);
    }

    @Deprecated
    public static <T extends Page> T create(Class<T> pageClass, WebDriver driver) {
        return PAGE_FACTORY.createPage(pageClass, driver);
    }

    @Deprecated
    public static void clearCache() {
        ResponsiveClassFinder.clearCache();
    }
}
