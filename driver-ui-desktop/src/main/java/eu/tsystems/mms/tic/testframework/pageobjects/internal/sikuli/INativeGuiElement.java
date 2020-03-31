/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
/*
* Created on 26.08.2014
*
* Copyright(c) 1995 - 2014 T-Systems Multimedia Solutions GmbH
* Riesaer Str. 5, 01129 Dresden
* All rights reserved.
*/

package eu.tsystems.mms.tic.testframework.pageobjects.internal.sikuli;

import eu.tsystems.mms.tic.testframework.pageobjects.location.OffsetBasePoint;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;

import java.awt.*;
import java.awt.event.InputEvent;
import java.net.URL;

/**
 *
 * This Interface guarantees that all NativeGuiElements class implements the same methods.
 *
 * @param <T> the NativeGuiElement class that the constructor methods shall return
 *
 * @author fakr
 */

public interface INativeGuiElement<T> {

    /**
     * Constructor-method to instantiate with a resource file, searching only in the region of this element.
     *
     * @param resourceFile Location of image in the resource folder.
     * @return New NativeGuiElement Instance
     */
    T getSubElement(String resourceFile);

    /**
     * Constructor-method to instantiate with an url, searching only in the region of this element.
     *
     * @param url Locator for the image.
     * @return New NativeGuiElement Instance
     */
    T getSubElement(URL url);

    /**
     * Method to create a new 1x1 pixels big NativeGuiElement based on another element and an offset.
     * This allows for precise actions in a larger element, instead of always using its center.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this NativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this NativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     * @return A new NativeGuiElement of size 1x1.
     */
    T getNewElementByOffset(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint);

    /**
     * Changes the point that is used for interaction with this element.
     * This way, for example a clicks can be permanently directed to specific locations in this element.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this NativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this NativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     */
    void setRelativeHandlePoint(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint);

    /**
     * Calculates an offset point based on a offsetBasePoint.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this NativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this NativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     * @return The location set off by the parameters.
     */
    //ScreenLocation getOffsetLocation(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint);

    /**
     * Mainly for debug purposes, this get all matches of this elements target in its screenregion
     * and save them image to the given path. In general the first of these elements is the best one
     * and will be actually used.
     *
     * @param path Path to store the images in.
     */
    void saveAllRegionMatchesToPath(String path);

    /**
     * Get the Screen Region is element is located in.
     *
     * @return The region this element is located in.
     */
    ScreenRegion getScreenRegion();

    /**
     * Get the ScreenRegion that is defined as this element.
     *
     * @return The region of this element
     */
    ScreenRegion getElementRegion();

    /**
     * Get the Size of the Element.
     *
     * @return Size of the element
     */
    Dimension getSize();

    /**
     * Get the Target of the Element
     *
     * @return the Target of this element
     */
    Target getTarget();

    /**
     * Find the element with its target
     */
    void find();

    /**
     * Find the element. Do not matter if it is already found
     */
    void forceFind();

    /**
     * Perform a click into the center with AWT.
     */
    void click();

    /**
     * Click with the specified Button.
     *
     * @param button the button to click
     */
    void click(MouseButton button);

    /**
     * Perform a DoubleClick into the center with AWT.
     */
    void doubleClick();

    /**
     * DoubleClick with the specified Button.
     *
     * @param button the button to click
     */
    void doubleClick(MouseButton button);

    /**
     * Type the given text with AWT. Please note that there might be problems with special
     * characters. If you have trouble with this, use paste.
     *
     * @param text The text to type
     */
    void type(final String text);

    /**
     * Paste the given text into the element.
     *
     * @param text The text to paste
     */
    void paste(final String text);

    /**
     * A fast left click for internal purposes.
     */
    //void fastLeftClick();

    /**
     * Returns the text of the block that is in th center of this element.
     * Only works if the text is selectable, not with text in an image.
     * The returned text is the whole paragraph, possibly more than visible in the image.
     *
     * @return Textblock of the center of the image.
     */
    String getSelectableText();

    /**
     * Get the handlePoint of this element.
     *
     * @return The point in the element on which actions are performed.
     */
    ScreenLocation getLocation();

    /**
     * Drag and drop this element onto another element.
     *
     * @param targetElement The element on which this element should be dropped
     */
    void dragAndDropToElement(INativeGuiElement targetElement);

    /**
     * Drag and drop this element onto another element.
     *
     * @param targetElement The element on which this element should be dropped
     * @param button        The button which should be pressed
     */
    void dragAndDropToElement(INativeGuiElement targetElement, MouseButton button);

    /**
     * Drag this element and drop it on the given absolute location.
     *
     * @param x absolute x-coordinate
     * @param y absolute y-coordinate
     */
    void dragAndDropToScreenLocation(int x, int y);

    /**
     * Drag this element and drop it on the given absolute location, using the given MouseButton.
     *
     * @param x      absolute x-coordinate
     * @param y      absolute y-coordinate
     * @param button The button which should be pressed
     */
    void dragAndDropToScreenLocation(int x, int y, MouseButton button);

    /**
     * Drag this element and drop it to the given, relative location.
     *
     * @param x relative x-coordinate
     * @param y relative y-coordinate
     */
    void dragAndDropRelative(int x, int y);

    /**
     * Drag this element with the give MouseButton and drop it to the given, relative location.
     *
     * @param x      relative x-coordinate
     * @param y      relative y-coordinate
     * @param button The button which should be pressed
     */
    void dragAndDropRelative(int x, int y, MouseButton button);

    /**
     * Drag and Drop method
     *
     * @param startX x-coordinate to start the drag from
     * @param startY y-coordinate to start the drag from
     * @param endX   x-coordinate to endTime the drag at
     * @param endY   y-coordinate to endTime the drag at
     * @param button the button to do the drag with
     */
    void dragAndDrop(int startX, int startY, int endX, int endY, MouseButton button);

    /**
     * Hover the cursor over this element.
     */
    void hover();

    /**
     * Checks whether NativeGuiElement is present (tries to find element)
     *
     * @return true if element is present else false
     */
    boolean isPresent();

    /**
     * Assert that element is present
     */
    void assertIsPresent();

    /**
     * Assert that element is not present
     */
    void assertIsNotPresent();

    /**
     * Waits for a time-span until an element is present
     *
     * @param timeoutSeconds time delay the method is waiting
     * @return true if element is present after the given time delay
     */
    boolean waitForIsPresent(int timeoutSeconds);

    /**
     * Waits a certain period seconds until an element is present (class-target used)
     *
     * @return true if element is present else it returns false
     */
    boolean waitForIsPresent();

    /**
     * Waits until an element is not present (class-target used)
     *
     * @return true if element is not present else it returns false
     */
    boolean waitForIsNotPresent();

    /**
     * Waits for a certain number of seconds until an element is not present
     *
     * @param seconds the time delay the method is waiting
     * @return true if element is not present else it returns false
     */
    boolean waitForIsNotPresent(int seconds);

    /**
     * MouseButtons
     */
    public enum MouseButton {
        /** left mouse button */
        LEFT(InputEvent.BUTTON1_DOWN_MASK),
        /** right mouse button */
        RIGHT(InputEvent.BUTTON3_DOWN_MASK),
        /** middle mouse button */
        MIDDLE(InputEvent.BUTTON2_DOWN_MASK);

        private int inputEvent;

        public int getInputEvent() {
            return inputEvent;
        }

        /**
         * Constructor for MouseButton
         *
         * @param inputEvent represents a mouse button from class InputEvent
         */
        MouseButton(int inputEvent) {
            this.inputEvent = inputEvent;
        }
    }

    String toString();

}
