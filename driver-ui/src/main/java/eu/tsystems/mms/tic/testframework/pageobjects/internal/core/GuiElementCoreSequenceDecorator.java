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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
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
@Deprecated
public class GuiElementCoreSequenceDecorator implements GuiElementCore, Loggable {

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
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
        Timer.Sequence<WebElement> sequence = new Timer.Sequence<WebElement>() {
            @Override
            public void run() {
                WebElement webElement = guiElementCore.getWebElement();
                setReturningObject(webElement);
            }
        };
        sequence.setSkipThrowingException(true);
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<WebElement> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public List<WebElement> findWebElements() {
        return guiElementCore.findWebElements();
    }

    @Override
    public WebElement findFirstWebElement() {
        return guiElementCore.findFirstWebElement();
    }

    @Override
    public By getBy() {
        return guiElementCore.getBy();
    }

    @Override
    public GuiElementCore scrollToElement() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.scrollToElement();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore scrollToElement(int yOffset) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.scrollToElement(yOffset);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore select() {
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
        return this;
    }

    @Override
    public GuiElementCore deselect() {
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
        return this;
    }

    @Override
    public GuiElementCore type(final String text) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.type(text);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore click() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                guiElementCore.click();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "click", guiElementCore::clickJS);
        return this;
    }

    private void checkForClickingJSAlternativeOrExit(ThrowablePackedResponse throwablePackedResponse, String action, Runnable runnable) {
        if (throwablePackedResponse.hasTimeoutException()) {

            if (!UseJSAlternatives.class.isAssignableFrom(guiElementCore.getClass()) || !IGuiElement.Properties.USE_JS_ALTERNATIVES.asBool()) {
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
                log().warn(action + "() failed on " + guiElementCore + ". Trying fallback " + action + "JS().");
                runnable.run();
                return;
            }

            throwablePackedResponse.finalizeTimer();
        }
    }

    @Override
    public GuiElementCore clickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.clickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore clickAbsolute() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.clickAbsolute();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore mouseOverAbsolute2Axis() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.mouseOverAbsolute2Axis();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore submit() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.submit();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(final CharSequence... charSequences) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.sendKeys(charSequences);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore clear() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.clear();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
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
    public IGuiElement getSubElement(final By by, final String description) {
        Timer.Sequence<IGuiElement> sequence = new Timer.Sequence<IGuiElement>() {
            @Override
            public void run() {
                IGuiElement guiElement = guiElementCore.getSubElement(by, description);
                setReturningObject(guiElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<IGuiElement> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public IGuiElement getSubElement(Locate locator) {
        Timer.Sequence<IGuiElement> sequence = new Timer.Sequence<IGuiElement>() {
            @Override
            public void run() {
                IGuiElement guiElement = guiElementCore.getSubElement(locator);
                setReturningObject(guiElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<IGuiElement> throwablePackedResponse = timerWrapper.executeSequence(sequence);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public IGuiElement getSubElement(By by) {
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
    public GuiElementCore mouseOver() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                guiElementCore.mouseOver();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore mouseOverJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.mouseOverJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
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
    public GuiElementCore doubleClick() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.doubleClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "doubleClick", guiElementCore::doubleClickJS);
        return this;
    }

    @Override
    public GuiElementCore highlight() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.highlight();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.logThrowableAndReturnResponse();
        return this;
    }

    @Override
    public GuiElementCore swipe(final int offsetX, final int offSetY) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.swipe(offsetX, offSetY);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
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
    public GuiElementCore rightClick() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.rightClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "rightClick", guiElementCore::rightClickJS);
        return this;
    }

    @Override
    public GuiElementCore rightClickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.rightClickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore doubleClickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                guiElementCore.doubleClickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = timerWrapper.executeSequence(sequence);
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public File takeScreenshot() {
        return guiElementCore.takeScreenshot();
    }

    @Override
    public boolean isPresent() {
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
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
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isEnabled() {
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
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
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
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
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
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
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isVisible(boolean complete) {
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
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
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isSelected() {
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
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
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
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
    public boolean isDisplayedFromWebElement() {
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean displayedFromWebElement = guiElementCore.isDisplayedFromWebElement();
                setReturningObject(displayedFromWebElement);
                setPassState(displayedFromWebElement);
            }
        };
        sequence.setSkipThrowingException(true);
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isSelectable() {
        int prevTimeout = timerWrapper.getTimeoutInSeconds();
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
        timerWrapper.setTimeoutInSeconds(1);
        ThrowablePackedResponse<Boolean> response = timerWrapper.executeSequence(sequence);
        timerWrapper.setTimeoutInSeconds(prevTimeout);
        return response.logThrowableAndReturnResponse();
    }

}
