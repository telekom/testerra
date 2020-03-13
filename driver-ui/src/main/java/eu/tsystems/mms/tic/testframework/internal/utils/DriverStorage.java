/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.internal.utils;

import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DriverStorage {

    private DriverStorage() {
            }

    private static final Map<String, Map<String, WebDriver>> DRIVERS_PER_TEST_METHOD =
            Collections.synchronizedMap(new HashMap<String, Map<String, WebDriver>>());

    /**
     * save the existing EventFiringDriver for the existing Testmethodname and ThreadName in a SynchronizedMap
     * @param driver .
     * @param threadName .
     * @param testMethodName .
     */
    public static void saveDriverForTestMethod(WebDriver driver, String threadName, String testMethodName) {
        if (!DRIVERS_PER_TEST_METHOD.containsKey(testMethodName)) {
            Map<String, WebDriver> map = Collections.synchronizedMap(new HashMap<String, WebDriver>());
            map.put(threadName, driver);
            DRIVERS_PER_TEST_METHOD.put(testMethodName, map);
        } else {
            Map<String, WebDriver> map = DRIVERS_PER_TEST_METHOD.get(testMethodName);
            map.put(threadName, driver);
            DRIVERS_PER_TEST_METHOD.put(testMethodName, map);
        }
    }

    /**
     * Get an existing EventFiringDriver for an existing testMethodName and threadName
     * @param testMethodName .
     * @param threadName .
     * @return webdriver
     */
    public static WebDriver getDriverByTestMethodName(String testMethodName, String threadName) {
        WebDriver webDriver = null;
        if (DRIVERS_PER_TEST_METHOD.containsKey(testMethodName)) {
            Map<String, WebDriver> driverMap = DRIVERS_PER_TEST_METHOD.get(testMethodName);
            if (driverMap != null) {
                webDriver = driverMap.get(threadName);
            }
        }
        return webDriver;
    }

    /**
     * Get all EventFiringDrivers of all executed TestMethods
     * @return drivers per test method
     */
    public static Map<String, Map<String, WebDriver>> getAllDriversPerTestMethod() {
        return DRIVERS_PER_TEST_METHOD;
    }

    /**
     * removes specific drivers from test method
     *
     * @param testMethodName .
     */
    public static void removeSpecificDriver(String testMethodName) {
        String threadName = Thread.currentThread().getId() + "";
         if (DRIVERS_PER_TEST_METHOD.containsKey(testMethodName)) {
             DRIVERS_PER_TEST_METHOD.get(testMethodName).remove(threadName);
         }
    }

    /**
     * clears the drivers
     */
    public static void clearDriverMap() {
        DRIVERS_PER_TEST_METHOD.clear();
    }
}
