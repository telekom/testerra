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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByImage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.MouseActions;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebElementProxy;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesktopGuiElementCore implements GuiElementCore, Loggable {

    private By by;

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiElement.class);

    private static final int delayAfterFindInMilliSeconds = PropertyManager.getIntProperty(TesterraProperties.DELAY_AFTER_GUIELEMENT_FIND_MILLIS);

    private final WebDriver webDriver;

    private final GuiElementData guiElementData;

    public DesktopGuiElementCore(
            By by,
            WebDriver webDriver,
            GuiElementData guiElementData
    ) {
        this.by = by;
        this.webDriver = webDriver;
        this.guiElementData = guiElementData;
    }

    /**
     * Executes a search without a timeout (instantaneously). This method should be called inside a sequence to provide the timeout.
     *
     * @return the WebElement located by by-locator.
     */
    private int find() {
        int findCounter = -1;
        int numberOfFoundElements = 0;
        long start = System.currentTimeMillis();
        guiElementData.webElement = null;
        GuiElementCore parent = guiElementData.parent;
        Exception notFoundCause = null;
        try {
            List<WebElement> elements;
            if (parent != null) {
                elements = parent.getWebElement().findElements(by);
            } else {
                elements = webDriver.findElements(by);
            }
            if (elements != null) {
                final Locate selector = guiElementData.guiElement.getLocator();
                Predicate<WebElement> filter = selector.getFilter();
                if (filter != null) {
                    elements.removeIf(webElement -> !filter.test(webElement));
                }
                if (selector.isUnique() && elements.size() > 1) {
                    throw new Exception("To many WebElements found (" + elements.size() + ")");
                }
                numberOfFoundElements = elements.size();

                findCounter = setWebElement(elements);
            }
        } catch (Exception e) {
            //guiElementData.executionLog.addMessage("WebElement was not found:" + e.toString());
            notFoundCause = e;
        }
        throwExceptionIfWebElementIsNull(notFoundCause);

        logTimings(start, findCounter);

        if (delayAfterFindInMilliSeconds > 0) {
            TimerUtils.sleep(delayAfterFindInMilliSeconds);
        }
        return numberOfFoundElements;
    }

    private int setWebElement(List<WebElement> elements) {
        int numberOfFoundElements = elements.size();
        if (numberOfFoundElements < guiElementData.index + 1) {
            throw new StaleElementReferenceException("Request index of GuiElement was " + guiElementData.index + ", but only " + numberOfFoundElements + " were found. There you go, GuiElementList-Fanatics!");
        }

        if (numberOfFoundElements > 0) {
            // webelement to set
            WebElement webElement = elements.get(Math.max(0, guiElementData.index));

            // check for shadowRoot
            if (guiElementData.shadowRoot) {
                Object o = JSUtils.executeScript(webDriver, "return arguments[0].shadowRoot", webElement);
                if (o instanceof WebElement) {
                    webElement = (WebElement) o;
                }
            }

            // proxy the web element for logging
            WebElementProxy webElementProxy = new WebElementProxy(webDriver, webElement);
            Class[] interfaces = ObjectUtils.getAllInterfacesOf(webElement);
            webElement = ObjectUtils.simpleProxy(WebElement.class, webElementProxy, interfaces);

            // set webelement
            guiElementData.webElement = webElement;
            GuiElementData.WEBELEMENT_MAP.put(webElement, guiElementData.guiElement);

            // find timings
            int findCounter = Timings.raiseFindCounter();
            if (numberOfFoundElements > 1 && guiElementData.index == -1) {
                LOGGER.debug("find()#" + findCounter + ": GuiElement " + toString() + " was found " + numberOfFoundElements + " times. Will use the first occurrence.");
            } else {
                LOGGER.debug("find()#" + findCounter + ": GuiElement " + toString() + " was found.");
            }
            return findCounter;
        } else {
            LOGGER.debug("find(): GuiElement " + toString() + " was NOT found. Element list has 0 entries.");
            return -1;
        }
    }

    private void throwExceptionIfWebElementIsNull(Exception cause) {
        if (guiElementData.webElement == null) {
            String message = "GuiElement not found: " + toString();

            MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
            if (currentMethodContext != null) {
                currentMethodContext.errorContext().setThrowable(message, cause);
            }

            throw new ElementNotFoundException(message, cause);
        }
    }

    private void logTimings(long start, int findCounter) {
        if (findCounter != -1) {
            GuiElementCore parent = guiElementData.parent;
            long end = System.currentTimeMillis();
            long ms = end - start;
            if (parent != null) {
                Timings.TIMING_GUIELEMENT_FIND_WITH_PARENT.put(findCounter, ms);
            } else {
                Timings.TIMING_GUIELEMENT_FIND.put(findCounter, ms);
            }

            final long limit = Timings.LARGE_LIMIT;
            if (ms >= limit) {
                LOGGER.warn("find()#" + findCounter + " of GuiElement " + toString() + " took longer than " + limit + " ms.");
            }
        }
    }

    @Override
    public WebElement getWebElement() {
        find();
        return guiElementData.webElement;
    }

    @Override
    public By getBy() {
        return by;
    }

    @Override
    @Deprecated
    public void scrollToElement(int yOffset) {
        pScrollToElement(yOffset);
    }

    @Override
    public void scrollIntoView(Point offset) {
        JSUtils utils = new JSUtils();
        utils.scrollToCenter(guiElementData.webDriver, getWebElement(), offset);
    }

    /**
     * Private scroll to element.
     */
    private void pScrollToElement(int yOffset) {
        final Point location = getWebElement().getLocation();
        final int x = location.getX();
        final int y = location.getY() - yOffset;
        LOGGER.trace("Scrolling into view: " + x + ", " + y);

        JSUtils.executeScript(webDriver, "scroll(" + x + ", " + y + ");");
    }

    @Override
    public void select() {
        if (!isSelected()) {
            click();
        }
    }

    @Override
    public void deselect() {
        if (isSelected()) {
            click();
        }
    }

    @Override
    public void type(String text) {
        pType(text);
    }

    private void pType(String text) {
        if (text == null) {
            LOGGER.warn("Text to type is null. Typing nothing.");
            return;
        }
        if (text.isEmpty()) {
            LOGGER.warn("Text to type is empty!");
        }

        find();

        WebElement webElement = guiElementData.webElement;
        webElement.clear();
        webElement.sendKeys(text);

        String valueProperty = webElement.getAttribute("value");
        if (valueProperty != null) {
            if (!valueProperty.equals(text)) {
                LOGGER.warn("Writing text to input field didn't work. Trying again.");

                webElement.clear();
                webElement.sendKeys(text);

                if (!webElement.getAttribute("value").equals(text)) {
                    LOGGER.error("Writing text to input field didn't work on second try!");
                }
            }
        } else {
            LOGGER.warn("Cannot perform value check after type() because this element doesn't have a value property. Consider using sendKeys() instead.");
        }
    }

    @Override
    public void click() {
        find();
        LOGGER.debug("click(): found element, adding ClickPath");
        LOGGER.debug("click(): added ClickPath, clicking relative");
        pClickRelative(this, webDriver, guiElementData.webElement);
        LOGGER.debug("click(): clicked relative");
    }

    private void pClickRelative(
            GuiElementCore guiElementCore,
            WebDriver driver,
            WebElement webElement
    ) {
        By by = guiElementCore.getBy();
        if (by instanceof ByImage) {
            ByImage byImage = (ByImage) by;
            int x = byImage.getCenterX();
            int y = byImage.getCenterY();
            LOGGER.info("Image Click on image webElement at " + x + "," + y);
            JSUtils.executeJavaScriptMouseAction(driver, webElement, JSMouseAction.CLICK, x, y);
        } else {
            LOGGER.trace("Standard click on: " + guiElementCore.toString());
            // Start the StopWatch for measuring the loading time of a Page
            StopWatch.startPageLoad(driver);
            webElement.click();
        }
    }

    @Override
    public void submit() {
        getWebElement().submit();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        getWebElement().sendKeys(charSequences);
    }

    @Override
    public void clear() {
        getWebElement().clear();
    }

    @Override
    public String getTagName() {
        find();
        String tagName = guiElementData.webElement.getTagName();
        return tagName;
    }

    @Override
    public String getAttribute(String attributeName) {
        find();
        String attribute = guiElementData.webElement.getAttribute(attributeName);
        return attribute;
    }

    @Override
    public boolean isSelected() {
        find();
        boolean selected = guiElementData.webElement.isSelected();
        return selected;
    }

    @Override
    public boolean isEnabled() {
        find();
        boolean enabled = guiElementData.webElement.isEnabled();
        return enabled;
    }

    @Override
    public String getText() {
        find();
        String text = guiElementData.webElement.getText();
        return text;
    }

    @Override
    public GuiElement getSubElement(By by, String description) {
        return getSubElement(by).setName(description);
    }

    public GuiElement getSubElement(Locate locate) {
        FrameLogic frameLogic = guiElementData.frameLogic;
        GuiElement[] frames = null;
        if (frameLogic != null) {
            frames = frameLogic.getFrames();
        }

        String abstractLocatorString = locate.getBy().toString();
        if (abstractLocatorString.toLowerCase().contains("xpath")) {
            int i = abstractLocatorString.indexOf(":") + 1;
            String xpath = abstractLocatorString.substring(i).trim();
            String prevXPath = xpath;
            // Check if locator does not start with dot, ignoring a leading parenthesis for choosing the n-th element
            if (xpath.startsWith("/")) {
                xpath = xpath.replaceFirst("/", "./");
                log().warn(String.format("Replaced absolute xpath locator \"%s\" to relative: \"%s\"", prevXPath, xpath));
                locate = Locate.by(By.xpath(xpath));
            } else if (!xpath.startsWith(".")) {
                xpath = "./" + xpath;
                log().warn(String.format("Added relative xpath locator for children to \"%s\": \"%s\"", prevXPath, xpath));
                locate = Locate.by(By.xpath(xpath));
            }
        }

        GuiElement subElement = new GuiElement(webDriver, locate, frames);
        subElement.setParent(this);
        return subElement;
    }

    @Override
    public GuiElement getSubElement(By by) {
        return getSubElement(Locate.by(by));
    }

    @Override
    public boolean isDisplayed() {
        return pIsDisplayed();
    }

    private boolean pIsDisplayed() {
        if (!isPresent()) {
            return false;
        }

        // in isPresent(), find() was executed which should set "webElement" or throw an exception
        WebElement webElement = guiElementData.webElement;
        return webElement.isDisplayed();
    }

    @Override
    public boolean isVisible(final boolean complete) {
        if (!isDisplayed()) return false;
        Rectangle viewport = WebDriverUtils.getViewport(webDriver);
        final WebElement webElement = getWebElement();
        // getRect doesn't work
        Point elementLocation = webElement.getLocation();
        Dimension elementSize = webElement.getSize();
        java.awt.Rectangle viewportRect = new java.awt.Rectangle(viewport.x, viewport.y, viewport.width, viewport.height);
        java.awt.Rectangle elementRect = new java.awt.Rectangle(elementLocation.x, elementLocation.y, elementSize.width, elementSize.height);
        return ((complete && viewportRect.contains(elementRect)) || viewportRect.intersects(elementRect));
    }

    @Override
    public boolean isSelectable() {
        return pIsSelectable();
    }

    private boolean pIsSelectable() {
        boolean wasSelected;
        try {
            wasSelected = isSelected();
        } catch (WebDriverException e) {
            if (e.getMessage().toLowerCase().contains("unable to determine if element is selected")) {
                // if webdriver can't get selection status, we can assume ti is not selectable. We have no way of checking anyway
                return false;
            } else {
                throw e;
            }
        }
        // if the selection status check did not lead to an error in WebDriver, this element might be selectable. To test this, we
        // try to select or deselect it, depending on its status. If the selection status changes, it is selectable.
        click();
        boolean isSelected = isSelected();
        boolean selectionStatusChanged = wasSelected != isSelected;
        if (selectionStatusChanged) {
            click();
        }
        return selectionStatusChanged;
    }


    @Override
    public Point getLocation() {
        find();
        Point location = guiElementData.webElement.getLocation();
        return location;
    }

    @Override
    public Dimension getSize() {
        find();
        Dimension size = guiElementData.webElement.getSize();
        return size;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        find();
        String cssValue = guiElementData.webElement.getCssValue(cssIdentifier);
        return cssValue;
    }

    @Override
    public void mouseOver() {
        pMouseOver();
    }

    /**
     * Hidden method.
     */
    private void pMouseOver() {
        highlight(new Color(255, 255, 0));
        WebElement webElement = getWebElement();
        final Point location = webElement.getLocation();
        final int x = location.getX();
        final int y = location.getY();
        webElement.getSize();
        LOGGER.debug("MouseOver: " + toString() + " at x: " + x + " y: " + y);

        Actions action = new Actions(webDriver);
        action.moveToElement(webElement).build().perform();
    }

    @Override
    public boolean isPresent() {
        try {
            LOGGER.debug("isPresent(): trying to find WebElement");
            find();
        } catch (Exception e) {
            LOGGER.debug("isPresent(): Element not found: " + by, e);
            return false;
        }
        return true;
    }

    @Override
    public Select getSelectElement() {
        find();
        Select select = new Select(guiElementData.webElement);
        return select;
    }


    @Override
    public List<String> getTextsFromChildren() {
        return pGetTextsFromChildren();
    }

    private List<String> pGetTextsFromChildren() {
        find();
        // find() not necessary here, because findElements() is called, which calls find().
        List<WebElement> childElements = guiElementData.webElement.findElements(By.xpath(".//*"));

        ArrayList<String> childTexts = new ArrayList<String>();
        for (WebElement childElement : childElements) {
            String text = childElement.getText();
            if (text != null && !text.equals("")) {
                childTexts.add(text);
            }
        }

        return childTexts;
    }

    @Override
    public boolean anyFollowingTextNodeContains(String contains) {
        By byStringContain = By.xpath(String.format(".//*[contains(text(),\"%s\")]", contains));
        GuiElement subElement = getSubElement(byStringContain, null);
        return subElement.isPresent();
    }

    @Override
    public void doubleClick() {
        find();
        WebElement webElement = guiElementData.webElement;
        By localBy = getBy();

        WebDriverRequest driverRequest = WebDriverManager.getRelatedWebDriverRequest(webDriver);

        if (localBy instanceof ByImage) {
            ByImage byImage = (ByImage) localBy;
            int x = byImage.getCenterX();
            int y = byImage.getCenterY();
            LOGGER.info("Image Double Click on image webElement at " + x + "," + y);
            JSUtils.executeJavaScriptMouseAction(webDriver, webElement, JSMouseAction.DOUBLE_CLICK, x, y);
        } else if (Browsers.safari.equalsIgnoreCase(driverRequest.browser)) {
            LOGGER.info("Safari double click workaround");
            JSUtils.executeJavaScriptMouseAction(webDriver, webElement, JSMouseAction.DOUBLE_CLICK, 0, 0);
        } else {
            final Actions actions = new Actions(webDriver);
            final Action action = actions.moveToElement(webElement).doubleClick(webElement).build();

            try {
                action.perform();
            } catch (InvalidElementStateException e) {
                LOGGER.error("Error performing double click", e);
                LOGGER.info("Retrying double click with click-click");
                actions.moveToElement(webElement).click().click().build().perform();
            }
        }
    }

    @Override
    public void highlight(Color color) {
        LOGGER.debug("highlight(): starting highlight");
        find();
        JSUtils.highlightWebElement(webDriver, this.getWebElement(), color);
        LOGGER.debug("highlight(): finished highlight");
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        MouseActions.swipeElement(guiElementData.guiElement, offsetX, offSetY);
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        clear();
        sendKeys(textToInput);
        int valueLength = getAttribute("value").length();
        return valueLength;
    }

    @Override
    public int getNumberOfFoundElements() {

        if (isPresent()) {
            return find();
        }

        return 0;
    }

    /**
     * Build a nice string.
     *
     * @return .
     */
    @Override
    public String toString() {
        return guiElementData.toString();
    }

    @Override
    public void rightClick() {
        find();
        Actions actions = new Actions(webDriver);
        actions.moveToElement(guiElementData.webElement).contextClick().build().perform();
    }

    @Override
    public File takeScreenshot() {
        final WebElement element = getWebElement();
        final boolean isSelenium4 = false;

        if (isSelenium4) {
            return element.getScreenshotAs(OutputType.FILE);
        } else {
            if (!isVisible(false)) {
                scrollIntoView();
            }
            Rectangle viewport = WebDriverUtils.getViewport(webDriver);
            try {
                final TakesScreenshot driver = ((TakesScreenshot) guiElementData.webDriver);

                File screenshot = driver.getScreenshotAs(OutputType.FILE);
                BufferedImage fullImg = ImageIO.read(screenshot);

                Point point = element.getLocation();
                int eleWidth = element.getSize().getWidth();
                int eleHeight = element.getSize().getHeight();

                BufferedImage eleScreenshot = fullImg.getSubimage(
                        point.getX() - viewport.getX(),
                        point.getY() - viewport.getY(),
                        eleWidth,
                        eleHeight
                );
                ImageIO.write(eleScreenshot, "png", screenshot);
                return screenshot;
            } catch (IOException e) {
                LOGGER.error(String.format("%s unable to take screenshot: %s ", this.guiElementData, e));
            }
        }

        return null;
    }

    @Override
    public void clickJS() {
        JSUtils utils = new JSUtils();
        utils.click(guiElementData.webDriver, getWebElement());
    }

    @Override
    public void clickAbsolute() {
        JSUtils utils = new JSUtils();
        utils.clickAbsolute(guiElementData.webDriver, getWebElement());
    }

    @Override
    public void mouseOverAbsolute2Axis() {
        JSUtils utils = new JSUtils();
        utils.mouseOverAbsolute2Axis(guiElementData.webDriver, getWebElement());
    }

    @Override
    public void mouseOverJS() {
        JSUtils utils = new JSUtils();
        utils.mouseOver(guiElementData.webDriver, getWebElement());
    }

    @Override
    public void rightClickJS() {
        JSUtils utils = new JSUtils();
        utils.rightClick(guiElementData.webDriver, getWebElement());
    }

    @Override
    public void doubleClickJS() {
        JSUtils utils = new JSUtils();
        utils.doubleClick(guiElementData.webDriver, getWebElement());
    }
}
