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
 package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * Wraps core methods into sequences, to improve stability.
 * Some status methods (the is... methods that return boolean) should, in contrast return the status immediately, without
 * retry in a sequence.
 * <p>
 * Created by rnhb on 12.08.2015.
 */
public class GuiElementCoreSequenceDecorator implements GuiElementCore {

    private final GuiElementCore guiElementCore;
    private final GuiElementData guiElementData;
    private final TimerWrapper timerWrapper;

    public GuiElementCoreSequenceDecorator(GuiElementCore guiElementCore, GuiElementData guiElementData) {
        this.guiElementCore = guiElementCore;
        this.guiElementData = guiElementData;
        this.timerWrapper = guiElementData.timerWrapper;
    }

    @Override
    public WebElement getWebElement() {
        Timer.Sequence<WebElement> sequence = new Timer.Sequence<WebElement>() {
            @Override
            public void run() {
                WebElement webElement = guiElementCore.getWebElement();
                setReturningObject(webElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<WebElement> throwablePackedResponse = timerWrapper.executeShortIntervalSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public By getBy() {
        return guiElementCore.getBy();
    }

    @Override
    public void scrollToElement() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.scrollToElement();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void scrollToElement(int yOffset) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.scrollToElement(yOffset);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void center(Point offset) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.center(offset);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void select() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.select();
                setPassState(guiElementCore.isSelected());
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void deselect() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.deselect();
                setPassState(!guiElementCore.isSelected());
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void type(final String text) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.type(text);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void click() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                guiElementCore.click();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "click", guiElementCore::clickJS);
    }

    private void checkForClickingJSAlternativeOrExit(ThrowablePackedResponse throwablePackedResponse, String action, Runnable runnable) {
        if (throwablePackedResponse.hasTimeoutException()) {

            if (!UseJSAlternatives.class.isAssignableFrom(guiElementCore.getClass()) || !Flags.GUIELEMENT_USE_JS_ALTERNATIVES) {
                // we cannot use clickJS()
                throwablePackedResponse.finalizeTimer();
                return;
            }


            // this should be TimeoutException
            Throwable throwable = throwablePackedResponse.getTimeoutException();
            String message = throwable.getMessage();

            // get the real cause
            Throwable cause = throwable.getCause();
            if (cause != null) {
                if (cause.getMessage() != null) {
                    message += cause.getMessage();
                }
            }
            else {
                cause = throwable;
            }

            if (message == null) {
                message = "";
            }
            message = message.toLowerCase();
            if (    ElementNotVisibleException.class.isAssignableFrom(cause.getClass())
                    ||
                    message.contains("other element would receive the click") // from chrome driver
                    ||
                    message.contains("Element is obscured") // from edge driver
                    ||
                    message.contains("not clickable at point") // another chrome message (maybe FF with native events (emulation), too)
                    ) {
                guiElementData.getLogger().warn(action + "() failed on " + guiElementCore + ". Trying fallback " + action + "JS().");
                runnable.run();
                return;
            }

            throwablePackedResponse.finalizeTimer();
        }
    }

    @Override
    public void clickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.clickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void clickAbsolute() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.clickAbsolute();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void mouseOverAbsolute2Axis() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.mouseOverAbsolute2Axis();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void submit() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.submit();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void sendKeys(final CharSequence... charSequences) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.sendKeys(charSequences);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void clear() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.clear();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public String getTagName() {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String tagName = guiElementCore.getTagName();
                setReturningObject(tagName);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public GuiElement getSubElement(final By byLocator, final String description) {
        Timer.Sequence<GuiElement> sequence = new Timer.Sequence<GuiElement>() {
            @Override
            public void run() {
                GuiElement guiElement = guiElementCore.getSubElement(byLocator, description);
                setReturningObject(guiElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<GuiElement> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public GuiElement getSubElement(Locate locator) {
        Timer.Sequence<GuiElement> sequence = new Timer.Sequence<GuiElement>() {
            @Override
            public void run() {
                GuiElement guiElement = guiElementCore.getSubElement(locator);
                setReturningObject(guiElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<GuiElement> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public GuiElement getSubElement(By by) {
        return getSubElement(by, "");
    }

    @Override
    public Point getLocation() {
        Timer.Sequence<Point> sequence = new Timer.Sequence<Point>() {
            @Override
            public void run() {
                Point point = guiElementCore.getLocation();
                setReturningObject(point);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Point> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public Dimension getSize() {
        Timer.Sequence<Dimension> sequence = new Timer.Sequence<Dimension>() {
            @Override
            public void run() {
                Dimension dimension = guiElementCore.getSize();
                setReturningObject(dimension);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Dimension> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public String getCssValue(final String cssIdentifier) {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String cssValue = guiElementCore.getCssValue(cssIdentifier);
                setReturningObject(cssValue);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void mouseOver() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                guiElementCore.mouseOver();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void mouseOverJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.mouseOverJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public Select getSelectElement() {
        Timer.Sequence<Select> sequence = new Timer.Sequence<Select>() {
            @Override
            public void run() {
                Select select = guiElementCore.getSelectElement();
                setReturningObject(select);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Select> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public List<String> getTextsFromChildren() {
        Timer.Sequence<List<String>> sequence = new Timer.Sequence<List<String>>() {
            @Override
            public void run() {
                List<String> stringList = guiElementCore.getTextsFromChildren();
                setReturningObject(stringList);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<List<String>> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void doubleClick() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.doubleClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "doubleClick", guiElementCore::doubleClickJS);
    }

    @Override
    public void highlight() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.highlight();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public void swipe(final int offsetX, final int offSetY) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.swipe(offsetX, offSetY);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public int getLengthOfValueAfterSendKeys(final String textToInput) {
        Timer.Sequence<Integer> sequence = new Timer.Sequence<Integer>() {
            @Override
            public void run() {
                Integer integer = guiElementCore.getLengthOfValueAfterSendKeys(textToInput);
                setReturningObject(integer);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Integer> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public int getNumberOfFoundElements() {
        Timer.Sequence<Integer> sequence = new Timer.Sequence<Integer>() {
            @Override
            public void run() {
                setReturningObject(0);
                int numberOfFoundElements = guiElementCore.getNumberOfFoundElements();
                setReturningObject(numberOfFoundElements);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Integer> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void rightClick() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.rightClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "rightClick", guiElementCore::rightClickJS);
    }

    @Override
    public void rightClickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.rightClickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public void doubleClickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.doubleClickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
    }

    @Override
    public File takeScreenshot() {
        return guiElementCore.takeScreenshot();
    }

    @Override
    public boolean isPresent() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean present = guiElementCore.isPresent();
                setReturningObject(present);
                setPassState(present);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeShortIntervalSequence(sequence);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isEnabled() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean enabled = guiElementCore.isEnabled();
                setReturningObject(enabled);
                setPassState(enabled);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeShortIntervalSequence(sequence);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean anyFollowingTextNodeContains(final String contains) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                Boolean bool = guiElementCore.anyFollowingTextNodeContains(contains);
                setReturningObject(bool);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isDisplayed() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean displayed = guiElementCore.isDisplayed();
                setReturningObject(displayed);
                setPassState(displayed);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeShortIntervalSequence(sequence);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isVisible(boolean complete) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean visible = guiElementCore.isVisible(complete);
                setReturningObject(visible);
                setPassState(visible);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeShortIntervalSequence(sequence);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isSelected() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean selected = guiElementCore.isSelected();
                setReturningObject(selected);
                setPassState(selected);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeShortIntervalSequence(sequence);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public String getText() {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String text = guiElementCore.getText();
                setReturningObject(text);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public String getAttribute(final String attributeName) {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String attributeValue = guiElementCore.getAttribute(attributeName);
                setReturningObject(attributeValue);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public boolean isSelectable() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean selectable = guiElementCore.isSelectable();
                setReturningObject(selectable);
                setPassState(selectable);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeShortIntervalSequence(sequence);
        return response.logThrowableAndReturnResponse();
    }

}
