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
 * Created on 09.05.2016
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.core.test.sikuli;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.pageobjects.NativeGuiElement;
import eu.tsystems.mms.tic.testframework.sikuli.remote.connection.Type;
import eu.tsystems.mms.tic.testframework.sikuli.remote.connection.sikuliconnection.SikuliConnectionInfo;
import eu.tsystems.mms.tic.testframework.sikuli.remote.connection.sikuliconnection.SikuliConnector;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

/**
 * This class ...
 *
 * @author fakr
 */
public class SikuliRemoteTest extends AbstractTest {
    /**
     * the connector to connect to Sikuli Server
     */
    public static SikuliConnector sc;

    @BeforeMethod
    public void setup(Method method) {
        initConnector();
    }

    /**
     * initialize the connector with default configuration
     */
    public void initConnector(){
        sc = new SikuliConnector();
        sc.setSikuliType(Type.REMOTE);
    }



    /**
     * testT01: click on native Start Button
     */
    @Test(expectedExceptions = FennecSystemException.class)
    public void testT01_SikuliClient_connectToServer() {
        sc.setScInfo(new SikuliConnectionInfo("notExisting",0));
        NativeGuiElement nge = new NativeGuiElement(
                new ImageTarget(
                        new BufferedImage(10,10, BufferedImage.TYPE_3BYTE_BGR)),
                new DesktopScreenRegion(),sc);
    }


}
