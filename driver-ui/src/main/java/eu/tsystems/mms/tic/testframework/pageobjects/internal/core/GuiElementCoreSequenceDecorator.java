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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
import java.awt.Color;
import java.io.File;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Wraps core methods into sequences, to improve stability.
 * Some status methods (the is... methods that return boolean) should, in contrast return the status immediately, without
 * retry in a sequence.
 * <p>
 * Created by rnhb on 12.08.2015.
 */
@Deprecated
public class GuiElementCoreSequenceDecorator extends GuiElementCoreDecorator implements Loggable {

    private final GuiElementData guiElementData;

    public GuiElementCoreSequenceDecorator(GuiElementCore core, GuiElementData guiElementData) {
        super(core);
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
                WebElement webElement = core.getWebElement();
                setReturningObject(webElement);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<WebElement> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, 1);
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public WebElement findWebElement() {
        return core.findWebElement();
    }

    @Override
    public By getBy() {
        return core.getBy();
    }

    @Override
    @Deprecated
    public GuiElementCore scrollToElement(int yOffset) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                core.scrollToElement(yOffset);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public GuiElementCore scrollIntoView(Point offset) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                core.scrollIntoView(offset);
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
                core.select();
                setPassState(core.isSelected());
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
                core.deselect();
                setPassState(!core.isSelected());
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
                core.type(text);
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
                core.click();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());

        return this;
    }

    @Override
    public GuiElementCore submit() {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                core.submit();
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
                core.sendKeys(charSequences);
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
                core.clear();
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
                String tagName = core.getTagName();
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
                Point point = core.getLocation();
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
                Dimension dimension = core.getSize();
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
                String cssValue = core.getCssValue(cssIdentifier);
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
                core.mouseOver();
            }
        };
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        throwablePackedResponse.finalizeTimer();
        return this;
    }

    @Override
    public Select getSelectElement() {
        Timer.Sequence<Select> sequence = new Timer.Sequence<Select>() {
            @Override
            public void run() {
                Select select = core.getSelectElement();
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
                List<String> stringList = core.getTextsFromChildren();
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
                core.doubleClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return this;
    }

    @Override
    public GuiElementCore highlight(Color color) {
        Timer.Sequence sequence = new Timer.Sequence() {
            @Override
            public void run() {
                core.highlight(color);
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
                core.swipe(offsetX, offSetY);
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
                Integer integer = core.getLengthOfValueAfterSendKeys(textToInput);
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
                int numberOfFoundElements = core.getNumberOfFoundElements();
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
                core.rightClick();
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return this;
    }

    @Override
    public File takeScreenshot() {
        return core.takeScreenshot();
    }

    @Override
    protected void beforeDelegation() {

    }

    @Override
    protected void afterDelegation() {

    }

    @Override
    public boolean isPresent() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean present = core.isPresent();
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

                boolean enabled = core.isEnabled();
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

                boolean displayed = core.isDisplayed();
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

                boolean visible = core.isVisible(complete);
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

                boolean selected = core.isSelected();
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
                String text = core.getText();
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
                String attributeValue = core.getAttribute(attributeName);
                setReturningObject(attributeValue);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<String> throwablePackedResponse = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return throwablePackedResponse.finalizeTimer();
    }

    @Override
    public boolean isSelectable() {
        Timer.Sequence<Boolean> sequence = new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                setReturningObject(false);
                setSkipThrowingException(true);

                boolean selectable = core.isSelectable();
                setReturningObject(selectable);
                setPassState(selectable);
            }
        };
        sequence.setSkipThrowingException(true);
        ThrowablePackedResponse<Boolean> response = getTimerWrapper().executeSequence(sequence, guiElementData.getTimeoutSeconds());
        return response.logThrowableAndReturnResponse();
    }

}
