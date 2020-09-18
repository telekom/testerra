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
 package eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;

public class DefaultGuiElementWait implements GuiElementWait, Loggable {

    private final GuiElementCore core;
    private final GuiElementData guiElementData;

    private static PageOverrides pageOverrides = Testerra.injector.getInstance(PageOverrides.class);

    @Inject
    public DefaultGuiElementWait(GuiElementCore core, GuiElementData data) {
        this.guiElementData = data;
        this.core = core;
    }

    private TimerWrapper getTimerWrapper() {
        return Testerra.injector.getInstance(IWebDriverManager.class).getTimerWrapper(guiElementData.getWebDriver());
    }

    @Override
    public boolean waitForIsPresent() {
        return waitForPresentStatus(true);
    }

    @Override
    public boolean waitForIsNotPresent() {
        return waitForPresentStatus(false);
    }

    private boolean waitForPresentStatus(final boolean checkForPresent) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!checkForPresent); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean present = core.isPresent();
                final boolean sequenceStatus = present == checkForPresent;
                setReturningObject(sequenceStatus);
                setPassState(sequenceStatus);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForIsEnabled() {
        return pWaitForEnableDisableStatus(true);
    }

    @Override
    public boolean waitForIsDisabled() {
        return pWaitForEnableDisableStatus(false);
    }

    private boolean pWaitForEnableDisableStatus(final boolean checkForEnabled) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!checkForEnabled); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean isEnabled = core.isEnabled();
                boolean sequenceStatus = isEnabled == checkForEnabled;
                setPassState(sequenceStatus);
                setReturningObject(sequenceStatus);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForAnyFollowingTextNodeContains(final String contains) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean anyFollowingTextNodeContains = guiElementData.getGuiElement().anyFollowingTextNodeContains(contains);
                setPassState(anyFollowingTextNodeContains);
                setReturningObject(anyFollowingTextNodeContains);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForIsDisplayed() {
        return pWaitForDisplayedStatus(true);
    }

    @Override
    public boolean waitForIsNotDisplayed() {
        return pWaitForDisplayedStatus(false);
    }

    private boolean pWaitForDisplayedStatus(final boolean checkForDisplayed) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!checkForDisplayed); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean displayed = core.isDisplayed();
                boolean sequenceStatus = displayed == checkForDisplayed;
                setPassState(sequenceStatus);
                setReturningObject(sequenceStatus);
            }
        };
        log().debug("Executing pWaitForDisplayedStatus=" + checkForDisplayed + " with Sequence.");
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForIsVisible(boolean complete) {
        return pWaitForVisibleStatus(true, complete);
    }

    @Override
    public boolean waitForIsNotVisible() {
        return pWaitForVisibleStatus(false, false);
    }

    private boolean pWaitForVisibleStatus(final boolean visible, final boolean complete) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!visible); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean displayed = core.isVisible(complete);
                boolean sequenceStatus = displayed == visible;
                setPassState(sequenceStatus);
                setReturningObject(sequenceStatus);
            }
        };
        log().debug("Executing pWaitForVisibleStatus=" + visible + " with Sequence.");
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForIsDisplayedFromWebElement() {
        return pWaitForDisplayedFromWebelementStatus(true);
    }

    @Override
    public boolean waitForIsNotDisplayedFromWebElement() {
        return pWaitForDisplayedFromWebelementStatus(false);
    }

    private boolean pWaitForDisplayedFromWebelementStatus(final boolean checkForDisplayed) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!checkForDisplayed); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean displayed = core.isDisplayed();
                boolean sequenceStatus = displayed == checkForDisplayed;
                setPassState(sequenceStatus);
                setReturningObject(sequenceStatus);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForIsSelected() {
        return waitForSelectionStatus(true);
    }

    @Override
    public boolean waitForIsNotSelected() {
        return waitForSelectionStatus(false);
    }

    private boolean waitForSelectionStatus(final boolean checkForSelected) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(!checkForSelected); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean selected = core.isSelected();
                boolean sequenceStatus = selected == checkForSelected;
                setPassState(sequenceStatus);
                setReturningObject(sequenceStatus);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForText(String text) {
        return pWaitForText(text);
    }

    private boolean pWaitForText(String text) {
        final String trimmedExpectedText = text.trim();
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                String trimmedActualText = core.getText().trim();
                boolean equals = trimmedActualText.equals(trimmedExpectedText);
                setPassState(equals);
                setReturningObject(equals);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForTextContains(String... text) {
        return pWaitForTextContains(text);
    }

    private boolean pWaitForTextContains(final String... texts) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                String currentText = core.getText();
                boolean contains = true;
                for (String text : texts) {
                    if (!currentText.contains(text)) {
                        contains = false;
                        break;
                    }
                }
                setPassState(contains);
                setReturningObject(contains);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForTextContainsNot(String... texts) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                String currentText = core.getText();
                boolean gone = true;
                for (String text : texts) {
                    if (currentText.contains(text)) {
                        gone = false;
                        break;
                    }
                }
                setPassState(gone);
                setReturningObject(gone);
            }
        };
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForAttribute(final String attributeName) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean hasAttribute = core.getAttribute(attributeName) != null;
                setPassState(hasAttribute);
                setReturningObject(hasAttribute);
            }
        };
        ThrowablePackedResponse<Boolean> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForAttribute(final String attributeName, final String value) {
        final String trimmedExpectedAttributeValue = value.trim();
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                String attributeValue = core.getAttribute(attributeName);
                boolean hasAttribute = attributeValue != null && trimmedExpectedAttributeValue.equals(attributeValue.trim());
                setPassState(hasAttribute);
                setReturningObject(hasAttribute);
            }
        };
        ThrowablePackedResponse<Boolean> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForAttributeContains(final String attributeName, final String value) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                String attribute = core.getAttribute(attributeName);
                boolean hasAttribute = attribute != null && attribute.contains(value);
                setPassState(hasAttribute);
                setReturningObject(hasAttribute);
            }
        };
        ThrowablePackedResponse<Boolean> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForAttributeContainsNot(final String attributeName, final String value) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                String attribute = core.getAttribute(attributeName);
                boolean hasNotAttribute = attribute == null || attribute.contains(value) == false;
                setPassState(hasNotAttribute);
                setReturningObject(hasNotAttribute);
            }
        };
        ThrowablePackedResponse<Boolean> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForCssClassIsPresent(final String className) {
        return waitForAttributeContains("class", className);
    }

    @Override
    public boolean waitForCssClassIsNotPresent(final String className) {
        return waitForAttributeContainsNot("class", className);
    }

    @Override
    public boolean waitForIsSelectable() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean selectable = core.isSelectable();
                setPassState(selectable);
                setReturningObject(selectable);
            }
        };
        ThrowablePackedResponse<Boolean> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public boolean waitForIsNotSelectable() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false); // in case of an error while executing webelement method -> no exception has to be thrown
                setSkipThrowingException(true);

                boolean notSelectable = !core.isSelectable();
                setPassState(notSelectable);
                setReturningObject(notSelectable);
            }
        };
        ThrowablePackedResponse<Boolean> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, pageOverrides.getTimeout());
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }
}
