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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByImage;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.*;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebElementProxy;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rnhb on 12.08.2015.
 */
public class DesktopGuiElementCore implements GuiElementCore, UseJSAlternatives {

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
        return find(false);
    }

    private int find(final boolean enableHighlight) {
        guiElementData.executionLog.addMessage("Executing find().");
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
                guiElementData.executionLog.addMessage("Found " + elements.size() + " WebElements for the locator " + by);
                final Locate selector = guiElementData.guiElement.getLocator();
                if (selector.isUnique() && elements.size() > 1) {
                    throw new Exception("To many WebElements found (" + elements.size() + ")");
                }
                elements = applyFilters(elements, selector.getFilters());
                numberOfFoundElements = elements.size();

                findCounter = setWebElement(elements);
            }
        } catch (Exception e) {
            //guiElementData.executionLog.addMessage("WebElement was not found:" + e.toString());
            notFoundCause = e;
        }
        throwExceptionIfWebElementIsNull(notFoundCause);

        logTimings(start, findCounter);

        if (enableHighlight) {
            highlightWebElement();
        }

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

    private List<WebElement> applyFilters(List<WebElement> foundElements, List<WebElementFilter> webElementFilters) {
        if (webElementFilters == null || webElementFilters.isEmpty()) {
            LOGGER.debug("find(): No WebElementFilters existing, not filtering");
            return foundElements;
        } else {
            LOGGER.debug("find(): Filtering with WebElementFilters " + webElementFilters);
            List<WebElement> filteredElements = new LinkedList<WebElement>();
            for (WebElement webElement : foundElements) {
                boolean satisfiesAllFilters = true;
                for (WebElementFilter filter : webElementFilters) {
                    if (filter.isSatisfiedBy(webElement)) {
                        LOGGER.debug("find():" + webElement + " satisfies filter " + filter);
                    } else {
                        LOGGER.debug("find(): " + webElement + " does not satisfy filter " + filter);
                        satisfiesAllFilters = false;
                        break;
                    }
                }
                if (satisfiesAllFilters) {
                    LOGGER.debug("find(): " + webElement + " satisfied all filters");
                    filteredElements.add(webElement);
                } else {
                    LOGGER.debug("find(): " + webElement + " did not satisfy all filters");
                }
            }
            LOGGER.debug("Before Filtering: " + foundElements.size() + " WebElements, After Filtering: " + filteredElements.size() + " WebElements.");
            guiElementData.executionLog.addMessage(filteredElements.size() + " WebElements remaining after filtering. Removed " + (foundElements.size() - filteredElements.size() + " WebElements."));
            return filteredElements;
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

    private void highlightWebElement() {
        if (POConfig.isDemoMode()) {
            LOGGER.debug("find(): Highlighting WebElement");
            JSUtils.highlightWebElement(webDriver, guiElementData.webElement, 0, 0, 255); // blue
            LOGGER.debug("find(): Done highlighting WebElement");
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
        find(false);
        return guiElementData.webElement;
    }

    @Override
    public By getBy() {
        return by;
    }

    @Override
    public void scrollToElement() {
        pScrollToElement(0);
    }

    @Override
    public void scrollToElement(int yOffset) {
        pScrollToElement(yOffset);
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

        boolean skipValueCheck = false;
        if (text.contains("\n")) {
            skipValueCheck = true;
            LOGGER.warn("The text to type contains a linebreak, which will result in an enter after the type." +
                    " Since this will probably invalidate this GuiElement (" + toString() + "), the result of the " +
                    "type won't be checked.");
        }

        find();

        WebElement webElement = guiElementData.webElement;
        webElement.clear();
        webElement.sendKeys(text);

        if (!skipValueCheck && !webElement.getAttribute("value").equals(text)) {
            LOGGER.warn("Writing text to input field didn't work. Trying again.");

            webElement.clear();
            webElement.sendKeys(text);

            if (!webElement.getAttribute("value").equals(text)) {
                LOGGER.error("Writing text to input field didn't work on second try!");
            }
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

    private void pClickAbsolute(GuiElementCore guiElementCore, WebDriver driver, WebElement webElement) {
        LOGGER.trace("Absolute navigation and click on: " + guiElementCore.toString());
        guiElementCore.mouseOverAbsolute2Axis();
        Actions action = new Actions(driver);
        action.moveByOffset(1, 1);

        // Start the StopWatch for measuring the loading time of a Page
        StopWatch.startPageLoad(driver);

        action.click().perform();
    }

    @Override
    public void clickJS() {
        find();
        JSUtils.executeScript(webDriver, "arguments[0].click();", guiElementData.webElement);
    }

    @Override
    public void clickAbsolute() {
        find();
        pClickAbsolute(this, webDriver, guiElementData.webElement);
    }

    @Override
    public void mouseOverAbsolute2Axis() {
        find();
        demoMouseOver();
        mouseOverAbsolute2Axis(webDriver, guiElementData.webElement);
    }

    /**
     * Two axis mouse move.
     *
     * @param driver     .
     * @param webElement .
     */
    private void mouseOverAbsolute2Axis(WebDriver driver, WebElement webElement) {
        checkAndWarnIfIE();
        Actions action = new Actions(driver);

        Point point = webElement.getLocation();

        // goto 0,0
        action.moveToElement(webElement, 1 + -point.getX(), 1 + -point.getY()).perform();

        // move y, then x
        action.moveByOffset(0, point.getY()).moveByOffset(point.getX(), 0).perform();

        // move to webElement
        action.moveToElement(webElement).perform();
    }

    private void checkAndWarnIfIE() {
        if (Browsers.ie.equalsIgnoreCase(guiElementData.browser)) {
            LOGGER.warn("*** IE driver with mouse events might not work ***");
        }
    }

    @Override
    public void submit() {
        find();
        guiElementData.webElement.submit();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        find();
        guiElementData.webElement.sendKeys(charSequences);
    }

    @Override
    public void clear() {
        find();
        guiElementData.webElement.clear();
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
    public GuiElement getSubElement(By byLocator, String description) {
        FrameLogic frameLogic = guiElementData.frameLogic;
        GuiElement[] frames = null;
        if (frameLogic != null) {
            frames = frameLogic.getFrames();
        }

        String locatorToString = byLocator.toString();
        if (locatorToString.toLowerCase().contains("xpath")) {
            int i = locatorToString.indexOf(":") + 1;
            String locator = locatorToString.substring(i).trim();
            // Check if locator does not start with dot, ignoring a leading parenthesis for choosing the n-th element
            if (locator.startsWith("/")) {
                LOGGER.warn("GetSubElement: Forced replacement of / to ./ at startTime of By.xpath locator, because / would not be relative: " + locator);
                byLocator = By.xpath(locator);
            } else if (!locator.startsWith(".") && !(locator.length() >= 2 && locator.startsWith("(") && locator.substring(1, 2).equals("."))) {
                LOGGER.warn("Apparently, getSubElement is called with an By.xpath locator that does not startTime with a dot. " +
                        "This will most likely lead to unexpected and potentially quiet errors. Locator is \"" +
                        locatorToString + "\".");
            }
        }

        GuiElement subElement = new GuiElement(webDriver, byLocator, frames);
        subElement.setName(description);
        subElement.setParent(this);
        return subElement;
    }

    @Override
    public boolean isDisplayed() {
        return pIsDisplayed();
    }

    private boolean pIsDisplayed() {
        guiElementData.executionLog.addMessage("Checking for isDisplayed. Expecting 3 things: Element is Present, " +
                "webElement.isDisplayed() is true and the element is inside the viewport.");
        if (!isPresent()) {
            guiElementData.executionLog.addMessage("WebElement is not present");
            return false;
        }

        // in isPresent(), find() was executed which should set "webElement" or throw an exception
        WebElement webElement = guiElementData.webElement;
        if (webElement == null) {
            throw new TesterraSystemException("Internal error. This state should not be reached.");
        }

        if (webElement.isDisplayed()) {
            guiElementData.executionLog.addMessage("isDisplayedFromWebElement = true");
            return true;
            /*
            Locatable item = (Locatable) webElement;

            int x;
            int y;
            try {
                y = item.getCoordinates().inViewPort().getY();
                x = item.getCoordinates().inViewPort().getX();
            } catch (WebDriverException e) {
                LOGGER.debug("Error getting location of webElement.", e);
                return false;
            }

            // Philosophical Question: Is a WebElement displayed, if only one Pixel of it is displayed? Currently, yes.
            Dimension size = webElement.getSize();
            Dimension screenSize = webDriver.manage().window().getSize();
            boolean inViewportHorizontally = x + size.width >= 0 && x <= screenSize.getWidth();
            boolean inViewportVertically = y + size.height >= 0 && y <= screenSize.getHeight();
            if (inViewportHorizontally && inViewportVertically) {
                guiElementData.executionLog.addMessage("isInViewport(" + x + ", " + y + ") = true");
                return true;
            } else {
                guiElementData.executionLog.addMessage("isInViewport" + x + ", " + y + " = false");
            }
            */
        } else {
            guiElementData.executionLog.addMessage("isDisplayedFromWebElement = false");
            return false;
        }
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        if (!isPresent()) {
            LOGGER.debug("isDisplayedFromWebElement(): WebElement is not present");
            return false;
        }
        boolean displayed = guiElementData.webElement.isDisplayed();
        guiElementData.executionLog.addMessage("isDisplayedFromWebElement = " + displayed);
        return displayed;
    }

    @Override
    public boolean isVisible(final boolean complete) {
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
        String browser = guiElementData.browser;
        switch (browser) {
            case Browsers.safari:
            case Browsers.edge:

                LOGGER.warn("Auto-changing mouseOver to mouseOverJS for " + browser);
                pMouseOverJS();
                break;

            default:
                pMouseOver();
        }
    }

    /**
     * Mouse over highlight in demo mode.
     */
    private void demoMouseOver() {
        if (POConfig.isDemoMode()) {
            JSUtils.highlightWebElement(webDriver, guiElementData.webElement, 0, 255, 255); // yellow
        }
    }

    /**
     * Hidden method.
     */
    private void pMouseOver() {
        find();
        demoMouseOver();

        WebElement webElement = guiElementData.webElement;
        final Point location = webElement.getLocation();
        final int x = location.getX();
        final int y = location.getY();
        webElement.getSize();
        LOGGER.debug("MouseOver: " + toString() + " at x: " + x + " y: " + y);

        Actions action = new Actions(webDriver);
        action.moveToElement(webElement).build().perform();
    }

    @Override
    public void mouseOverJS() {
        demoMouseOver();
        pMouseOverJS();
    }

    private void pMouseOverJS() {
        find();
        final String code = "var fireOnThis = arguments[0];"
                + "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initEvent( 'mouseover', true, true );"
                + "fireOnThis.dispatchEvent(evObj);";

        ((JavascriptExecutor) webDriver).executeScript(code, guiElementData.webElement);
    }

    @Override
    public boolean isPresent() {
        try {
            LOGGER.debug("isPresent(): trying to find WebElement");
            find();
            guiElementData.executionLog.addMessage("isPresent = true");
        } catch (Exception e) {
            LOGGER.debug("isPresent(): Element not found: " + by, e);
            guiElementData.executionLog.addMessage("isPresent = false");
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
        }
        else if (Browsers.safari.equalsIgnoreCase(driverRequest.browser)) {
            LOGGER.info("Safari double click workaround");
            JSUtils.executeJavaScriptMouseAction(webDriver, webElement, JSMouseAction.DOUBLE_CLICK, 0, 0);
        }
        else {
            Actions actions = new Actions(webDriver);
            final Action action = actions.doubleClick(webElement).build();

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
    public void highlight() {
        LOGGER.debug("highlight(): starting highlight");
        JSUtils.highlightWebElement(webDriver, getWebElement(), 0, 0, 255);
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
        return find();
    }

    /**
     * Tries to parse the By-Locator to get the user defined selector string.
     *
     * @return Selector string if parsing is successful, complete By.toString() if not.
     */
    private String getSelectorString() {
        try {
            return by.toString().substring(by.toString().indexOf(" ") + 1);
        } catch (final Exception e) {
            LOGGER.warn("Error in getSelectorString.", e);
            return by.toString();
        }
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
    public void rightClickJS() {
        find();
        String script = "var element = arguments[0];" +
                "var e = element.ownerDocument.createEvent('MouseEvents');" +
                "e.initMouseEvent('contextmenu', true, true,element.ownerDocument.defaultView, 1, 0, 0, 0, 0, false,false, false, false,2, null);" +
                "return !element.dispatchEvent(e);";

        JSUtils.executeScript(webDriver, script, guiElementData.webElement);
    }

    @Override
    public void doubleClickJS() {
        find();
        WebElement webElement = getWebElement();
        Point location = webElement.getLocation();
        JSUtils.executeJavaScriptMouseAction(webDriver, webElement, JSMouseAction.DOUBLE_CLICK, location.getX(), location.getY());
    }

    @Override
    public File takeScreenshot() {
        final WebElement element = getWebElement();
        final boolean isSelenium4 = false;

        if (isSelenium4) {
            return element.getScreenshotAs(OutputType.FILE);
        } else {
            if (!isVisible(false)) {
                this.scrollToElement();
            }
            Rectangle viewport = WebDriverUtils.getViewport(webDriver);
            try {
                final TakesScreenshot driver = ((TakesScreenshot)guiElementData.webDriver);

                File screenshot = driver.getScreenshotAs(OutputType.FILE);
                BufferedImage fullImg = ImageIO.read(screenshot);

                Point point = element.getLocation();
                int eleWidth = element.getSize().getWidth();
                int eleHeight = element.getSize().getHeight();

                BufferedImage eleScreenshot = fullImg.getSubimage(
                    point.getX()-viewport.getX(),
                    point.getY()-viewport.getY(),
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
}
