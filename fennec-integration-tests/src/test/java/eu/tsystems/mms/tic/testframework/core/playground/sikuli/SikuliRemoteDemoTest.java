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
 * Created on 16.03.2016
 *
 * Copyright(c) 2011 - 2016 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.core.playground.sikuli;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.pageobjects.NativeGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.sikuli.INativeGuiElement;
import eu.tsystems.mms.tic.testframework.sikuli.remote.connection.Type;
import eu.tsystems.mms.tic.testframework.sikuli.remote.connection.sikuliconnection.SikuliConnector;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.robot.desktop.DesktopScreen;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class to demonstrate the remote execution of sikuli based functionality.
 * Please ensure that the Sikuli Server System has started through reproducing of following steps:
 *
 * 1.) Configure the test.properties:
 *      sikuli.mode = remote
 * 2.) Start the Server
 *      a) Start the Server in STANDALONE mode:
 *           ---> "java -jar sikuliServer.jar"
 *      OR
 *      b) Start the Server in grid mode:
 *          Start the HUB:
 *              ---> "java -jar sikuliServer.jar -role hub"
 *          AND
 *          Start (at least one) NODE:
 *              ---> "java -jar sikuliServer.jar -role node -hub 127.0.0.1 -port 7703"
 *
 * Use the "-help" option to print the usage information of the server
 *  --> "java -jar sikuliServer.jar -help"
 *
 * @author fakr
 */
public class SikuliRemoteDemoTest extends AbstractTest {

    /**
     * the connector to connect to Sikuli Server
     */
    public static SikuliConnector sc;

    @BeforeMethod
    public void setup() {
        initConnector();
    }

    /**
     * initialize the connector with default configuration
     */
    public void initConnector(){
        sc = new SikuliConnector();
        sc.setSikuliType(Type.REMOTE);
        /*if (PropertyManager.getProperty(FennecProperties.SIKULI_MODE).equals(Type.REMOTE.name().toLowerCase())) {
            sc.setSikuliType(Type.REMOTE);
        } else {
            sc.setSikuliType(Type.LOCAL);
        }*/
    }

    /**
     * creates a NativeGuiElement of a Windows Start Button
     *
     * @param sc the sikuli Connector
     * @return the NativeGuiElement
     */
    public INativeGuiElement configuredWinStartSymbol(SikuliConnector sc) {
        /* use the Desktop as Screen */
        ScreenRegion s = new DesktopScreenRegion();
        /* use the default screen in a multi screen environment*/
        s.setScreen(new DesktopScreen(0)); // 0 = Main (default), 1 = Second Screen...

        /* use the image of the Windows Start Button of the test system */
        //return new NativeGuiElement("sikuli/haekchen.png", s, sc);
        return new NativeGuiElement("sikuli/WindowsMenuLogo_Win7.png", s, sc);
        //return new NativeGuiElement("sikuliPictures/RemoteStart.png", s, sc);
    }

    /**
     * testT01: click on native Start Button
     */
    @Test
    public void testT01_SikuliServer_clickStartButton_RemoteTest() {

        /* Create an Element and register it at server [IMAGE] */
        NativeGuiElement windowsStartButton = (NativeGuiElement) configuredWinStartSymbol(sc);
        /* Wait for Server (optional)*/
        TestUtils.sleep(1000);
        /* Delegate a click to Server [COMMAND] */
        windowsStartButton.click();

    }

    /**
     * Clean up after each test method
     */
    @AfterMethod
    public void shutDownSession() {
        /* Deletes all session/client information on server [SESSION] */
        SikuliConnector.cleanUp();
    }
}
