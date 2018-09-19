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
/*
 * Created on 26.08.2014
 *
 * Copyright(c) 1995 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.pageobjects.internal.sikuli;

import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.location.OffsetBasePoint;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.sikuli.api.*;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * This class implements the functionality of the NativeGuiElement class.
 *
 * @author fakr
 */
public final class InternalNativeGuiElement implements IInternalNativeGuiElement {

    //static {
        //Loader.load(org.bytedeco.javacpp.opencv_core.class);
        //Loader.load(org.bytedeco.javacpp.presets.opencv_core.class);
    //}

    /**
     * Logger used *
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Region that should contain this element.
     */
    private ScreenRegion screenRegion;

    /**
     * Locator Target of this Element. There are several possible Target-Implementations.
     */
    private Target target;

    /**
     * The region the this Instance represents
     */
    private ScreenRegion elementRegion;

    /**
     * Point inside this element, that is used to perform actions on this element.
     */
    private ScreenLocation handlePoint;

    /**
     * Timeout when searching for the given target in screenRegion
     * DEFAULT is 30
     */
    private static final int TIMEOUT = POConfig.getUiElementTimeoutInSeconds();

    /**
     * The AWT mouse from Sikuli
     */
    private DesktopMouse desktopMouse = new DesktopMouse();

    /**
     * The AWT keyboard from Sikuli
     */
    private DesktopKeyboard desktopKeyboard = new DesktopKeyboard();

    /**
     * Constructor for elements defined in a resource file.
     *
     * @param resourceFile Location of image in the resource folder.
     */
    public InternalNativeGuiElement(String resourceFile) {
        this(Thread.currentThread().getContextClassLoader().getResource(resourceFile));
    }

    /**
     * Constructor for elements defined in a resource file with a given region to search in.
     *
     * @param resourceFile Location of image in the resource folder.
     * @param screenRegion Region to search the element in.
     */
    public InternalNativeGuiElement(String resourceFile, ScreenRegion screenRegion) {
        this(Thread.currentThread().getContextClassLoader().getResource(resourceFile), screenRegion);
    }

    /**
     * Constructor for elements defined by an URL.
     *
     * @param url Locator for the image.
     */
    public InternalNativeGuiElement(URL url) {
        this(new ImageTarget(url), new DesktopScreenRegion());
    }

    /**
     * Constructor for elements defined by an URL with a given region to search in.
     *
     * @param url          Locator for the image.
     * @param screenRegion Region to search the element in.
     */
    public InternalNativeGuiElement(URL url, ScreenRegion screenRegion) {
        this(new ImageTarget(url), screenRegion);
    }

    /**
     * Basic Constructor.
     *
     * @param target       Sikuli Target to locate the element with.
     * @param screenRegion Region to search the element in.
     */
    public InternalNativeGuiElement(Target target, ScreenRegion screenRegion) {
        this.target = target;
        this.screenRegion = screenRegion;
    }

    /**
     * Private Constructor to create a new Element by directly providing its location and size.
     *
     * @param elementRegion The region of the new element.
     * @param screenRegion  The region the new element is located in.
     */
    public InternalNativeGuiElement(ScreenRegion elementRegion, ScreenRegion screenRegion) {
        this.elementRegion = elementRegion;
        this.screenRegion = screenRegion;
    }

    /**
     * Execute sequence utility method.
     *
     * @param <T>      .
     * @param sequence .
     * @return .
     */
    private <T> ThrowablePackedResponse<T> executeSequence(Timer.Sequence<T> sequence) {
        return executeSleepingSequence(sequence, 1000);
    }

    /**
     * Execute sequence utility method with sleep time.
     *
     * @param <T>      .
     * @param sequence .
     * @return .
     */
    private <T> ThrowablePackedResponse<T> executeSleepingSequence(Timer.Sequence<T> sequence, int sleepTimeInSeconds) {
        Timer timer = new Timer(sleepTimeInSeconds * 1000, TIMEOUT);
        return timer.executeSequence(sequence);
    }

