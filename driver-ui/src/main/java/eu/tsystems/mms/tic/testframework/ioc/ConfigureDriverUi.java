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

package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.hooks.DriverUiHook;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultElementLabelLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultTestControllerOverrides;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultUiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultUiElementFinderFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFinderFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementLabelLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultPropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.webdriver.DefaultWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverManager;

public class ConfigureDriverUi extends AbstractModule {
    protected void configure() {
        // Singletons
        bind(UiElementFactory.class).to(DefaultUiElementFactory.class).in(Scopes.SINGLETON);
        bind(PageFactory.class).to(DefaultPageFactory.class).in(Scopes.SINGLETON);
        bind(TestController.Overrides.class).to(DefaultTestControllerOverrides.class).in(Scopes.SINGLETON);
        bind(IWebDriverManager.class).to(DefaultWebDriverManager.class).in(Scopes.SINGLETON);
        bind(UiElementLabelLocator.class).to(DefaultElementLabelLocator.class).in(Scopes.SINGLETON);
        bind(UiElementFinderFactory.class).to(DefaultUiElementFinderFactory.class).in(Scopes.SINGLETON);
        bind(PropertyAssertionFactory.class).to(DefaultPropertyAssertionFactory.class).in(Scopes.SINGLETON);

        Multibinder<ModuleHook> hookBinder = Multibinder.newSetBinder(binder(), ModuleHook.class);
        hookBinder.addBinding().to(DriverUiHook.class).in(Scopes.SINGLETON);
    }
}
