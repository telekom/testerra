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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
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

    private final GuiElementData guiElementData;
    private final GuiElementCore decoratedCore;

    public GuiElementCoreSequenceDecorator(GuiElementCore decoratedCore, GuiElementData guiElementData) {
        this.decoratedCore = decoratedCore;
        this.guiElementData = guiElementData;
    }

    private TimerWrapper getTimerWrapper() {
        return Testerra.injector.getInstance(IWebDriverManager.class).getTimerWrapper(guiElementData.getWebDriver());
    }

    @Override
    public WebElement getWebElement() {
        Timer.Sequence<WebElement> sequence = new Timer.Sequence<WebElement>() {
            @Override
            public void run() {
                WebElement webElement = decoratedCore.getWebElement();
                setReturningObject(webElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<WebElement> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, 1);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public List<WebElement> findWebElements() {
        return decoratedCore.findWebElements();
    }

    @Override
    public WebElement findWebElement() {
        return decoratedCore.findWebElement();
    }

    @Override
    public By getBy() {
        return decoratedCore.getBy();
    }

    @Override
    public GuiElementCore scrollToElement() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.scrollToElement();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore scrollToElement(int yOffset) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.scrollToElement(yOffset);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore select() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.select();
                setPassState(decoratedCore.isSelected());
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore deselect() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.deselect();
                setPassState(!decoratedCore.isSelected());
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore type(final String text) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.type(text);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore click() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                decoratedCore.click();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "click", decoratedCore::clickJS);
        return this;
    }

    private void checkForClickingJSAlternativeOrExit(ThrowablePackedResponse throwablePackedResponse, String action, Runnable runnable) {
        if (throwablePackedResponse.hasTimeoutException()) {

            if (!UseJSAlternatives.class.isAssignableFrom(decoratedCore.getClass()) || !UiElement.Properties.USE_JS_ALTERNATIVES.asBool()) {
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
                log().warn(action + "() failed on " + decoratedCore + ". Trying fallback " + action + "JS().");
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
                decoratedCore.clickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore clickAbsolute() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.clickAbsolute();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore mouseOverAbsolute2Axis() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.mouseOverAbsolute2Axis();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore submit() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.submit();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(final CharSequence... charSequences) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.sendKeys(charSequences);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore clear() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.clear();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public String getTagName() {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String tagName = decoratedCore.getTagName();
                setReturningObject(tagName);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public Point getLocation() {
        Timer.Sequence<Point> sequence = new Timer.Sequence<Point>() {
            @Override
            public void run() {
                Point point = decoratedCore.getLocation();
                setReturningObject(point);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Point> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public Dimension getSize() {
        Timer.Sequence<Dimension> sequence = new Timer.Sequence<Dimension>() {
            @Override
            public void run() {
                Dimension dimension = decoratedCore.getSize();
                setReturningObject(dimension);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Dimension> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public String getCssValue(final String cssIdentifier) {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String cssValue = decoratedCore.getCssValue(cssIdentifier);
                setReturningObject(cssValue);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public GuiElementCore mouseOver() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                decoratedCore.mouseOver();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore mouseOverJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.mouseOverJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public Select getSelectElement() {
        Timer.Sequence<Select> sequence = new Timer.Sequence<Select>() {
            @Override
            public void run() {
                Select select = decoratedCore.getSelectElement();
                setReturningObject(select);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Select> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public List<String> getTextsFromChildren() {
        Timer.Sequence<List<String>> sequence = new Timer.Sequence<List<String>>() {
            @Override
            public void run() {
                List<String> stringList = decoratedCore.getTextsFromChildren();
                setReturningObject(stringList);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<List<String>> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public GuiElementCore doubleClick() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.doubleClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "doubleClick", decoratedCore::doubleClickJS);
        return this;
    }

    @Override
    public GuiElementCore highlight() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.highlight();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.logThrowableAndReturnResponse();
        return this;
    }

    @Override
    public GuiElementCore swipe(final int offsetX, final int offSetY) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.swipe(offsetX, offSetY);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(final String textToInput) {
        Timer.Sequence<Integer> sequence = new Timer.Sequence<Integer>() {
            @Override
            public void run() {
                Integer integer = decoratedCore.getLengthOfValueAfterSendKeys(textToInput);
                setReturningObject(integer);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Integer> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public int getNumberOfFoundElements() {
        Timer.Sequence<Integer> sequence = new Timer.Sequence<Integer>() {
            @Override
            public void run() {
                setReturningObject(0);
                int numberOfFoundElements = decoratedCore.getNumberOfFoundElements();
                setReturningObject(numberOfFoundElements);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Integer> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public GuiElementCore rightClick() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.rightClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());

        checkForClickingJSAlternativeOrExit(throwablePackedResponse, "rightClick", decoratedCore::rightClickJS);
        return this;
    }

    @Override
    public GuiElementCore rightClickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.rightClickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore doubleClickJS() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                decoratedCore.doubleClickJS();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public File takeScreenshot() {
        return decoratedCore.takeScreenshot();
    }

    @Override
    public boolean isPresent() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean present = decoratedCore.isPresent();
                setReturningObject(present);
                setPassState(present);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence,1);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isEnabled() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean enabled = decoratedCore.isEnabled();
                setReturningObject(enabled);
                setPassState(enabled);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence,1);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isDisplayed() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean displayed = decoratedCore.isDisplayed();
                setReturningObject(displayed);
                setPassState(displayed);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence,1);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isVisible(boolean complete) {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean visible = decoratedCore.isVisible(complete);
                setReturningObject(visible);
                setPassState(visible);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence,1);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isSelected() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean selected = decoratedCore.isSelected();
                setReturningObject(selected);
                setPassState(selected);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence,1);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public String getText() {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String text = decoratedCore.getText();
                setReturningObject(text);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public String getAttribute(final String attributeName) {
        Timer.Sequence<String> sequence = new Timer.Sequence<String>() {
            @Override
            public void run() {
                String attributeValue = decoratedCore.getAttribute(attributeName);
                setReturningObject(attributeValue);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean displayedFromWebElement = decoratedCore.isDisplayedFromWebElement();
                setReturningObject(displayedFromWebElement);
                setPassState(displayedFromWebElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence,1);
        return response.logThrowableAndReturnResponse();
    }

    @Override
    public boolean isSelectable() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean selectable = decoratedCore.isSelectable();
                setReturningObject(selectable);
                setPassState(selectable);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return response.logThrowableAndReturnResponse();
    }

}