    /**
     * Execute sequence utility method with time out.
     *
     * @param <T>      .
     * @param sequence .
     * @return .
     */
    private <T> ThrowablePackedResponse<T> executeTimeoutSequence(Timer.Sequence<T> sequence, int timeoutInSeconds) {
        /** Sequence runs every 1000 ms until given timeout in ms is reached*/
        Timer timer = new Timer(1000, timeoutInSeconds * 1000);
        return timer.executeSequence(sequence);
    }

    /**
     * Constructor-method to instantiate with a resource file, searching only in the region of this element.
     *
     * @param resourceFile Location of image in the resource folder.
     * @return New InternalNativeGuiElement Instance
     */
    public InternalNativeGuiElement getSubElement(String resourceFile) {
        return getSubElement(Thread.currentThread().getContextClassLoader().getResource(resourceFile));
    }

    /**
     * Constructor-method to instantiate with an url, searching only in the region of this element.
     *
     * @param url Locator for the image.
     * @return New InternalNativeGuiElement Instance
     */
    public InternalNativeGuiElement getSubElement(URL url) {
        return new InternalNativeGuiElement(url, elementRegion);
    }

    /**
     * Method to create a new 1x1 pixels big InternalNativeGuiElement based on another element and an offset. This
     * allows for precise actions in a larger element, instead of always using its center.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this InternalNativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this InternalNativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     * @return A new InternalNativeGuiElement of size 1x1.
     */
    public InternalNativeGuiElement getNewElementByOffset(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint) {
        find();
        ScreenLocation offsetLocation = getOffsetLocation(offsetX, offsetY, offsetBasePoint);
        ScreenRegion offsetElementRegion = new DesktopScreenRegion(offsetLocation.getX(), offsetLocation.getY(), 1, 1);
        return new InternalNativeGuiElement(offsetElementRegion, elementRegion);
    }

    /**
     * Changes the point that is used for interaction with this element. This way, for example a clicks can be
     * permanently directed to specific locations in this element.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this InternalNativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this InternalNativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     */
    public void setRelativeHandlePoint(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint) {
        handlePoint = getOffsetLocation(offsetX, offsetY, offsetBasePoint);
    }

    /**
     * Calculates an offset point based on a offsetBasePoint.
     *
     * @param offsetX         Horizontal offset to a BasePoint in this InternalNativeGuiElement.
     * @param offsetY         Vertical offset to a BasePoint in this InternalNativeGuiElement.
     * @param offsetBasePoint BasePoint to use for applying the given offset.
     * @return The location set off by the parameters.
     */
    private ScreenLocation getOffsetLocation(int offsetX, int offsetY, OffsetBasePoint offsetBasePoint) {
        find();
        if (elementRegion == null) {
            throw new ElementNotFoundException(this + " was not found.");
        }
        ScreenLocation baseLocation;
        switch (offsetBasePoint) {
            case TOP_LEFT:
                baseLocation = elementRegion.getUpperLeftCorner();
                break;
            case CENTER:
                baseLocation = elementRegion.getCenter();
                break;
            default:
                baseLocation = elementRegion.getUpperLeftCorner();
        }
        int positionX = baseLocation.getX() + offsetX;
        int positionY = baseLocation.getY() + offsetY;
        return new DesktopScreenLocation(positionX, positionY);
    }

    /**
     * Mainly for debug purposes, this get all matches of this elements target in its screenregion and save them image
     * to the given path. In general the first of these elements is the best one and will be actually used.
     *
     * @param path Path to store the images in.
     */
    public void saveAllRegionMatchesToPath(String path) {
        List<ScreenRegion> all = screenRegion.findAll(target);
        int count = 0;
        for (ScreenRegion region : all) {
            File outputfile = new File(path + "/image" + count + ".png");
            count++;
            try {
                ImageIO.write(region.capture(), "png", outputfile);
            } catch (IOException e) {
                logger.trace("Error occurred writing the file", e);
            }

        }
    }

