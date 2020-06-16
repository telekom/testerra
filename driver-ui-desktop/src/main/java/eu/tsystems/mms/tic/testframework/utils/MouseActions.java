/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class MouseActions {

    public enum Position {
        Top_Left, Top, Top_Right, Left, Center, Right, Bottom_Left, Bottom, Bottom_Right;

        static int getX(Position pos, int width) {
            if (Top_Left.equals(pos) || Left.equals(pos) || Bottom_Left.equals(pos)) {
                return 1;
            } else if (Top.equals(pos) || Center.equals(pos) || Bottom.equals(pos)) {
                return width / 2;
            } else if (Top_Right.equals(pos) || Right.equals(pos) || Bottom_Right.equals(pos)) {
                return width - 1;
            } else {
                return 0;
            }
        }

        static int getY(Position pos, int height) {
            if (Top_Left.equals(pos) || Top.equals(pos) || Top_Right.equals(pos)) {
                return 1;
            } else if (Left.equals(pos) || Center.equals(pos) || Right.equals(pos)) {
                return height / 2;
            } else if (Bottom_Left.equals(pos) || Bottom.equals(pos) || Bottom_Right.equals(pos)) {
                return height - 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Performs an drag&drop action by using a provided JavaScript Solution to work with modern websites.
     *
     * @param drag {@link GuiElement} Element to drag
     * @param drop {@link GuiElement} Drop location
     */
    public static void dragAndDropJS(GuiElement drag, GuiElement drop) {
        final IFrameLogic dragFrameLogic = drag.getFrameLogic();
        final WebElement dragWebElement = drag.getWebElement();
        final Point dragLocation = drag.getLocation();
        final Dimension dragSize = drag.getSize();

        final IFrameLogic dropFrameLogic = drop.getFrameLogic();
        final WebElement dropWebElement = drop.getWebElement();
        final Point dropLocation = drop.getLocation();
        final Dimension dropSize = drop.getSize();

        int dragFromX = dragLocation.getX()
                + (dragSize == null ? 0 : MouseActions.Position.getX(MouseActions.Position.Center, dragSize.getWidth()));
        int dragFromY = dragLocation.getY() + (dragSize == null ? 0
                : MouseActions.Position.getY(MouseActions.Position.Center, dragSize.getHeight()));
        int dragToX = dropLocation.getX()
                + (dropSize == null ? 0 : MouseActions.Position.getX(MouseActions.Position.Center, dropSize.getWidth()));
        int dragToY = dropLocation.getY() + (dropSize == null ? 0
                : MouseActions.Position.getY(MouseActions.Position.Center, dropSize.getHeight()));

        final WebDriver driver = drag.getWebDriver();

        // frame? ...
        if (dragFrameLogic == null && dropFrameLogic == null) {
            dragAndDrop(driver, dragWebElement, dropWebElement, dragFromX, dragFromY, dragToX, dragToY);
        } else {
            dragAndDropOverFrames(driver, drag, drop, dragFromX, dragFromY, dragToX, dragToY);
        }
    }

    /**
     * Perfoms a drag and drop over frames using the JS selectors.
     *
     * @param driver WebDriver
     * @param drag   GuiElement
     * @param drop   GuiElement
     * @param fromX  int
     * @param fromY  int
     * @param toX    int
     * @param toY    int
     */
    private static void dragAndDropOverFrames(
        final WebDriver driver,
        GuiElement drag,
        GuiElement drop,
        int fromX,
        int fromY,
        int toX,
        int toY
    ) {

        JSUtils.implementJavascriptOnPage(driver, "js/inject/dragAndDrop.js", "TesterraDragAndDrop");

        final String jsSelectorDrag = JSUtils.getJavaScriptSelector(drag);
        final String jsSelectorDrop = JSUtils.getJavaScriptSelector(drop);

        final String script = "TesterraDNDFrame(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);";
        JSUtils.executeScript(driver, script, jsSelectorDrag, jsSelectorDrop, fromX, fromY, toX, toY);
    }

    private static void dragAndDrop(final WebDriver driver, WebElement drag, WebElement drop, int fromX, int fromY,
                                    int toX, int toY) {

        JSUtils.implementJavascriptOnPage(driver, "js/inject/dragAndDrop.js", "TesterraDragAndDrop");

        final String script = "TesterraDND(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);";

        JSUtils.executeScript(driver, script, drag, drop, fromX, fromY, toX, toY);
    }

    public static void swipeElement(GuiElement elementToSwipe, int offsetX, int offsetY) {

        WebDriver driver = elementToSwipe.getWebDriver();
        Point location = elementToSwipe.getLocation();
        Dimension size = elementToSwipe.getSize();

        int fromX = location.getX()
                + (size == null ? 0 : MouseActions.Position.getX(MouseActions.Position.Center, size.getWidth()));
        int fromY = location.getY()
                + (size == null ? 0 : MouseActions.Position.getY(MouseActions.Position.Center, size.getHeight()));

        int toX = fromX + offsetX;
        int toY = fromY + offsetY;

        JSUtils.implementJavascriptOnPage(driver, "js/inject/dragAndDrop.js", "TesterraDragAndDrop");
        final String script = "TesterraSwipe(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);";
        JSUtils.executeScript(driver, script, elementToSwipe.getWebElement(), fromX, fromY, toX, toY);
    }

}
