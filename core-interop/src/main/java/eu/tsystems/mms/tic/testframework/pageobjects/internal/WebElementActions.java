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

import eu.tsystems.mms.tic.testframework.pageobjects.InteractiveUiElement;
import java.awt.Color;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Fluent interface for all actions that can be performed on a non-interactive {@link WebElement}
 * @author Mike Reiche
 */
public interface WebElementActions extends WebElementRetainer {
    /**
     * Centers the element in the viewport
     */
    default InteractiveUiElement scrollIntoView() {
        return scrollIntoView(new Point(0,0));
    }

    /**
     * Centers the element in the viewport with a given offset
     * @param offset
     */
    InteractiveUiElement scrollIntoView(Point offset);

    /**
     * Highlights the element with a specified color
     */
    InteractiveUiElement highlight(Color color);

    /**
     * Highlights the element with a default color
     */
    default InteractiveUiElement highlight() {
        return highlight(new Color(0,0, 255, 255));
    }
}
