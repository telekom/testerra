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
 * Created on 21.08.2014
 * 
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.test.nativeguielement;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.NativeGuiElement;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.sikuli.api.DesktopScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author someone feeling responsible, please enter your name
 */
public class NativeGuiElementTest extends AbstractTest {

    /**
     * Resource path to start button of Windows 7
     */
    private static final String[] WINDOWS7_ICONS = {
            "sikuli/WindowsMenuLogo_Win7.png",
            "sikuli/WindowsMenuLogo_Win7_pele.png"
    };

    /**
     * Resource path to start button of Windows Server 2008
     */
    private static final String[] WINDOWSSERVER2008_ICON = {
            "sikuli/WindowsMenuLogo_WinServer2008.png",
            "sikuli/WindowsMenuLogo_WinServer2008_1024x768.png"
    };

    /**
     * The Monitor which is used as default
     */
    private static final int DEFAULT_SCREEN_ID = 0; // default: 0 <=> primary monitor

    /**
     * the default resource path to a present element
     */
    private static String currentResourcePath = "will be set automatically";

    /**
     * the current monitor used in test cases in multi monitor environment
     * 0 ... primary monitor
     * 1 ... second monitor
     * etc.
     */
    private static int screenID = DEFAULT_SCREEN_ID;

    /**
     * the ScreenRegion to use depending on screenID
     */
    private static DesktopScreenRegion screenRegion = new DesktopScreenRegion(screenID);

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeGuiElementTest.class);

    private static final String osName;

    private static final String notPresentResourcePath = "sikuli/iis.png";

    static {
        osName = System.getProperty("os.name");
        if (StringUtils.isStringEmpty(osName)) {
            throw new FennecRuntimeException("Could not get system property \"os.name\"");
        }
    }

    private List<NativeGuiElement> getNativeGuiElements() {
        List<NativeGuiElement> nativeGuiElements = new ArrayList<NativeGuiElement>();
        if (!osName.contains("2008")) {
            LOGGER.info("Using Win7 element");
            for (String icon : WINDOWS7_ICONS) {
                nativeGuiElements.add(new NativeGuiElement(icon, screenRegion));
            }
        } else {
            LOGGER.info("Using Win2008Server element");
            for (String icon : WINDOWSSERVER2008_ICON) {
                nativeGuiElements.add(new NativeGuiElement(icon, screenRegion));
            }
        }
        return nativeGuiElements;
    }

    private void exitWithError() {
        Assert.assertTrue(false, "Element not present");
    }

    /**
     * Method checks whether element is present
     */
    @Test
    public void testT01_NativeGuiElement_assertIsPresent() {
        final List<NativeGuiElement> nativeGuiElements = getNativeGuiElements();
//        for (NativeGuiElement nativeGuiElement : nativeGuiElements) {
//            try {
//                nativeGuiElement.assertIsPresent();
//                return;
//            } catch (Throwable e) {
//                LOGGER.warn("Could not find element", e);
//            }
//        }
        exitWithError();
    }

    /**
     * Method waits for element to become present
     */
    @Test
    public void testT02_NativeGuiElement_waitForIsPresent() {
        final List<NativeGuiElement> nativeGuiElements = getNativeGuiElements();
        for (NativeGuiElement nativeGuiElement : nativeGuiElements) {
            if (nativeGuiElement.waitForIsPresent()) {
                return;
            }
        }
        exitWithError();
    }

    /**
     * Method waits for element which does not become present
     */
    @Test
    public void testT02N_NativeGuiElement_waitForIsPresent_missing() {
        NativeGuiElement testElementNotPresent = new NativeGuiElement(notPresentResourcePath, screenRegion);
        Assert.assertFalse(testElementNotPresent.waitForIsPresent(), "Waiting for element is present");
    }

    /**
     * Method waits for element to become present for a certain time delay
     */
    @Test
    public void testT03_NativeGuiElement_waitForIsPresent_withTimeout() {
        final List<NativeGuiElement> nativeGuiElements = getNativeGuiElements();
        for (NativeGuiElement nativeGuiElement : nativeGuiElements) {
            if (nativeGuiElement.waitForIsPresent(5)) {
                return;
            }
        }
        exitWithError();
    }

    /**
     * Method waits for not present element to become present
     */
    @Test
    public void testT03N_NativeGuiElement_waitForIsPresent_intoTimeout() {
        NativeGuiElement testElementNotPresent = new NativeGuiElement(notPresentResourcePath, screenRegion);
        /* wait one second for the element becoming present, should return false */
        Assert.assertFalse(testElementNotPresent.waitForIsPresent(1), "Waiting for element is present");
    }

    /**
     * Method waits for an element to become not present
     */
    @Test
    public void testT04_NativeGuiElement_waitForIsNotPresent() {
        NativeGuiElement testElementNotPresent = new NativeGuiElement(notPresentResourcePath, screenRegion);
        Assert.assertTrue(testElementNotPresent.waitForIsNotPresent(), "Waiting for element to become not present");
    }

    /**
     * Method waits for an element to become not present for a certain time delay
     */
    @Test
    public void testT05_NativeGuiElement_waitForIsNotPresent_withTimeout() {
        NativeGuiElement testElementNotPresent = new NativeGuiElement(notPresentResourcePath, screenRegion);
        Assert.assertTrue(testElementNotPresent.waitForIsNotPresent(5), "Waiting for element to become not present");
    }

    /**
     * Method checks whether element is not present
     */
    @Test
    public void testT06_NativeGuiElement_assertIsNotPresent() {
        NativeGuiElement testElementNotPresent = new NativeGuiElement(notPresentResourcePath, screenRegion);
        testElementNotPresent.assertIsNotPresent();
    }
}
