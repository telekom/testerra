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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
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

    private final boolean forceStandardAssert;
    private final static PageOverrides pageOverrides = Testerra.ioc().getInstance(PageOverrides.class);

    public GuiElementCheckFieldAction(FieldWithActionConfig field, AbstractPage declaringPage) {
        super(field, declaringPage);
        forceStandardAssert = field.forceStandardAssert;
    }

    private void pCheckField(BasicGuiElement guiElement, GuiElementAssert GuiElementAssert, CheckRule checkRule, boolean findNot, boolean fast) {
        if (checkRule == CheckRule.DEFAULT) {
            checkRule = pageOverrides.getGuiElementCheckRule(CheckRule.valueOf(GuiElement.Properties.CHECK_RULE.asString()));
        }

        String errorMessageNotNot = "You are trying to FIND_NOT a not present element.";

        if (fast) {
            /**
             * Sets the timeout for "fast" checks.
             * Unfortunately, this value is not documented and differs in previous implementations.
             * However, we set this timeout now to zero seconds.
             */
            pageOverrides.setElementTimeoutInSeconds(0);
        }

        switch (checkRule) {
            case DEFAULT:
                throw new TesterraSystemException("Internal Error. Please provide stacktrace to testerra developers.");
            case IS_PRESENT:
                if (findNot) {
                    GuiElementAssert.assertIsNotPresent();
                } else {
                    GuiElementAssert.assertIsPresent();
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
                    GuiElementAssert.assertIsNotPresent();
                }
                break;
            case IS_DISPLAYED: {
                if (findNot) {
                    GuiElementAssert.assertIsNotDisplayed();
                } else {
                    GuiElementAssert.assertIsDisplayed();
                }
            }
            break;
            case IS_NOT_DISPLAYED: {
                if (findNot) {
                    logger.warn(errorMessageNotNot);
                } else {
                    GuiElementAssert.assertIsNotDisplayed();
                }
            }
            break;
            default:
                if (fast) {
                    pageOverrides.removeElementTimeoutInSeconds();
                }
                throw new TesterraSystemException("CheckRule not implemented: " + checkRule);
        }
        if (fast) {
            pageOverrides.removeElementTimeoutInSeconds();
        }
    }

    @Override
    protected void checkField(Check check, boolean fast) {
        CheckRule checkRule = check.checkRule();

        if (check.nonFunctional()) {
            pCheckField(checkableInstance, checkableInstance.nonFunctionalAsserts(), checkRule, findNot, fast);
        } else {

            GuiElementAssert guiElementAssert;
            /**
             * A standard assert means, that a throwed exception is expected.
             * This is covered by a {@link GuiElementAssert} that contains an {@link InstantAssertion}.
             */
            if (forceStandardAssert) {
                guiElementAssert = checkableInstance.instantAsserts();
            } else {
                guiElementAssert = checkableInstance.asserts();
            }

            try {
                pCheckField(checkableInstance, guiElementAssert, checkRule, findNot, fast);
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
        }
    }

    @Override
    protected void additionalBeforeCheck() {
        if (field.isAnnotationPresent(IfJavascriptDisabled.class)) {

            if (checkableInstance != null && checkableInstance instanceof GuiElement) {
                // get the web driver session
                GuiElement guiElement = (GuiElement) checkableInstance;
                WebDriver driver = guiElement.getWebDriver();
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
