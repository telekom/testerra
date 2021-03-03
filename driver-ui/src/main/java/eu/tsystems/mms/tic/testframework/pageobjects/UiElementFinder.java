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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;
import org.openqa.selenium.By;

/**
 * Interface for finding {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementFinder extends LocatorFactoryProvider, Loggable, WebDriverRetainer {
    UiElement find(Locator locator);
    default UiElement findById(Object id) {
        return find(LOCATE.by(By.id(id.toString())));
    }
    default UiElement findByQa(String qa) {
        return find(LOCATE.byQa(qa));
    }
    default UiElement find(By by) {
        return find(LOCATE.by(by));
    }
    default UiElement find(XPath xPath) {
        return find(LOCATE.by(xPath));
    }
    default UiElement findDeep(XPath xPath) {
        return findDeep(LOCATE.by(xPath));
    }
    default UiElement findDeep(Locator locator) {
        UiElement currentScope = find(locator);
        if (currentScope.waitFor().foundElements().getActual() > 0) {
            return currentScope;
        }

        UiElement frames = find(By.xpath("(//iframe|//frame)"));
        for (UiElement frame : frames.list()) {
            UiElement deepScope = frame.findDeep(locator);
            if (deepScope.waitFor().foundElements().getActual() > 0) {
                return deepScope;
            }
        }
        return currentScope;
    }

    /**
     * Returns an empty element
     */
    default UiElement createEmpty() {
        return createEmpty(LOCATE.by(By.tagName("empty")));
    }
    default UiElement createEmpty(Locator locator) {
        return new EmptyUiElement(getWebDriver(), locator);
    }
}
