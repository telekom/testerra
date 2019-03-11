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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.CheckFieldAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldWithActionConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.openqa.selenium.WebDriver;

/**
 * Created by rnhb on 17.12.2015.
 */
public class GuiElementCheckFieldAction extends CheckFieldAction {

    final boolean forceStandardAssert;

    public GuiElementCheckFieldAction(FieldWithActionConfig field, AbstractPage declaringPage) {
        super(field, declaringPage);
        forceStandardAssert = field.forceStandardAssert;
    }

    private void pCheckField(GuiElement guiElement, GuiElementAssert GuiElementAssert, CheckRule checkRule, boolean findNot, boolean fast) {
        if (checkRule == CheckRule.DEFAULT) {
            checkRule = POConfig.getGuiElementCheckRule();
        }

        String errorMessageNotNot = "You are trying to FIND_NOT a not present element.";

        switch (checkRule) {
            case DEFAULT:
                throw new FennecSystemException("Internal Error. Please provide stacktrace to fennec developers.");
            case IS_PRESENT:
                if (findNot) {
                    if (fast) {
                        GuiElementAssert.assertIsNotPresentFast();
                    } else {
                        GuiElementAssert.assertIsNotPresent();
                    }
                } else {
                    if (fast) {
                        GuiElementAssert.assertIsPresentFast();
                    } else {
                        GuiElementAssert.assertIsPresent();
                    }
                }
                break;
            case IS_NOT_PRESENT:
                if (findNot) {
//                    if (fast) {
//                        assertGuiElement.assertIsPresentFast();
//                    } else {
//                        assertGuiElement.assertIsPresent();
//                    }
                    logger.warn(errorMessageNotNot);
                } else {
                    if (fast) {
                        GuiElementAssert.assertIsNotPresentFast();
                    } else {
                        GuiElementAssert.assertIsNotPresent();
                    }
                }
                break;
            case IS_DISPLAYED: {
                // save timeout config
                int timeoutInSeconds = guiElement.getTimeoutInSeconds();
                if (fast) {
                    // reduce timeout to 0
                    guiElement.setTimeoutInSeconds(0);
                }

                try {
                    if (findNot) {
                        GuiElementAssert.assertIsNotDisplayed();
                    } else {
                        GuiElementAssert.assertIsDisplayed();
                    }
                } finally {
                    // restore timeout
                    if (fast) {
                        guiElement.setTimeoutInSeconds(timeoutInSeconds);
                    }
                }
            }
            break;
            case IS_NOT_DISPLAYED: {
                // save timeout config
                int timeoutInSeconds = guiElement.getTimeoutInSeconds();
                if (fast) {
                    // reduce timeout to 0
                    guiElement.setTimeoutInSeconds(0);
                }

                try {
                    if (findNot) {
//                            assertGuiElement.assertIsDisplayed();
                        logger.warn(errorMessageNotNot);
                    } else {
                        GuiElementAssert.assertIsNotDisplayed();
                    }
                } finally {
                    // restore timeout
                    if (fast) {
                        guiElement.setTimeoutInSeconds(timeoutInSeconds);
                    }
                }
            }
            break;
            default:
                throw new FennecSystemException("CheckRule not implemented: " + checkRule);
        }
    }

    @Override
    protected void checkField(Check check, boolean fast) {
        CheckRule checkRule = check.checkRule();

        GuiElement guiElement;
        try {
            guiElement = (GuiElement) checkableInstance;
        } catch (ClassCastException e) {
            throw new FennecSystemException("Internal Error. FieldAction was used by a Page, for which it was not implemented." +
                    " " + declaringPage + " from " + declaringClass, e);
        }

        if (check.nonFunctional()) {
            pCheckField(guiElement, guiElement.nonFunctionalAsserts(), checkRule, findNot, fast);
        } else {

            // force standard asserts if commanded so
            if (forceStandardAssert) {
                guiElement.forceStandardAsserts();
            }

            try {
                pCheckField(guiElement, guiElement.asserts(), checkRule, findNot, fast);
            } catch (AssertionError e) {
                final PageNotFoundException pageNotFoundException = new PageNotFoundException(readableMessage, e);

                /*
                if @Check has a prioritizedErrorMessage mark, then wrap t's
                 */
                String prioritizedErrorMessage = check.prioritizedErrorMessage();
                if (!StringUtils.isStringEmpty(prioritizedErrorMessage)) {
                    throw new AssertionError(prioritizedErrorMessage, pageNotFoundException);
                }
                else {
                    throw pageNotFoundException;
                }
            }
            finally {
                // reset standard asserts setting
                if (forceStandardAssert) {
                    guiElement.resetDefaultAsserts();
                }
            }

        }
    }

    @Override
    protected void additionalBeforeCheck() {
        if (field.isAnnotationPresent(IfJavascriptDisabled.class)) {

            if (checkableInstance != null && checkableInstance instanceof GuiElement) {
                // get the web driver session
                GuiElement guiElement = (GuiElement) checkableInstance;
                WebDriver driver = guiElement.getDriver();
                String sessionId = WebDriverManagerUtils.getSessionId(driver);

                if (!StringUtils.isStringEmpty(sessionId)) {
                    // do only search for the gui element if JS is disabled
                    if (WebDriverManager.isJavaScriptActivated(sessionId)) {
                        execute = false;
                    }
                }
            }
        }
    }

}
