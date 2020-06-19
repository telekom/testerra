/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package eu.tsystems.mms.tic.testframework.sikuli;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * default image element with implemented image element
 */
public class DefaultImageElement implements ImageElement {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private void executeJavaScriptMouseAction(String type, int x, int y) {
        logger.info("JS action " + type + " at " + x + "," + y);
        String aimedElement = "arguments[0]";
        if (containerWebElement == null) {
            aimedElement = "document";
        }
        ((JavascriptExecutor) driver).executeScript("var evt = document.createEvent('MouseEvents');"
                + " evt.initMouseEvent('" + type + "',true, true, window, 0, 0, 0,"
                + x + "," + y + ","
                + " false, false, false, false, 0,null);" +
                " " + aimedElement + ".dispatchEvent(evt);", containerWebElement);
    }

    /**
     * executes java script mouse action by click
     */
    public void click() {
        executeJavaScriptMouseAction("click", x + width / 2, y + height / 2);
    }

    /**
     * executes java script mouse action by doubleclick
     */
    public void doubleClick() {
        executeJavaScriptMouseAction("dblclick", x + width / 2, y + height / 2);
    }

    private final WebDriver driver;
    private final WebElement containerWebElement;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    DefaultImageElement(WebDriver driver, WebElement containerWebElement, int x, int y, int width, int height) {
        this.driver = driver;
        this.containerWebElement = containerWebElement;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
