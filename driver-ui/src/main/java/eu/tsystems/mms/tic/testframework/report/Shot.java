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
 * Created on 14.11.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.Date;

/**
 * Abstract class containing informations about a screenshot.
 */
public class Shot<S extends TakesScreenshot> implements Loggable {

    private Date date;

    private S takesScreenShot;

    /** The cause of the screenshot. */
    private ScreenshotCause cause;

    public Shot(S takesScreenShot) {
        this.takesScreenShot = takesScreenShot;
    }

    @Deprecated
    public long getTimestamp() {
        return date.getTime();
    }

    @Deprecated
    public void setTimestamp(final long timestamp) {
        date = new Date(timestamp);
    }

    @Deprecated
    public ScreenshotCause getScreenshotCause() {
        return cause;
    }

    @Deprecated
    public void setScreenshotCause(final ScreenshotCause screenshotCause) {
        this.cause = screenshotCause;
    }

    @Deprecated
    public static File takeScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    }
}
