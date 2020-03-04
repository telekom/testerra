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
package eu.tsystems.mms.tic.testframework.report;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Abstract class containing informations about a screenshot.
 */
public abstract class Shot {

    private static final Logger LOGGER = LoggerFactory.getLogger(Shot.class);

    /** Timestamp, indicates when screenshot is taken. */
    private long timestamp;

    /** The cause of the screenshot. */
    private ScreenshotCause screenshotCause;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public ScreenshotCause getScreenshotCause() {
        return screenshotCause;
    }

    public void setScreenshotCause(final ScreenshotCause screenshotCause) {
        this.screenshotCause = screenshotCause;
    }

    /**
     *
     * @param driver
     * @return File or NULL if no screenshot could be taken
     */
    public static File takeScreenshot(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        } catch (WebDriverException e) {
            LOGGER.error("Could not get screenshot: "+ e.getLocalizedMessage());
        }
        return null;
    }
}
