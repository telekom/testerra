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
package eu.tsystems.mms.tic.testframework.constants;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;

import java.text.SimpleDateFormat;

/**
 * User: rnhb
 * Date: 10.10.13
 * Time: 16:42
 */

public final class Constants {

    private Constants() {
    }

    public static final String EXECUTING_SELENIUM_HOST_LOCAL_MODE = "local";
    public static final int WEBDRIVER_START_RETRY_TIME_IN_MS = 10000;
    public static final SimpleDateFormat fennec_SIMPLE_DATE_FORMAT_DATE;
    public static final SimpleDateFormat fennec_SIMPLE_DATE_FORMAT_DELTA;
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = PropertyManager.getIntProperty(fennecProperties.WEBDRIVER_TIMEOUT_SECONDS_PAGELOAD, 120);

    /*
    Download paths
     */
    static final String fennec_DOWNLOAD_DIRECTORY_WIN = "C:\\fennecDownloads\\";
    static final String fennec_DOWNLOAD_DIRECTORY_LINUX = "/tmp/fennecDownloads/";


    static {
        try {
            fennec_SIMPLE_DATE_FORMAT_DATE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            fennec_SIMPLE_DATE_FORMAT_DELTA = new SimpleDateFormat("H 'h' m 'min' s 'sec' S 'ms'");
        } catch (Exception e) {
            throw new fennecSystemException("Internal Error", e);
        }
    }

}
