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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.sikuli;

import eu.tsystems.mms.tic.testframework.pageobjects.location.OffsetBasePoint;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;

import java.awt.*;
import java.net.URL;

/**
 * This class is an default implementation for the IInternalNativeGuiElementInterface
 *
 * @author fakr
 */
public class DefaultNativeGuiElementDecorator implements IInternalNativeGuiElement {
    /**
     * the element the decorator decorates
     */
    protected IInternalNativeGuiElement decoratedElement;

    /**
     * the ScreenRegion the element is in
     */
    protected ScreenRegion screenRegion;

    /**
     * Constructor to set the decorated element
     *
     * @param nativeGuiElement the element to decorate
     * @param s ScreenRegion where the element is expected
     */
    public DefaultNativeGuiElementDecorator(IInternalNativeGuiElement nativeGuiElement, ScreenRegion s) {
        this.decoratedElement = nativeGuiElement;
        this.screenRegion = s;
    }

    /**
     * Constructor-method to instantiate with a resource file, searching only in the region of this element.
     *
     * @param resourceFile Location of image in the resource folder.
     * @return New NativeGuiElement Instance
     */

    public IInternalNativeGuiElement getSubElement(String resourceFile) {
        return decoratedElement.getSubElement(resourceFile);
    }

    /**
     * Constructor-method to instantiate with an url, searching only in the region of this element.
     *
     * @param url Locator for the image.
     * @return New NativeGuiElement Instance
     */
    @Override
    public IInternalNativeGuiElement getSubElement(URL url) {
        return decoratedElement.getSubElement(url);
    }

    /**
     * Method to create a new 1x1 pixels big NativeGuiElement based on another element and an offset.
     * This allows for precise actions in a larger element, instead of always using its center.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this NativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this NativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     * @return A new NativeGuiElement of size 1x1.
     */
    @Override
    public IInternalNativeGuiElement getNewElementByOffset(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint) {
        return decoratedElement.getNewElementByOffset(offsetX, offsetY, offsetBasePoint);
    }

    /**
     * Changes the point that is used for interaction with this element.
     * This way, for example a clicks can be permanently directed to specific locations in this element.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this NativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this NativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     */
    @Override
    public void setRelativeHandlePoint(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint) {
        decoratedElement.setRelativeHandlePoint(offsetX, offsetY, offsetBasePoint);
    }

    /**
     * Mainly for debug purposes, this get all matches of this elements target in its screenregion
     * and save them image to the given path. In general the first of these elements is the best one
     * and will be actually used.
     *
     * @param path Path to store the images in.
     */
    @Override
    public void saveAllRegionMatchesToPath(String path) {
        decoratedElement.saveAllRegionMatchesToPath(path);
    }

    /**
     * Get the Screen Region is element is located in.
     *
     * @return The region this element is located in.
     */
    @Override
    public ScreenRegion getScreenRegion() {
        return decoratedElement.getScreenRegion();
    }

    /**
     * Get the ScreenRegion that is defined as this element.
     *
     * @return The region of this element
     */
    @Override
    public ScreenRegion getElementRegion() {
        return decoratedElement.getElementRegion();
    }

    /**
     * Get the Size of the Element.
     *
     * @return Size of the element
     */
    @Override
    public Dimension getSize() {
        return decoratedElement.getSize();
    }

    /**
     * Get the target of the element
     *
     * @return target of the element
     */
    @Override
    public Target getTarget() {
        return decoratedElement.getTarget();
    }

    /**
     * Find the element with its target and the given timeout.
     */
    @Override
    public void find() {
        decoratedElement.find();
    }

    /**
     * Find the element. Ignore whether it is already found
     */
    public void forceFind() {
        decoratedElement.forceFind();
    }

    /**
     * Perform a click into the center with AWT.
     */
    @Override
    public void click() {
        decoratedElement.click();
    }

    /**
     * Click with the specified Button.
     *
     * @param button the button to click
     */
    @Override
    public void click(MouseButton button) {
        decoratedElement.click(button);
    }

    /**
     * Perform a DoubleClick into the center with AWT.
     */
    @Override
    public void doubleClick() {
        decoratedElement.doubleClick();
    }

    /**
     * DoubleClick with the specified Button.
     *
     * @param button the button to click
     */
    @Override
    public void doubleClick(MouseButton button) {
        decoratedElement.doubleClick(button);
    }

    /**
     * Type the given text with AWT. Please note that there might be problems with special
     * characters. If you have trouble with this, use paste.
     *
     * @param text The text to type
     */
    @Override
    public void type(String text) {
        decoratedElement.type(text);
    }

    /**
     * Paste the given text into the element.
     *
     * @param text The text to paste
     */
    @Override
    public void paste(String text) {
        decoratedElement.paste(text);
    }

