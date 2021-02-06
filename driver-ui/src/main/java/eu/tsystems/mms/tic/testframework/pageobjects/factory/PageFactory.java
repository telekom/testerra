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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.WebDriver;

/**
 * @deprecated Use {@link eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory} instead
 */
@Deprecated
public final class PageFactory {

    private static eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory pageFactory = Testerra.getInjector().getInstance(eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory.class);

    private PageFactory() {

    }

    /**
     * @deprecated Use {@link eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory#setGlobalPagePrefix(String)} instead
     */
    public static void setGlobalPagesPrefix(String prefix) {
        pageFactory.setGlobalPagePrefix(prefix);
    }

    /**
     * @deprecated Use {@link eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory#setThreadLocalPagePrefix(String)} instead
     */
    @Deprecated
    public static void setThreadLocalPagesPrefix(String threadLocalPagesPrefix) {
        pageFactory.setThreadLocalPagePrefix(threadLocalPagesPrefix);
    }

    @Deprecated
    public static void clearThreadLocalPagesPrefix() {
        pageFactory.removeThreadLocalPagePrefix();
    }

    /**
     * @deprecated Use {@link eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory#createPageWithCheckRule(Class, WebDriver, CheckRule)} instead
     */
    @Deprecated
    public static <T extends Page> T checkNot(Class<T> pageClass, WebDriver driver) {
        return pageFactory.createPageWithCheckRule(pageClass, driver, CheckRule.IS_NOT_DISPLAYED);
    }

    /**
     * @deprecated Use {@link eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory#createPage(Class, WebDriver)} instead
     */
    @Deprecated
    public static <T extends Page> T create(Class<T> pageClass, WebDriver driver) {
        return pageFactory.createPage(pageClass, driver);
    }

    @Deprecated
    public static void clearCache() {
        ClassFinder.clearCache();
    }
}
