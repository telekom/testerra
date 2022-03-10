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
 package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.CheckFieldAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldWithActionConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

public class GuiElementCheckFieldAction extends CheckFieldAction {

    public GuiElementCheckFieldAction(FieldWithActionConfig field, AbstractPage declaringPage) {
        super(field, declaringPage);
    }

    private void pCheckField(GuiElement guiElement, GuiElementAssert GuiElementAssert, CheckRule checkRule, boolean findNot, boolean fast) {
        if (checkRule == CheckRule.DEFAULT) {
            checkRule = POConfig.getGuiElementCheckRule();
        }

        String errorMessageNotNot = "You are trying to FIND_NOT a not present element.";

        switch (checkRule) {
            case DEFAULT:
                throw new SystemException("Internal Error. Please provide stacktrace to testerra developers.");
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
                throw new SystemException("CheckRule not implemented: " + checkRule);
        }
    }

    @Override
    protected void checkField(Check check, boolean fast) {
        CheckRule checkRule = check.checkRule();

        GuiElement guiElement;
        try {
            guiElement = (GuiElement) checkableInstance;
        } catch (ClassCastException e) {
            throw new SystemException("Internal Error. FieldAction was used by a Page, for which it was not implemented." +
                    " " + declaringPage + " from " + declaringClass, e);
        }

        if (check.optional()) {
            pCheckField(guiElement, guiElement.optionalAsserts(), checkRule, findNot, fast);
        } else {

            try {
                pCheckField(guiElement, guiElement.asserts(), checkRule, findNot, fast);
            } catch (AssertionError e) {
                final PageNotFoundException pageNotFoundException = new PageNotFoundException(readableMessage, e);

                /*
                if @Check has a prioritizedErrorMessage mark, then wrap t's
                 */
                String prioritizedErrorMessage = check.prioritizedErrorMessage();
                if (!StringUtils.isBlank(prioritizedErrorMessage)) {
                    throw new AssertionError(prioritizedErrorMessage, pageNotFoundException);
                } else {
                    throw pageNotFoundException;
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
                WebDriver driver = guiElement.getWebDriver();
                String sessionId = WebDriverManagerUtils.getSessionKey(driver);

                if (!StringUtils.isBlank(sessionId)) {
                    // do only search for the gui element if JS is disabled
                    if (WebDriverManager.isJavaScriptActivated(driver)) {
                        execute = false;
                    }
                }
            }
        }
    }

}
