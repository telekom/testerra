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

import java.awt.Color;
import org.openqa.selenium.Point;

/**
 * Actions for {@link GuiElementCore}
 */
public interface GuiElementCoreActions {

    default void select(boolean select) {
        if (select) {
            select();
        } else {
            deselect();
        }
    }

    void select();
    void deselect();
    void type(String text);
    void click();
    void submit();
    void sendKeys(CharSequence... charSequences);
    void clear();
    void highlight(Color color);
    void hover();

    void contextClick();

    @Deprecated
    default void rightClick() {
        contextClick();
    }

    @Deprecated
    default void mouseOver() {
        hover();
    }

    /**
     * Scroll to the position of this element.
     *
     * @return this.
     */
    @Deprecated
    default void scrollToElement() {
        scrollToElement(0);
    }

    @Deprecated
    void scrollToElement(final int yOffset);

    default void scrollIntoView() {
        scrollIntoView(new Point(0,0));
    }

    void scrollIntoView(Point offset);

    /**
     * doubleclick
     *
     * @return .
     */
    void doubleClick();

    /**
     * Swipe the element by the given offset. (0,0) should be the top left.
     *
     * @param offsetX horizontal offset in pixel.
     * @param offSetY vertical offset in pixel.
     */
    void swipe(final int offsetX, final int offSetY);
}