    /**
     * Get the Screen Region is element is located in.
     *
     * @return The region this element is located in.
     */
    public ScreenRegion getScreenRegion() {
        return screenRegion;
    }

    /**
     * Get the ScreenRegion that is defined as this element.
     *
     * @return The region of this element
     */
    public ScreenRegion getElementRegion() {
        find();
        return elementRegion;
    }

    /**
     * Get the Size of the Element.
     *
     * @return Size of the element
     */
    public Dimension getSize() {
        find();
        ScreenLocation ul = elementRegion.getUpperLeftCorner();
        ScreenLocation lr = elementRegion.getLowerRightCorner();
        return new Dimension(lr.getX() - ul.getX(), lr.getY() - ul.getY());
    }

    /**
     * Find the element with its target.
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Find the element with its target and the given timeout.
     */
    public void find() {
        if (elementRegion == null) {
            elementRegion = screenRegion.wait(target, TIMEOUT);
            if (elementRegion != null) {
                handlePoint = elementRegion.getCenter();
            } else {
                throw new ElementNotFoundException("Element not found: " + target.toString());
            }
        } else {
            handlePoint = elementRegion.getCenter();
        }
        /* do nothing, if element is already found */
    }

    /**
     * Find the element. Do not matter about the current element information and wait for it
     */
    public void forceFind() {
        /* wait 1 second to find the target */
        elementRegion = screenRegion.wait(target, 1000);
        handlePoint = elementRegion.getCenter();
    }

    /**
     * Perform a click into the center with AWT.
     */
    public void click() {
        find();
        desktopMouse.click(handlePoint);
    }

    /**
     * Click with the specified Button.
     *
     * @param button the button to click
     */
    public void click(INativeGuiElement.MouseButton button) {
        find();
        desktopMouse.hover(handlePoint);
        desktopMouse.mouseDown(button.getInputEvent());
        desktopMouse.mouseUp(button.getInputEvent());
    }

    /**
     * Perform a DoubleClick into the center with AWT.
     */
    public void doubleClick() {
        find();
        desktopMouse.doubleClick(handlePoint);
    }

    /**
     * DoubleClick with the specified Button.
     *
     * @param button the button to click
     */
    public void doubleClick(INativeGuiElement.MouseButton button) {
        find();
        desktopMouse.hover(handlePoint);
        desktopMouse.mouseDown(button.getInputEvent());
        desktopMouse.mouseUp(button.getInputEvent());
        desktopMouse.mouseDown(button.getInputEvent());
        desktopMouse.mouseUp(button.getInputEvent());
    }

    /**
     * Type the given text with AWT. Please note that there might be problems with special characters. If you have
     * trouble with this, use paste.
     *
     * @param text The text to type
     */
    public void type(final String text) {
        click();
        desktopKeyboard.type(text);
    }

    /**
     * Paste the given text into the element.
     *
     * @param text The text to paste
     */
    public void paste(final String text) {
        click();
        desktopKeyboard.paste(text);
    }

    /**
     * A fast left click for internal purposes.
     */
    private void fastLeftClick() {
        desktopMouse.mouseDown(InputEvent.BUTTON1_MASK);
        desktopMouse.mouseUp(InputEvent.BUTTON1_MASK);
    }

    /**
     * Returns the text of the block that is in th center of this element. Only works if the text is selectable, not
     * with text in an image. The returned text is the whole paragraph, possibly more than visible in the image.
     *
     * @return Textblock of the center of the image.
     */
    public String getSelectableText() {
        find();
        desktopMouse.move(getLocation());
        fastLeftClick();
        fastLeftClick();
        fastLeftClick();
        return desktopKeyboard.copy();
    }

    /**
     * Get the handlePoint of this element.
     *
     * @return The point in the element on which actions are performed.
     */
    public ScreenLocation getLocation() {
        find();
        return handlePoint;
    }

