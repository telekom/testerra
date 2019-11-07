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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.frames;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Created by rnhb on 24.02.2015.
 */
public class FrameAwareWebElementDecorator implements WebElement {

    private WebElement webElement;
    private FrameLogic frameLogic;

    public FrameAwareWebElementDecorator(WebElement element, GuiElement[] frames, WebDriver driver) {
        this.webElement = element;
        frameLogic = new FrameLogic(driver, frames);
    }

    @Override
    public void click() {
        frameLogic.switchToCorrectFrame();
        webElement.click();
        frameLogic.switchToDefaultFrame();
    }

    @Override
    public void submit() {
        frameLogic.switchToCorrectFrame();
        webElement.submit();
        frameLogic.switchToDefaultFrame();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        frameLogic.switchToCorrectFrame();
        webElement.sendKeys(charSequences);
        frameLogic.switchToDefaultFrame();
    }

    @Override
    public void clear() {
        frameLogic.switchToCorrectFrame();
        webElement.clear();
        frameLogic.switchToDefaultFrame();
    }

    @Override
    public String getTagName() {
        frameLogic.switchToCorrectFrame();
        String tagName = webElement.getTagName();
        frameLogic.switchToDefaultFrame();
        return tagName;
    }

    @Override
    public String getAttribute(String s) {
        frameLogic.switchToCorrectFrame();
        String attribute = webElement.getAttribute(s);
        frameLogic.switchToDefaultFrame();
        return attribute;
    }

    @Override
    public boolean isSelected() {
        frameLogic.switchToCorrectFrame();
        boolean selected = webElement.isSelected();
        frameLogic.switchToDefaultFrame();
        return selected;
    }

    @Override
    public boolean isEnabled() {
        frameLogic.switchToCorrectFrame();
        boolean enabled = webElement.isEnabled();
        frameLogic.switchToDefaultFrame();
        return enabled;
    }

    @Override
    public String getText() {
        frameLogic.switchToCorrectFrame();
        String text = webElement.getText();
        frameLogic.switchToDefaultFrame();
        return text;
    }

    @Override
    public List<WebElement> findElements(By by) {
        frameLogic.switchToCorrectFrame();
        List<WebElement> elements = webElement.findElements(by);
        frameLogic.switchToDefaultFrame();
        return elements;
    }

    @Override
    public WebElement findElement(By by) {
        frameLogic.switchToCorrectFrame();
        WebElement element = webElement.findElement(by);
        frameLogic.switchToDefaultFrame();
        return element;
    }

    @Override
    public boolean isDisplayed() {
        frameLogic.switchToCorrectFrame();
        boolean displayed = webElement.isDisplayed();
        frameLogic.switchToDefaultFrame();
        return displayed;
    }

    @Override
    public Point getLocation() {
        frameLogic.switchToCorrectFrame();
        Point location = webElement.getLocation();
        frameLogic.switchToDefaultFrame();
        return location;
    }

    @Override
    public Dimension getSize() {
        frameLogic.switchToCorrectFrame();
        Dimension size = webElement.getSize();
        frameLogic.switchToDefaultFrame();
        return size;
    }

    @Override
    public Rectangle getRect() {
        frameLogic.switchToCorrectFrame();
        Rectangle rect = webElement.getRect();
        frameLogic.switchToDefaultFrame();
        return rect;
    }

    @Override
    public String getCssValue(String s) {
        frameLogic.switchToCorrectFrame();
        String cssValue = webElement.getCssValue(s);
        frameLogic.switchToDefaultFrame();
        return cssValue;
    }

    @Override
    public String toString() {
        return webElement.toString();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        frameLogic.switchToCorrectFrame();
        X screenshotAs = webElement.getScreenshotAs(outputType);
        frameLogic.switchToDefaultFrame();
        return screenshotAs;
    }
}
