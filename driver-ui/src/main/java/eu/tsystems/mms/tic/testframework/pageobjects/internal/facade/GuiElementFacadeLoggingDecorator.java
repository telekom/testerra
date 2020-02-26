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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.facade;

import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rnhb on 12.08.2015.
 */
public class GuiElementFacadeLoggingDecorator extends GuiElementFacadeDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiElement.class);

    private final GuiElementData guiElementData;

    public GuiElementFacadeLoggingDecorator(GuiElementFacade guiElementFacade, GuiElementData guiElementData) {
        super(guiElementFacade, guiElementData);
        this.guiElementData = guiElementData;
    }

    @Override
    protected void beforeDelegation(String methodName, String message) {

    }

    @Override
    protected void afterDelegation(String result) {
    }

    @Override
    protected void beforeActionDelegation(String message) {
        print(message);
    }

    private void print(String message) {
        String messageToLog = message + " on " + guiElementData.toString();

        LogLevel logLevel = guiElementData.getLogLevel();

        switch (logLevel) {
            case INFO:
                LOGGER.info(messageToLog);
                break;
            case DEBUG:
                LOGGER.debug(messageToLog);
                break;
            case ERROR:
                LOGGER.error(messageToLog);
                break;
            case WARNING:
                LOGGER.warn(messageToLog);
                break;
            default:
                LOGGER.error("LogLevel " + logLevel + " unknown. Tried to print " + message);
        }
    }
}