    /**
     * Drag and drop this element onto another element.
     *
     * @param targetElement The element on which this element should be dropped
     */
    @Override
    public void dragAndDropToElement(INativeGuiElement targetElement) {
        dragAndDropToElement(targetElement, INativeGuiElement.MouseButton.LEFT);
    }

    /**
     * Drag and drop this element onto another element.
     *
     * @param targetElement The element on which this element should be dropped
     * @param button        The button which performs the dragging
     */
    @Override
    public void dragAndDropToElement(INativeGuiElement targetElement, INativeGuiElement.MouseButton button) {
        targetElement.find();
        ScreenLocation thisLocation = getLocation();
        ScreenLocation targetLocation = targetElement.getLocation();
        dragAndDrop(thisLocation.getX(), thisLocation.getY(), targetLocation.getX(), targetLocation.getY(), button);

    }

    /**
     * Drag this element and drop it on the given absolute location.
     *
     * @param x absolute x-coordinate
     * @param y absolute y-coordinate
     */
    public void dragAndDropToScreenLocation(int x, int y) {
        dragAndDropToScreenLocation(x, y, INativeGuiElement.MouseButton.LEFT);
    }

    /**
     * Drag this element and drop it on the given absolute location, using the given MouseButton.
     *
     * @param x      absolute x-coordinate
     * @param y      absolute y-coordinate
     * @param button MouseButton instance
     */
    public void dragAndDropToScreenLocation(int x, int y, INativeGuiElement.MouseButton button) {
        ScreenLocation thisLocation = getLocation();
        dragAndDrop(thisLocation.getX(), thisLocation.getY(), x, y, button);
    }

    /**
     * Drag this element and drop it to the given, relative location.
     *
     * @param x relative x-coordinate
     * @param y relative y-coordinate
     */
    public void dragAndDropRelative(int x, int y) {
        dragAndDropRelative(x, y, INativeGuiElement.MouseButton.LEFT);
    }

    /**
     * Drag this element with the give MouseButton and drop it to the given, relative location.
     *
     * @param x      relative x-coordinate
     * @param y      relative y-coordinate
     * @param button MouseButton instance
     */
    public void dragAndDropRelative(int x, int y, INativeGuiElement.MouseButton button) {
        ScreenLocation thisLocation = getLocation();
        int startX = thisLocation.getX();
        int startY = thisLocation.getY();
        dragAndDrop(startX, startY, startX + x, startY + y, button);
    }

    /**
     * Drag this element and drop it on the given location using the mouse button.
     *
     * @param startX x-coordinate to start the drag from
     * @param startY y-coordinate to start the drag from
     * @param endX   x-coordinate to endTime the drag at
     * @param endY   y-coordinate to endTime the drag at
     * @param button the button to do the drag with
     */
    public void dragAndDrop(int startX, int startY, int endX, int endY, INativeGuiElement.MouseButton button) {
        find();
        desktopMouse.hover(new DefaultScreenLocation(screenRegion.getScreen(), startX, startY));
        desktopMouse.mouseDown(button.getInputEvent());
        desktopMouse.hover(new DefaultScreenLocation(screenRegion.getScreen(), endX, endY));
        desktopMouse.mouseUp(button.getInputEvent());
    }

    /**
     * Hover the cursor over this element.
     */
    public void hover() {
        desktopMouse.hover(getLocation());
    }

