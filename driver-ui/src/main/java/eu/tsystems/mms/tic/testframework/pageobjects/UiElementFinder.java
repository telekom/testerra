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

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementLabelLocator;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;
import org.openqa.selenium.By;

/**
 * Interface for finding {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementFinder extends LocatorFactoryProvider, Loggable, WebDriverRetainer {
    UiElement find(Locator locator);
    default UiElement findById(Object id) {
        return find(Locate.by(By.id(id.toString())));
    }
    default UiElement findByQa(String qa) {
        return find(Locate.byQa(qa));
    }
    default UiElement find(By by) {
        return find(Locate.by(by));
    }
    default UiElement find(XPath xPath) {
        return find(Locate.by(xPath));
    }
    default UiElement findByLabel(String element, String label) {
        return findDeep(Testerra.getInjector().getInstance(UiElementLabelLocator.class).createLocator(element, label));
    }

    default UiElement findDeep(Locator locator) {
        UiElement currentScope = find(locator);
        if (currentScope.waitFor().numberOfElements().getActual() > 0) {
            return currentScope;
        }

        UiElement frames = find(By.xpath("(//iframe|//frame)"));
        for (UiElement frame : frames.list()) {
            UiElement deepScope = frame.findDeep(locator);
            if (deepScope.waitFor().numberOfElements().getActual() > 0) {
                return deepScope;
            }
        }
        return currentScope;
    }
}
