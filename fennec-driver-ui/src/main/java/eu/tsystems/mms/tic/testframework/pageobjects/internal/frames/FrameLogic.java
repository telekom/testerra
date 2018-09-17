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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.frames;

import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to encapsulate the logic needed for switching between frames.
 * The issue gets more complex when dealing with several layers of frames, so redundancy is avoided this way.
 * <p>
 * Created by rnhb on 25.03.2015.
 */
public class FrameLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameLogic.class);

    private final WebDriver driver;
    private final GuiElement[] frames;

    private int xOffset = 0;
    private int yOffset = 0;

    /**
     * Contructor.
     *
     * @param driver .
     * @param frames .
     */
    public FrameLogic(WebDriver driver, GuiElement[] frames) {
        this.driver = driver;
        this.frames = frames;
    }

    /**
     * Returns a list of frames as GuiElement in switching order.
     *
     * @return List of frames as GuiElement in switching order
     */
    public List<GuiElement> getAllFramesInOrder() {
        List<GuiElement> frameList = new LinkedList<GuiElement>();
        GuiElement[] framesToAdd = frames;
        while (framesToAdd != null) {
            GuiElement frameToAdd = framesToAdd[framesToAdd.length - 1];
            if (frameToAdd == null) {
                throw new fennecSystemException("Error when retrieving the frame hierarchy. This means, the GuiElement or one" +
                        " of its frames was not correctly constructed inside of fennec.");
            }
            frameList.add(frameToAdd);
            FrameLogic frameLogic = frameToAdd.getFrameLogic();
            if (frameLogic != null) {
                framesToAdd = frameLogic.getFrames();
            } else {
                framesToAdd = null;
            }
        }
        Collections.reverse(frameList);
        return frameList;
    }

    /**
     * Switches to the frame the GuiElement is inside in.
     */
    public void switchToCorrectFrame() {
        List<GuiElement> allFrames = getAllFramesInOrder();

        driver.switchTo().defaultContent();
        ArrayList<WebElement> frameWebElementList = new ArrayList<WebElement>();
        for (GuiElement frameGuiElement : allFrames) {
            // do not switch to the webElement that was recovered last, as this will be done by getWebElement of 'frameGuiElement'
            for (int i = 0; i < frameWebElementList.size() - 1; i++) {
                // get webelement
                WebElement webElement = frameWebElementList.get(i);

                // get offset und sum up
                Point location = webElement.getLocation();
                if (location != null) {
                    xOffset += location.getX();
                    yOffset += location.getY();
                }

                // switch
                driver.switchTo().frame(webElement);
            }
            WebElement frameWebElement = frameGuiElement.getWebElement();
            frameWebElementList.add(frameWebElement);
        }

        for (WebElement webElement : frameWebElementList) {
            LOGGER.debug("Switching to Frame " + webElement);
            driver.switchTo().frame(webElement);
        }
    }

    /**
     * Switch to the default content.
     */
    public void switchToDefaultFrame() {
        LOGGER.debug("Switching to DefaultContent");
        driver.switchTo().defaultContent();

        xOffset = 0;
        yOffset = 0;
    }

    public GuiElement[] getFrames() {
        return frames;
    }

    @Override
    public String toString() {
        String string = "FrameLogic = {";
        int i = 1;
        if (hasFrames()) {
            for (GuiElement frame : frames) {
                string += "\n" + i + ". :" + frame;
            }
        } else {
            string += "\nautodetect";
        }
        return string + "}";
    }

    public boolean hasFrames() {
        return getFrames() != null && getFrames().length > 0;
    }
}
