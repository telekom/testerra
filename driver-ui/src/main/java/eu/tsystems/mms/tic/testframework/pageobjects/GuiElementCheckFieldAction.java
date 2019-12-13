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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicUiElement;
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

    private final static PageOverrides pageOverrides = Testerra.injector.getInstance(PageOverrides.class);

    public GuiElementCheckFieldAction(FieldWithActionConfig field, AbstractPage declaringPage) {
        super(field, declaringPage);
    }

    private void pCheckField(
        BasicUiElement guiElement,
        GuiElementAssert GuiElementAssert,
        Check check,
        boolean findNot,
        boolean fast
    ) {
        CheckRule checkRule = check.checkRule();
        if (checkRule == CheckRule.DEFAULT) {
            checkRule = pageOverrides.getCheckRule();
        }

        String errorMessageNotNot = "You are trying to FIND_NOT a not present element.";
        int prevTimeout = -1;
        if (fast) {
            /**
             * Sets the timeout for "fast" checks.
             * Unfortunately, this value is not documented and differs in previous implementations.
             * However, we set this timeout now to zero seconds.
             */
            prevTimeout = pageOverrides.setTimeoutSeconds(0);
        } else if (check.timeout()!=-1) {
            prevTimeout = pageOverrides.setTimeoutSeconds(check.timeout());
        }

        switch (checkRule) {
            case IS_PRESENT:
                if (findNot) {
                    guiElement.present().isFalse();
                } else {
                    guiElement.present().isTrue();
                }
                break;
            case IS_NOT_PRESENT:
                if (findNot) {
//                    if (fast) {
//                        assertGuiElement.assertIsPresentFast();
//                    } else {
//                        assertGuiElement.assertIsPresent();
//                    }
                    log().warn(errorMessageNotNot);
                } else {
                    guiElement.present().isFalse();
                }
                break;
            case IS_NOT_DISPLAYED: {
                if (findNot) {
                    log().warn(errorMessageNotNot);
                } else {
                    guiElement.displayed().isFalse();
                }
            }
            break;
            case DEFAULT:
            case IS_DISPLAYED:
            default: {
                if (findNot) {
                    guiElement.displayed().isFalse();
                } else {
                    guiElement.displayed().isTrue();
                }
            }
        }

        if (prevTimeout >= 0) {
            pageOverrides.setTimeoutSeconds(prevTimeout);
        }
    }

    @Override
    protected void checkField(Check check, boolean fast) {
        try {
            pCheckField(checkableInstance, null, check, findNot, fast);
        } catch (AssertionError e) {
            final PageNotFoundException pageNotFoundException = new PageNotFoundException(createReadableMessage(), e);

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

    @Override
    protected void additionalBeforeCheck() {
        if (field.isAnnotationPresent(IfJavascriptDisabled.class)) {

            if (checkableInstance != null && checkableInstance instanceof GuiElement) {
                // get the web driver session
                GuiElement guiElement = (GuiElement) checkableInstance;
                WebDriver driver = guiElement.getWebDriver();
                String sessionId = WebDriverManagerUtils.getSessionKey(driver);

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
