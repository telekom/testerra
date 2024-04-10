/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action;

import eu.tsystems.mms.tic.testframework.annotations.PageOptions;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.UiElementBase;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class GuiElementCheckFieldAction extends AbstractCheckFieldAction {

    private final static TestController.Overrides overrides = Testerra.getInjector().getInstance(TestController.Overrides.class);
    private final CheckRule defaultCheckRule;

    public GuiElementCheckFieldAction(
            Field field,
            CheckRule defaultCheckRule,
            AbstractPage declaringPage
    ) {
        super(field, declaringPage);

        this.defaultCheckRule = defaultCheckRule;
    }

    private void pCheckField(UiElementBase guiElement, Check check) {

        CheckRule checkRule = check.checkRule();
        if (checkRule == CheckRule.DEFAULT) {
            checkRule = this.defaultCheckRule;
            if (checkRule == CheckRule.DEFAULT) {
                checkRule = CheckRule.valueOf(GuiElement.Properties.CHECK_RULE.asString());
            }
        }

        // Handling custom timeouts
        int useTimeout;
        boolean threadLocalTimeoutSet = overrides.hasTimeout();
        if (threadLocalTimeoutSet) {
            useTimeout = overrides.getTimeoutInSeconds();
        } else {
            useTimeout = check.timeout();
            if (useTimeout < 0) {
                if (declaringPage.getClass().isAnnotationPresent(PageOptions.class)) {
                    PageOptions annotation = declaringPage.getClass().getAnnotation(PageOptions.class);
                    useTimeout = annotation.elementTimeoutInSeconds();
                }
            }
        }
        int prevTimeout = -1;
        if (useTimeout >= 0) {
            prevTimeout = overrides.setTimeout(useTimeout);
        }

        /*
         * It does not make sense to set both flags optional and collect!
         *
         * Because an optional assertion is always collected but without PageFactoryException
         * the 'optional' is prioritised over 'collected'.
         * In that case 'collected' will be ignored.
         */
        if (check.collect() && check.optional()) {
            log().warn("{} -> {}: @Check 'collect' and 'optional' are used but 'collect' will be ignored."
                    , this.declaringPage.getName(false), this.field.getName());
        }

        // Handling optional and collected assertions
        Assertion previousAssertionImpl = null;
        if (check.optional()) {
            OptionalAssertion assertionImpl = Testerra.getInjector().getInstance(OptionalAssertion.class);
            previousAssertionImpl = overrides.setAssertionImpl(assertionImpl);
        } else if (check.collect()) {
            CollectedAssertion assertionImpl = Testerra.getInjector().getInstance(CollectedAssertion.class);
            previousAssertionImpl = overrides.setAssertionImpl(assertionImpl);
        }

        // Execute UiElement check
        switch (checkRule) {
            case IS_PRESENT:
                guiElement.expect().present(true);
                break;
            case IS_NOT_PRESENT:
                guiElement.expect().present(false);
                break;
            case IS_NOT_DISPLAYED: {
                if (guiElement.waitFor().present(true)) {
                    guiElement.expect().displayed(false);
                }
            }
            break;
            case DEFAULT:
            case IS_DISPLAYED:
            default: {
                guiElement.expect().displayed(true);
            }
        }

        if (threadLocalTimeoutSet) {
            if (prevTimeout >= 0) {
                // Reset timeout to previous value
                overrides.setTimeout(prevTimeout);
            }
        } else {
            // Remove thread local timeout
            overrides.setTimeout(-1);
        }

        if (check.optional() || check.collect()) {
            overrides.setAssertionImpl(previousAssertionImpl);
        }
    }

    @Override
    protected void checkField(UiElementBase checkableInstance, Check check) {
        try {
            pCheckField(checkableInstance, check);
        } catch (AssertionError error) {
            /*
            if @Check has a prioritizedErrorMessage mark, then wrap it
             */
            String prioritizedErrorMessage = check.prioritizedErrorMessage();
            if (StringUtils.isNotBlank(prioritizedErrorMessage)) {
                throw new AssertionError(prioritizedErrorMessage, error);
            } else {
                throw error;
            }
        }
    }
//
//    @Override
//    protected void additionalBeforeCheck() {
//        if (field.isAnnotationPresent(IfJavascriptDisabled.class)) {
//
//            if (checkableInstance != null && checkableInstance instanceof GuiElement) {
//                // get the web driver session
//                GuiElement guiElement = (GuiElement) checkableInstance;
//                WebDriver driver = guiElement.getWebDriver();
//                String sessionId = WebDriverManagerUtils.getSessionKey(driver);
//
//                if (!StringUtils.isStringEmpty(sessionId)) {
//                    // do only search for the gui element if JS is disabled
//                    if (WebDriverManager.isJavaScriptActivated(driver)) {
//                        execute = false;
//                    }
//                }
//            }
//        }
//    }

}
