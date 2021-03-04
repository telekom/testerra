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
 package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.ClassFinder;
import org.openqa.selenium.WebDriver;

/**
 * A page factory that supports the instantiation of responsive page objects
 */
public class ResponsivePageFactory extends DefaultPageFactory {
    @Override
    public <T extends Page> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver) {
        return ClassFinder.getBestMatchingClass(pageClass, webDriver, getConfiguredPrefix());
    }

    @Override
    public PageFactory clearThreadLocalPagesPrefix() {
        ClassFinder.clearCache();
        return super.clearThreadLocalPagesPrefix();
    }
}