    /**
     * Checks whether InternalNativeGuiElement is present (tries to find element)
     *
     * @return true if element is present else false
     */
    public boolean isPresent() {
        try {
            this.find();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * assert that element will become present
     */
    public void assertIsPresent() {
        Assert.assertTrue(waitForIsPresent(), "Element is present: " + this);

    }

    /**
     * assert that element won't become present
     */
    public void assertIsNotPresent() {
        Assert.assertTrue(waitForIsNotPresent(), "Element is not present" + this);
    }

    /**
     * Waits for a time-span until an element is present
     *
     * @param timeoutInSeconds time delay the method is waiting
     * @return true if element is present after the given time delay
     */
    public boolean waitForIsPresent(int timeoutInSeconds) {
        return this.pWaitForIsPresent(timeoutInSeconds);
    }

    /**
     * Waits a certain period seconds until an element is present (class-target used)
     *
     * @return true if element is present else it returns false
     */
    public boolean waitForIsPresent() {
        return pWaitForIsPresent(TIMEOUT);
    }

    /**
     * Waits for a time-span until an element is present
     *
     * @param timeoutInSeconds time delay the method is waiting for the element
     * @return true if element is present after the given time delay
     */
    private boolean pWaitForIsPresent(final int timeoutInSeconds) {
        final ThrowablePackedResponse<Boolean> response = executeTimeoutSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                boolean isPresent = isPresent();
                setPassState(isPresent);
                setReturningObject(isPresent);
            }
        }, timeoutInSeconds);
        return response.logThrowableAndReturnResponse();
    }


    /**
     * Waits until an element is not present (class-target used)
     *
     * @return true if element is not present else it returns false
     */
    public boolean waitForIsNotPresent() {
        return pWaitForIsNotPresent();
    }

    /**
     * Waits for a certain number of seconds until an element is not present
     *
     * @param timeoutInSeconds the timeout the method is waiting for element
     *                         to become not present
     * @return true if element is not present else it returns false
     */
    @Override
    public boolean waitForIsNotPresent(int timeoutInSeconds) {
        return pWaitForIsNotPresent(timeoutInSeconds);
    }

    /**
     * Waits until an element is not present (class-target used)
     *
     * @return true if element is not present else it returns false
     */
    private boolean pWaitForIsNotPresent() {
        /* wait for not presents as long as specified at POConfig */
        return pWaitForIsNotPresent(TIMEOUT);
    }

    /**
     * Waits for a certain number of seconds until an element is not present
     *
     * @param timeoutInSeconds the time the method is waiting
     * @return true if element is not present else it returns false
     */
    private boolean pWaitForIsNotPresent(final int timeoutInSeconds) {
        /* the element is already not present */
        if (!isPresent()) {
            return true;
        }
        final ThrowablePackedResponse<Boolean> response = executeTimeoutSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                boolean isNotPresent;
                try {
                    forceFind();
                    isNotPresent = false;
                } catch (Exception e) {
                    isNotPresent = true;
                }
                setPassState(isNotPresent);
                setReturningObject(isNotPresent);
            }
        }, timeoutInSeconds);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public String toString() {
        Dimension dimension = screenRegion.getScreen().getSize();
        double height = dimension.getHeight();
        double width = dimension.getWidth();
        return "target=" + target + "\n" +
                "screen=" + width + "x" + height;
    }

   /* private static void loadLibrary() {
        try {
            InputStream in = null;
            File fileOut = null;
            String osName = System.getProperty("os.name");
            SikuliLogging.logInfo(InternalNativeGuiElement.class, osName);
            if(osName.startsWith("Windows")){
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if(bitness == 32){
                    SikuliLogging.logInfo(InternalNativeGuiElement.class,"32 bit detected");
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                            "org/bytedeco/javacpp/windows-x86/jniopencv_core.dll"
                    );
                    fileOut = File.createTempFile("lib", ".dll");
                }
                else if (bitness == 64){
                    SikuliLogging.logInfo(InternalNativeGuiElement.class,"64 bit detected");
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                            "org/bytedeco/javacpp/windows-x86_64/jniopencv_core.dll"
                    );
                    fileOut = File.createTempFile("lib", ".dll");
                }
                else{
                    SikuliLogging.logInfo(InternalNativeGuiElement.class, "Unknown bit detected - trying with 32 bit");
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                            "org/bytedeco/javacpp/windows-x86/jniopencv_core.dll"
                    );
                    fileOut = File.createTempFile("lib", ".dll");
                }
            }
            *//*else*//*
            *//*Not supported platform*//*

            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            Loader.load(fileOut.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }*/
}
