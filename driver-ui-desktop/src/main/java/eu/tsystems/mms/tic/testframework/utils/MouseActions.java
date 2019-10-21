/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.enums.DragAndDropOption;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.utils.reference.IntRef;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pele on 11.12.2015.
 */
public final class MouseActions {

    private static final Logger LOGGER = LoggerFactory.getLogger(MouseActions.class);

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

    public static void dragAndDrop(IGuiElement drag, IGuiElement drop) {
        dragAndDrop(drag, drop, new DragAndDropOption[0]);
    }

    public static void dragAndDropJS(IGuiElement drag, IGuiElement drop) {
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
     * @param drag GuiElement
     * @param drop GuiElement
     * @param fromX int
     * @param fromY int
     * @param toX int
     * @param toY int
     */
    private static void dragAndDropOverFrames(
        final WebDriver driver,
        IGuiElement drag,
        IGuiElement drop,
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

    public static void dragAndDrop(final WebDriver driver, WebElement drag, WebElement drop, int fromX, int fromY,
            int toX, int toY) {

        JSUtils.implementJavascriptOnPage(driver, "js/inject/dragAndDrop.js", "TesterraDragAndDrop");

        final String script = "TesterraDND(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);";

        JSUtils.executeScript(driver, script, drag, drop, fromX, fromY, toX, toY);
    }

    public static void swipeElement(final IGuiElement elementToSwipe, int offsetX, int offsetY) {

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

    /**
     * Drag and Drop with (frameaware) GuiElements on a driver session with the option for options.
     *
     * @param source .
     * @param target .
     * @param dragAndDropOptions .
     */
    public static void dragAndDrop(
        IGuiElement source,
        IGuiElement target,
        DragAndDropOption... dragAndDropOptions
    ) {
        // assert that they are displayed
        source.asserts("Drag source element is not displayed").assertIsDisplayed();
        target.asserts("Drop target element is not displayed").assertIsDisplayed();

        final boolean dragFromXY = dndOptionsContain(DragAndDropOption.DRAG_FROM_X_Y, dragAndDropOptions);
        final boolean relative = dndOptionsContain(DragAndDropOption.DROP_BY_RELATIVE_X_Y, dragAndDropOptions);
        boolean stayInDefaultFrame = false;
        final Point sourcePoint = new Point(0,0);
        if (dragFromXY) {
            stayInDefaultFrame = true;
            Point elementLocationInViewPort = JSUtils.getElementLocationInViewPort(source);
            sourcePoint.x = elementLocationInViewPort.x;
            sourcePoint.y = elementLocationInViewPort.y;
        }

        final IntRef x = new IntRef();
        final IntRef y = new IntRef();
        if (relative) {
            /*
            get relative x,y
             */
            Point relativePositionVector = JSUtils.getRelativePositionVector(source, target);
            x.setI(relativePositionVector.x);
            y.setI(relativePositionVector.y);
        }

        List<Runnable> workflow = new LinkedList<>();

        WebDriver driver = source.getWebDriver();
        final Actions actions = new Actions(driver);

        /*
        Get elements and frames
         */
        final WebElement sourceWebElement = source.getWebElement();
        final IFrameLogic sourceFrameLogic = source.getFrameLogic();
        final WebElement destinationWebElement = target.getWebElement();
        final IFrameLogic destinationFrameLogic = target.getFrameLogic();

        final IntRef sleepMS = new IntRef();
        sleepMS.setI(0);

        if (dndOptionsContain(DragAndDropOption.EXTRA_SLEEPS, dragAndDropOptions)) {
            sleepMS.setI(1000);
        }

        /*
        switch frame
         */
        if (stayInDefaultFrame) {
            workflow.add(() -> {
                LOGGER.info("Switch to default content frame");
                driver.switchTo().defaultContent();
            });
        }
        else {
            workflow.add(() -> {
                if (sourceFrameLogic != null) {
                    LOGGER.info("Switch to source frame");
                    sourceFrameLogic.switchToCorrectFrame();
                } else {
                    LOGGER.info("Switch to source frame (default content)");
                    driver.switchTo().defaultContent();
                }
            });
        }

        /*
        click and hold
         */
        workflow.add(() -> {
            if (dragFromXY) {
                LOGGER.info("MoveTo drag source by x,y: " + sourcePoint.x + "," + sourcePoint.y);
                actions.moveToElement(null, sourcePoint.x, sourcePoint.y).build().perform();
                sleep(sleepMS);
                LOGGER.info("ClickAndHold");
                actions.clickAndHold().build().perform();
            }
            else if (dndOptionsContain(DragAndDropOption.DRAG_MOVE_CLICKANDHOLD_SEPERATE, dragAndDropOptions)) {
                LOGGER.info("MoveTo drag source " + source);
                actions.moveToElement(sourceWebElement).build().perform();
                sleep(sleepMS);
                LOGGER.info("ClickAndHold");
                actions.clickAndHold().build().perform();
            }
            else {
                LOGGER.info("ClickAndHold drag source " + source);
                actions.clickAndHold(sourceWebElement).build().perform();
            }

            sleep(sleepMS);
        });

        /*
        target frame switch
         */
        workflow.add(() -> {
            if (relative) {
                /*
                just a relative move
                 */
                // do nothing
                LOGGER.debug("No frame switch");
            }
            else {
                if (destinationFrameLogic != null) {
                    LOGGER.info("Switch to target frame");
                    destinationFrameLogic.switchToCorrectFrame();
                } else {
                    LOGGER.info("Switch to target frame (default content)");
                    driver.switchTo().defaultContent();
                }
            }
        });

        /*
        release on destination
         */
        Runnable release = () -> {
            if (relative) {
                LOGGER.info("MoveRelative " + x.getI() + ", " + y.getI());
                actions.moveByOffset(x.getI(), y.getI()).build().perform();

                sleep(sleepMS);

                LOGGER.info("Release");
                actions.release().build().perform();
            }
            else if (dndOptionsContain(DragAndDropOption.EXTRA_MOVE, dragAndDropOptions)) {
                LOGGER.info("MoveTo drop target " + target);
                actions.moveToElement(destinationWebElement).build().perform();

                sleep(sleepMS);

                LOGGER.info("Release");
                actions.release().build().perform();
            }
            else {
                LOGGER.info("Release on " + target);
                actions.release(destinationWebElement).build().perform();
            }
        };
        workflow.add(release);

        /*
        extra release
         */
        if (dndOptionsContain(DragAndDropOption.DOUBLE_RELEASE, dragAndDropOptions)) {
            workflow.add(release);
        }

        /*
        click after release
         */
        if (dndOptionsContain(DragAndDropOption.CLICK_AFTER_RELEASE, dragAndDropOptions)) {
            workflow.add(() -> {
                LOGGER.info("Click");
                actions.click().build().perform();
            });
        }

        /*
        switch to default content
         */
        workflow.add(() -> {
            LOGGER.info("Switch to default content");
            driver.switchTo().defaultContent();
        });

        /*
        EXECUTE
         */
        for (Runnable runnable : workflow) {
            runnable.run();
        }

    }

    private static boolean dndOptionsContain(DragAndDropOption dragAndDropOption,
                                             DragAndDropOption... dragAndDropOptions) {
        if (dragAndDropOptions == null) {
            return false;
        }
        for (DragAndDropOption option : dragAndDropOptions) {
            if (option == dragAndDropOption) {
                return true;
            }
        }
        return false;
    }

    private static void sleep(IntRef sleepMS) {
        if (sleepMS.getI() > 0) {
            TimerUtils.sleep(sleepMS.getI());
        }
    }
}
