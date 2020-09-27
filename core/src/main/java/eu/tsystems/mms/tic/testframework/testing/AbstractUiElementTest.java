/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinderFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.WebDriverRetainer;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFinder;

/**
 * Base class for tests that need {@link UiElement} only.
 * @author Mike Reiche
 */
public abstract class AbstractUiElementTest extends TesterraTest implements UiElementFinderProvider, WebDriverRetainer {

    private UiElementFinder uiElementFinder;

    @Override
    public UiElementFinder getUiElementFinder() {
        if (uiElementFinder == null) {
            UiElementFinderFactory factory = Testerra.injector.getInstance(UiElementFinderFactory.class);
            uiElementFinder = factory.create(getWebDriver());
        }
        return uiElementFinder;
    }
}
