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
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.NonUniqueElementException;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
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
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rnhb on 12.08.2015.
 */
public class DesktopGuiElementCore extends AbstractGuiElementCore implements
    UseJSAlternatives,
    Loggable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiElement.class);

    public DesktopGuiElementCore(GuiElementData guiElementData) {
        super(guiElementData);
    }

    @Override
    public List<WebElement> findWebElements() {
        guiElementData.setWebElement(null);

        List<WebElement> elements = null;
        Locate locate = guiElementData.getLocate();
        Exception cause = null;
        try {
            if (guiElementData.hasParent()) {
                elements = guiElementData.getParent().getGuiElement().getCore().findWebElement().findElements(locate.getBy());
            } else {
                elements = guiElementData.getWebDriver().findElements(locate.getBy());
            }
        } catch(Exception e) {
            cause = e;
        }

        if (elements != null) {
            if (locate.isUnique() && elements.size() > 1) {
                throw new NonUniqueElementException(String.format("Locator(%s) found more than one WebElement [%d]", locate, elements.size()));
            }
            elements = applyFilters(elements, locate.getFilters());
            setWebElement(elements);
        }

        if (guiElementData.getWebElement() == null) {
            String message = String.format("{%s} not found", guiElementData.toString(true));
            MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
            if (currentMethodContext != null) {
                currentMethodContext.errorContext().setThrowable(message, cause);
            }
            throw new ElementNotFoundException(message, cause);
        }
        return elements;
    }

    @Override
    public WebElement findWebElement() {
        findWebElements();
        return guiElementData.getWebElement();
    }

    private int find() {
        long start = System.currentTimeMillis();
        List<WebElement> elements = findWebElements();
        logTimings(start, Timings.getFindCounter());
        if (UiElement.Properties.DELAY_AFTER_FIND_MILLIS.asLong() > 0) {
            TimerUtils.sleep(UiElement.Properties.DELAY_AFTER_FIND_MILLIS.asLong().intValue());
        }
        return elements.size();
    }

    private int setWebElement(List<WebElement> elements) {
        int numberOfFoundElements = elements.size();
        /*if (numberOfFoundElements < guiElementData.getIndex() + 1) {
            throw new StaleElementReferenceException("Request index of GuiElement was " + guiElementData.getIndex() + ", but only " + numberOfFoundElements + " were found. There you go, GuiElementList-Fanatics!");
        }*/

        if (numberOfFoundElements > 0) {
            // webelement to set
            WebElement webElement = elements.get(Math.max(0, guiElementData.getIndex()));

            // check for shadowRoot
            if (guiElementData.shadowRoot) {
                Object o = JSUtils.executeScript(guiElementData.getWebDriver(), "return arguments[0].shadowRoot", webElement);
                if (o instanceof WebElement) {
                    webElement = (WebElement) o;
                }
            }

            // proxy the web element for logging
            WebElementProxy webElementProxy = new WebElementProxy(guiElementData.getWebDriver(), webElement);
            Class[] interfaces = ObjectUtils.getAllInterfacesOf(webElement);
            webElement = ObjectUtils.simpleProxy(WebElement.class, webElementProxy, interfaces);

            // set webelement
            guiElementData.setWebElement(webElement);
            GuiElementData.WEBELEMENT_MAP.put(webElement, guiElementData.getGuiElement());

            // find timings
            int findCounter = Timings.raiseFindCounter();
            if (numberOfFoundElements > 1 && guiElementData.getIndex() == -1) {
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
            List<WebElement> filteredElements = new LinkedList<>();
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
            return filteredElements;
        }
    }

    private void highlightWebElement(Color color) {
        JSUtils.highlightWebElement(guiElementData.getWebDriver(), guiElementData.getWebElement(), color);
    }

    private void logTimings(long start, int findCounter) {
        if (findCounter != -1) {
            GuiElementData parent = guiElementData.getParent();
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
    @Deprecated
    public WebElement getWebElement() {
        find();
        return guiElementData.getWebElement();
    }

    @Override
    public By getBy() {
        return guiElementData.getLocate().getBy();
    }

    @Override
    public GuiElementCore scrollToElement() {
        pScrollToElement(0);
        return this;
    }

    @Override
    public GuiElementCore scrollToElement(int yOffset) {
        pScrollToElement(yOffset);
        return this;
    }

    /**
     * Private scroll to element.
     */
    private void pScrollToElement(int yOffset) {
        final Point location = getWebElement().getLocation();
        final int x = location.getX();
        final int y = location.getY() - yOffset;
        LOGGER.trace("Scrolling into view: " + x + ", " + y);

        JSUtils.executeScript(guiElementData.getWebDriver(), "scroll(" + x + ", " + y + ");");
    }

    @Override
    public GuiElementCore select() {
        if (!isSelected()) {
            click();
        }
        return this;
    }

    @Override
    public GuiElementCore deselect() {
        if (isSelected()) {
            click();
        }
        return this;
    }

    @Override
    public GuiElementCore type(String text) {
        pType(text);
        return this;
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

        WebElement webElement = guiElementData.getWebElement();
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
    public GuiElementCore click() {
        find();
        LOGGER.debug("click(): found element, adding ClickPath");
        LOGGER.debug("click(): added ClickPath, clicking relative");
        pClickRelative(this, guiElementData.getWebDriver(), guiElementData.getWebElement());
        LOGGER.debug("click(): clicked relative");
        return this;
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

        checkAndWarnIfIE();

        // Start the StopWatch for measuring the loading time of a Page
        StopWatch.startPageLoad(driver);

        Point point = webElement.getLocation();

        Actions action = new Actions(driver);

        // goto 0,0
        action.moveToElement(webElement, 1 + -point.getX(), 1 + -point.getY());

        // move y, then x
        action.moveByOffset(0, point.getY()).moveByOffset(point.getX(), 0);

        // move to webElement
        action.moveToElement(webElement);
        action.moveByOffset(1, 1);
        action.click().perform();
    }

    @Override
    public GuiElementCore clickJS() {
        find();
        JSUtils.executeScript(guiElementData.getWebDriver(), "arguments[0].click();", guiElementData.getWebElement());
        return this;
    }

    @Override
    public GuiElementCore clickAbsolute() {
        find();
        pClickAbsolute(this, guiElementData.getWebDriver(), guiElementData.getWebElement());
        return this;
    }

    @Override
    public GuiElementCore mouseOverAbsolute2Axis() {
        find();
        demoMouseOver();
        mouseOverAbsolute2Axis(guiElementData.getWebDriver(), guiElementData.getWebElement());
        return this;
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
        if (Browsers.ie.equalsIgnoreCase(guiElementData.getBrowser())) {
            LOGGER.warn("*** IE driver with mouse events might not work ***");
        }
    }

    @Override
    public GuiElementCore submit() {
        getWebElement().submit();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(CharSequence... charSequences) {
        getWebElement().sendKeys(charSequences);
        return this;
    }

    @Override
    public GuiElementCore clear() {
        getWebElement().clear();
        return this;
    }

    @Override
    public String getTagName() {
        find();
        String tagName = guiElementData.getWebElement().getTagName();
        return tagName;
    }

    @Override
    public String getAttribute(String attributeName) {
        find();
        String attribute = guiElementData.getWebElement().getAttribute(attributeName);
        return attribute;
    }

    @Override
    public boolean isSelected() {
        find();
        boolean selected = guiElementData.getWebElement().isSelected();
        return selected;
    }

    @Override
    public boolean isEnabled() {
        find();
        boolean enabled = guiElementData.getWebElement().isEnabled();
        return enabled;
    }

    @Override
    public String getText() {
        find();
        String text = guiElementData.getWebElement().getText();
        return text;
    }

    @Override
    public boolean isDisplayed() {
        return pIsDisplayed();
    }

    private boolean pIsDisplayed() {
        if (!isPresent()) {
            return false;
        }
        return guiElementData.getWebElement().isDisplayed();
    }

    @Override
    public Rectangle getRect() {
        return findWebElement().getRect();
    }

    @Override
    public boolean isVisible(boolean complete) {
        WebElement webElement = findWebElement();
        if (!webElement.isDisplayed()) return false;
        Rectangle viewport = WebDriverUtils.getViewport(guiElementData.getWebDriver());
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
        Point location = guiElementData.getWebElement().getLocation();
        return location;
    }

    @Override
    public Dimension getSize() {
        return findWebElement().getSize();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return findWebElement().getCssValue(cssIdentifier);
    }

    @Override
    public GuiElementCore mouseOver() {
        String browser = guiElementData.getBrowser();
        switch (browser) {
            case Browsers.safari:
            case Browsers.edge:

                LOGGER.warn("Auto-changing mouseOver to mouseOverJS for " + browser);
                pMouseOverJS();
                break;

            default:
                pMouseOver();
        }
        return this;
    }

    /**
     * Mouse over highlight in demo mode.
     */
    private void demoMouseOver() {
        if (Testerra.Properties.DEMO_MODE.asBool()) {
            highlightWebElement(new Color(255, 255, 0));
        }
    }

    /**
     * Hidden method.
     */
    private void pMouseOver() {
        find();
        demoMouseOver();

        WebElement webElement = guiElementData.getWebElement();
        final Point location = webElement.getLocation();
        final int x = location.getX();
        final int y = location.getY();
        webElement.getSize();
        LOGGER.debug("MouseOver: " + toString() + " at x: " + x + " y: " + y);

        Actions action = new Actions(guiElementData.getWebDriver());
        action.moveToElement(webElement).build().perform();
    }

    @Override
    public GuiElementCore mouseOverJS() {
        demoMouseOver();
        pMouseOverJS();
        return this;
    }

    private void pMouseOverJS() {
        find();
        final String code = "var fireOnThis = arguments[0];"
                + "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initEvent( 'mouseover', true, true );"
                + "fireOnThis.dispatchEvent(evObj);";

        ((JavascriptExecutor) guiElementData.getWebDriver()).executeScript(code, guiElementData.getWebElement());
    }

    @Override
    public boolean isPresent() {
        try {
            LOGGER.debug("isPresent(): trying to find WebElement");
            find();
        } catch (Exception e) {
            LOGGER.error("isPresent(): Element not found: " + guiElementData.getLocate(), e);
            return false;
        }
        return true;
    }

    @Override
    public Select getSelectElement() {
        find();
        Select select = new Select(guiElementData.getWebElement());
        return select;
    }


    @Override
    public List<String> getTextsFromChildren() {
        return pGetTextsFromChildren();
    }

    private List<String> pGetTextsFromChildren() {
        find();
        // find() not necessary here, because findElements() is called, which calls find().
        List<WebElement> childElements = guiElementData.getWebElement().findElements(By.xpath(".//*"));

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
    public GuiElementCore doubleClick() {
        find();
        WebElement webElement = guiElementData.getWebElement();
        By localBy = getBy();

        WebDriverRequest driverRequest = WebDriverManager.getRelatedWebDriverRequest(guiElementData.getWebDriver());

        if (localBy instanceof ByImage) {
            ByImage byImage = (ByImage) localBy;
            int x = byImage.getCenterX();
            int y = byImage.getCenterY();
            LOGGER.info("Image Double Click on image webElement at " + x + "," + y);
            JSUtils.executeJavaScriptMouseAction(guiElementData.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, x, y);
        } else if (Browsers.safari.equalsIgnoreCase(driverRequest.browser)) {
            LOGGER.info("Safari double click workaround");
            JSUtils.executeJavaScriptMouseAction(guiElementData.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, 0, 0);
        } else {
            Actions actions = new Actions(guiElementData.getWebDriver());
            final Action action = actions.doubleClick(webElement).build();

            try {
                action.perform();
            } catch (InvalidElementStateException e) {
                LOGGER.error("Error performing double click", e);
                LOGGER.info("Retrying double click with click-click");
                actions.moveToElement(webElement).click().click().build().perform();
            }
        }
        return this;
    }

    @Override
    public GuiElementCore highlight() {
        LOGGER.debug("highlight(): starting highlight");
        find();
        highlightWebElement(new Color(0, 0, 255));
        LOGGER.debug("highlight(): finished highlight");
        return this;
    }

    @Override
    public GuiElementCore swipe(int offsetX, int offSetY) {
        MouseActions.swipeElement(guiElementData, offsetX, offSetY);
        return this;
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
    public GuiElementCore rightClick() {
        find();
        Actions actions = new Actions(guiElementData.getWebDriver());
        actions.moveToElement(guiElementData.getWebElement()).contextClick().build().perform();
        return this;
    }

    @Override
    public GuiElementCore rightClickJS() {
        find();
        String script = "var element = arguments[0];" +
                "var e = element.ownerDocument.createEvent('MouseEvents');" +
                "e.initMouseEvent('contextmenu', true, true,element.ownerDocument.defaultView, 1, 0, 0, 0, 0, false,false, false, false,2, null);" +
                "return !element.dispatchEvent(e);";

        JSUtils.executeScript(guiElementData.getWebDriver(), script, guiElementData.getWebElement());
        return this;
    }

    @Override
    public GuiElementCore doubleClickJS() {
        find();
        WebElement webElement = getWebElement();
        Point location = webElement.getLocation();
        JSUtils.executeJavaScriptMouseAction(guiElementData.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, location.getX(), location.getY());
        return this;
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
            Rectangle viewport = WebDriverUtils.getViewport(guiElementData.getWebDriver());
            try {
                final TakesScreenshot driver = ((TakesScreenshot) guiElementData.getWebDriver());

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
                LOGGER.error(String.format("%s unable to take screenshot: %s ", guiElementData, e));
            }
        }
        return null;
    }
}
