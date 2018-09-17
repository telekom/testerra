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

/**
 * Created by pele on 10.06.2016.
 */
public final class RTConstants {

    /**
     * The properties file.
     */
    private static final String PROPERTY_FILE = System.getProperty(FennecProperties.TEST_PROPERTIES_FILE, "test.properties");

    public static String getFennecTestPropertiesFile() {
        return PROPERTY_FILE;
    }

    /**
     * Get download directory for the specified platform.
     * 
     * @param platform os to build a valid folder
     * @return Folder path valid for given OS.
     */
    public static String getDownloadPathByOS(TestOS platform) {
        if (platform == TestOS.WINDOWS) {
            return Constants.Fennec_DOWNLOAD_DIRECTORY_WIN;
        }

        if (platform == TestOS.LINUX) {
            return Constants.Fennec_DOWNLOAD_DIRECTORY_LINUX;
        }
        throw new UnsupportedOperationException("OS not supported by Supervisor Downloader. No path for OS set in Constants");
    }

    /**
     * Hide Default constructor.
     */
    private RTConstants() {
    }

}
