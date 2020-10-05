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

package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.pageobjects.InteractiveUiElement;
import org.openqa.selenium.WebElement;

/**
 * Fluent interface for all actions that can be performed on an interactive {@link WebElement}
 * like button, select, input etc...
 * @author Mike Reiche
 */
public interface InteractiveWebElementActions {
    /**
     * Select/Deselect a selectable element.
     */
    default InteractiveUiElement select(boolean select) {
        if (select) {
            return select();
        } else {
            return deselect();
        }
    }

    InteractiveUiElement click();
    InteractiveUiElement doubleClick();
    InteractiveUiElement contextClick();
    InteractiveUiElement select();
    InteractiveUiElement deselect();
    InteractiveUiElement sendKeys(CharSequence... charSequences);
    InteractiveUiElement type(String text);
    InteractiveUiElement clear();
    InteractiveUiElement hover();
    InteractiveUiElement submit();
}
