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
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
public class GuiElementCoreSequenceDecorator extends AbstractGuiElementCoreDecorator implements Loggable {

    private static final TestController.Overrides overrides = Testerra.injector.getInstance(TestController.Overrides.class);

    public GuiElementCoreSequenceDecorator(GuiElementCore core) {
        super(core);
    }

    /**
     * If the supplier returns TRUE and no {@link Throwable} was catched,
     * the sequence ends immediately
     * Otherwise it throws a {@link TimeoutException}
     * @param runnable
     */
    private void sequenced(Supplier<Boolean> runnable) {

        Sequence sequence = new Sequence()
                .setTimeoutMs(overrides.getTimeoutInSeconds()*1000)
                .setPauseMs(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong());

        AtomicReference<Throwable> atomicThrowable = new AtomicReference<>();
        AtomicBoolean atomicSuccess = new AtomicBoolean();
        sequence.run(() -> {
            try {
                atomicSuccess.set(runnable.get());
            } catch (Throwable throwable) {
                atomicThrowable.set(throwable);
                atomicSuccess.set(false);
            }
            return atomicSuccess.get();
        });

        if (!atomicSuccess.get()) {
            throw new TimeoutException("Sequence timed out after " + sequence.getDurationMs()/1000 + "s", atomicThrowable.get());
        }
    }

    @Override
    public WebElement findWebElement() {
        AtomicReference<WebElement> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.findWebElement());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        sequenced(() -> {
            decoratedCore.findWebElement(consumer);
            return true;
        });
    }

    @Override
    public void scrollToElement(int yOffset) {
        sequenced(() -> {
            decoratedCore.scrollToElement(yOffset);
            return true;
        });
    }

    @Override
    public void scrollIntoView(Point offset) {
        sequenced(() -> {
            decoratedCore.scrollIntoView(offset);
            return true;
        });
    }

    @Override
    public void select() {
        sequenced(() -> {
            decoratedCore.select();
            return decoratedCore.isSelected();
        });
    }

    @Override
    public void deselect() {
        sequenced(() -> {
            decoratedCore.deselect();
            return !decoratedCore.isSelected();
        });
    }

    @Override
    public void type(final String text) {
        sequenced(() -> {
            decoratedCore.type(text);
            return true;
        });
    }

    @Override
    public void click() {
        sequenced(() -> {
            decoratedCore.click();
            return true;
        });
    }

    @Override
    public void submit() {
        sequenced(() -> {
            decoratedCore.submit();
            return true;
        });
    }

    @Override
    public void sendKeys(final CharSequence... charSequences) {
        sequenced(() -> {
            decoratedCore.sendKeys(charSequences);
            return true;
        });
    }

    @Override
    public void clear() {
        sequenced(() -> {
            decoratedCore.clear();
            return true;
        });
    }

    @Override
    public String getTagName() {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getTagName());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public Point getLocation() {
        AtomicReference<Point> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getLocation());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public Dimension getSize() {
        AtomicReference<Dimension> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getSize());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public String getCssValue(final String cssIdentifier) {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getCssValue(cssIdentifier));
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public Select getSelectElement() {
        AtomicReference<Select> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getSelectElement());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public List<String> getTextsFromChildren() {
        AtomicReference<List<String>> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getTextsFromChildren());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public void doubleClick() {
        sequenced(() -> {
            decoratedCore.doubleClick();
            return true;
        });
    }

    @Override
    public void highlight(Color color) {
        sequenced(() -> {
            decoratedCore.highlight(color);
            return true;
        });
    }

    @Override
    public void swipe(final int offsetX, final int offSetY) {
        sequenced(() -> {
            decoratedCore.swipe(offsetX, offSetY);
            return true;
        });
    }

    @Override
    public int getLengthOfValueAfterSendKeys(final String textToInput) {
        AtomicInteger atomicReference = new AtomicInteger();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getLengthOfValueAfterSendKeys(textToInput));
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public int getNumberOfFoundElements() {
        AtomicInteger atomicReference = new AtomicInteger();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getNumberOfFoundElements());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public File takeScreenshot() {
        AtomicReference<File> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.takeScreenshot());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public boolean isPresent() {
        AtomicBoolean atomicReference = new AtomicBoolean();
        sequenced(() -> {
            atomicReference.set(decoratedCore.isPresent());
            return atomicReference.get();
        });
        return atomicReference.get();
    }

    @Override
    public boolean isEnabled() {
        AtomicBoolean atomicReference = new AtomicBoolean();
        sequenced(() -> {
            atomicReference.set(decoratedCore.isEnabled());
            return atomicReference.get();
        });
        return atomicReference.get();
    }

    @Override
    public boolean isDisplayed() {
        AtomicBoolean atomicReference = new AtomicBoolean();
        sequenced(() -> {
            atomicReference.set(decoratedCore.isDisplayed());
            return atomicReference.get();
        });
        return atomicReference.get();
    }

    @Override
    public boolean isVisible(boolean complete) {
        AtomicBoolean atomicReference = new AtomicBoolean();
        sequenced(() -> {
            atomicReference.set(decoratedCore.isVisible(complete));
            return atomicReference.get();
        });
        return atomicReference.get();
    }

    @Override
    public boolean isSelected() {
        AtomicBoolean atomicReference = new AtomicBoolean();
        sequenced(() -> {
            atomicReference.set(decoratedCore.isSelected());
            return atomicReference.get();
        });
        return atomicReference.get();
    }

    @Override
    public String getText() {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getText());
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public String getAttribute(final String attributeName) {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        sequenced(() -> {
            atomicReference.set(decoratedCore.getAttribute(attributeName));
            return true;
        });
        return atomicReference.get();
    }

    @Override
    public boolean isSelectable() {
        AtomicBoolean atomicReference = new AtomicBoolean();
        sequenced(() -> {
            atomicReference.set(decoratedCore.isSelectable());
            return atomicReference.get();
        });
        return atomicReference.get();
    }
}
