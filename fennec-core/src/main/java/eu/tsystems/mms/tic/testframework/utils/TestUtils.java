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
 * Created on 23.02.2012
 * 
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.constants.TestOS;
import eu.tsystems.mms.tic.testframework.remote.RemoteDownloadPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Helper class containing some util methods for fennec.
 *
 * @author pele
 */
public class TestUtils {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private static final String BROWSER_DOWNLOAD_PATH_UUID = UUID.randomUUID().toString();

    /**
     * Sleep like Thread.sleep and catch InterruptedException.
     *
     * @param milliSeconds ms to sleep.
     * @param comment      .
     */
    public static void sleep(final int milliSeconds, final String comment) {
        TimerUtils.sleep(milliSeconds, comment);
    }

    /**
     * Sleep like Thread.sleep and catch InterruptedException.
     *
     * @param milliSeconds ms to sleep.
     */
    public static void sleep(final int milliSeconds) {
        TimerUtils.sleep(milliSeconds);
    }

    /**
     * Return the browser download Directory for this session. This contains a uuid which is statically created.
     *
     * @param platform OS the browser will run on
     * @return session based download path.
     */
    public static RemoteDownloadPath getStaticBrowserDownloadDirectory(TestOS platform) {
        String uuid = BROWSER_DOWNLOAD_PATH_UUID;
        String fullPath = eu.tsystems.mms.tic.testframework.constants.RTConstants.getDownloadPathByOS(platform) + uuid;
        return new RemoteDownloadPath(fullPath, uuid);
    }

    /**
     * Return the browser download Directory for this session. This contains a uuid which is created from string parameter.
     *
     * @param platform OS the browser will run on
     * @return session based download path.
     */
    public static RemoteDownloadPath generateBrowserDownloadDirectory(TestOS platform) {
        String uuid = UUID.randomUUID().toString();
        String fullPath = eu.tsystems.mms.tic.testframework.constants.RTConstants.getDownloadPathByOS(platform) + uuid;
        return new RemoteDownloadPath(fullPath, uuid);
    }

}
