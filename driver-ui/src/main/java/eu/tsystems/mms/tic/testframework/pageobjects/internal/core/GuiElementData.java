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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.WeakHashMap;

/**
 * Created by rnhb on 21.12.2016.
 */
public class GuiElementData {
    public static final WeakHashMap<WebElement, GuiElement> WEBELEMENT_MAP = new WeakHashMap<>();

    public final By by;
    /**
     * @todo This will not @deprecated in Testerra 2
     */
    @Deprecated // Evil, should never be used!!! <<< why, i need it?? pele 23.08.2019
    public final GuiElement guiElement;

    public final WebDriver webDriver;
    public String name;
    public int timeoutInSeconds;
    public final TimerWrapper timerWrapper;
    public WebElement webElement;
    public final FrameLogic frameLogic;
    public final int timerSleepTimeInMs = 500;
    public boolean sensibleData = false;
    /**
     * @todo This will be @deprecated in Testerra 2
     */
    @Deprecated
    public GuiElementCore parent;
    public final int index;
    public LogLevel logLevel = LogLevel.DEBUG;
    public LogLevel storedLogLevel = logLevel;
    public String browser;
    public boolean shadowRoot = false;

    public GuiElementData(GuiElementData guiElementData, int index) {
        this(
            guiElementData.webDriver,
            guiElementData.name,
            guiElementData.frameLogic,
            guiElementData.by,
            guiElementData.guiElement,
            index
        );
        parent = guiElementData.parent;
    }

    public GuiElementData(
        WebDriver webDriver,
        String name,
        FrameLogic frameLogic,
        By by,
        GuiElement guiElement
    ) {
        this(webDriver, name, frameLogic, by, guiElement, -1);
    }

    private GuiElementData(
        WebDriver webDriver,
        String name,
        FrameLogic frameLogic,
        By by,
        GuiElement guiElement,
        int index
    ) {
        this.webDriver = webDriver;
        this.name = name;
        this.by = by;
        this.guiElement = guiElement;
        this.timeoutInSeconds = POConfig.getUiElementTimeoutInSeconds();
        this.frameLogic = frameLogic;
        // Central Timer Object which is used by all sequence executions
        this.timerWrapper = new TimerWrapper(timerSleepTimeInMs, timeoutInSeconds, webDriver);
        this.index = index;
    }

    public Logger getLogger() {
        return LoggerFactory.getLogger("GuiElement" + (name == null ? "" : " " + name));
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        timerWrapper.setTimeoutInSeconds(timeoutInSeconds);
    }

    public boolean hasName() {
        return !StringUtils.isEmpty(name);
    }

    @Override
    public String toString() {
        String toString = guiElement.getLocator().toString();
        if (parent != null) {
            toString += " child of " + parent;
        }

        if (hasName()) {
            String realName = name;
            if (index!=-1) {
                realName += "_"+index;
            }
            toString = ">" + realName + "< (" + toString + ")";
        }

        if (hasFrameLogic()) {
            String frameString = " inside Frames={";
            if (frameLogic.hasFrames()) {
                for (GuiElement frame : frameLogic.getFrames()) {
                    frameString += frame.toString() + ", ";
                }
            } else {
                frameString += "autodetect, ";
            }
            frameString = frameString.substring(0, frameString.length() - 2);
            toString = toString + frameString + "}";
        }
        return toString;
    }

    public boolean hasFrameLogic() {
        return frameLogic != null;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        // store previous log level away
        this.storedLogLevel = this.logLevel;
        // set new log level
        this.logLevel = logLevel;
    }

    public void resetLogLevel() {
        this.logLevel = this.storedLogLevel;
    }

}