    /**
     * A fast left click for internal purposes.
     */
    /*@Override
    public IInternalNativeGuiElement fastLeftClick() {
        return decoratedElement.fastLeftClick();
    }*/

    /**
     * Returns the text of the block that is in th center of this element.
     * Only works if the text is selectable, not with text in an image.
     * The returned text is the whole paragraph, possibly more than visible in the image.
     *
     * @return Textblock of the center of the image.
     */
    @Override
    public String getSelectableText() {
        return decoratedElement.getSelectableText();
    }

    /**
     * Get the handlePoint of this element.
     *
     * @return The point in the element on which actions are performed.
     */
    @Override
    public ScreenLocation getLocation() {
        return decoratedElement.getLocation();
    }

    /**
     * Drag and drop this element onto another element.
     *
     * @param targetElement The element on which this element should be dropped
     */
    @Override
    public void dragAndDropToElement(INativeGuiElement targetElement) {
        decoratedElement.dragAndDropToElement(targetElement);
    }

    /**
     * Drag and drop this element onto another element.
     *
     * @param targetElement The element on which this element should be dropped
     * @param button        the button to do the drag with
     */
    @Override
    public void dragAndDropToElement(INativeGuiElement targetElement, MouseButton button) {
        decoratedElement.dragAndDropToElement(targetElement, button);
    }

    /**
     * Drag this element and drop it on the given absolute location.
     *
     * @param x absolute x-coordinate
     * @param y absolute y-coordinate
     */
    @Override
    public void dragAndDropToScreenLocation(int x, int y) {
        decoratedElement.dragAndDropToScreenLocation(x, y);
    }

    /**
     * Drag this element and drop it on the given absolute location, using the given MouseButton.
     *
     * @param x      absolute x-coordinate
     * @param y      absolute y-coordinate
     * @param button the button to do the drag with
     */
    @Override
    public void dragAndDropToScreenLocation(int x, int y, MouseButton button) {
        decoratedElement.dragAndDropToScreenLocation(x, y, button);
    }

    /**
     * Drag this element and drop it to the given, relative location.
     *
     * @param x relative x-coordinate
     * @param y relative y-coordinate
     */
    @Override
    public void dragAndDropRelative(int x, int y) {
        decoratedElement.dragAndDropRelative(x, y);
    }

    /**
     * Drag this element with the give MouseButton and drop it to the given, relative location.
     *
     * @param x      relative x-coordinate
     * @param y      relative y-coordinate
     * @param button the button to do the drag with
     */
    @Override
    public void dragAndDropRelative(int x, int y, MouseButton button) {
        decoratedElement.dragAndDropRelative(x, y, button);
    }

    /**
     * @param startX x-coordinate to start the drag from
     * @param startY y-coordinate to start the drag from
     * @param endX   x-coordinate to endTime the drag at
     * @param endY   y-coordinate to endTime the drag at
     * @param button the button to do the drag with
     */
    @Override
    public void dragAndDrop(int startX, int startY, int endX, int endY, MouseButton button) {
        decoratedElement.dragAndDrop(startX, startY, endX, endY, button);
    }

    /**
     * Hover the cursor over this element.
     */
    @Override
    public void hover() {
        decoratedElement.hover();
    }

    /**
     * Checks whether NativeGuiElement is present (tries to find element)
     *
     * @return true if element is present else false
     */
    @Override public boolean isPresent() {
        return decoratedElement.isPresent();
    }

    /**
     * assert that element will become present
     */
    @Override public void assertIsPresent() {
        decoratedElement.assertIsPresent();
    }

    /**
     * assert that element won't become present
     */
    @Override public void assertIsNotPresent() {
        decoratedElement.assertIsNotPresent();
    }

    /**
     * Waits for a time-span until an element is present
     *
     * @param timeoutSeconds time delay the method is waiting
     * @return true if element is present after the given time delay
     */
    @Override public boolean waitForIsPresent(int timeoutSeconds) {
        return decoratedElement.waitForIsPresent(timeoutSeconds);
    }

    /**
     * Waits a certain period seconds until an element is present (class-target used)
     *
     * @return true if element is present else it returns false
     */
    @Override public boolean waitForIsPresent() {
        return decoratedElement.waitForIsPresent();
    }

    /**
     * Waits until an element is not present (class-target used)
     *
     * @return true if element is not present else it returns false
     */
    @Override public boolean waitForIsNotPresent() {
        return decoratedElement.waitForIsNotPresent();
    }

    /**
     * Waits for a certain number of seconds until an element is not present
     *
     * @param seconds the time delay the method is waiting
     * @return true if element is not present else it returns false
     */
    @Override public boolean waitForIsNotPresent(int seconds) {
        return decoratedElement.waitForIsNotPresent(seconds);
    }
}
