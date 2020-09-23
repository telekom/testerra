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

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.NonUniqueElementException;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFactoryProvider;
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
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
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

public class DesktopGuiElementCore extends AbstractGuiElementCore implements UiElementFactoryProvider, Loggable {

    public DesktopGuiElementCore(GuiElementData guiElementData) {
        super(guiElementData);
    }

    /**
     * Tries to find web elements by given selector and filters
     * Throws an {@link ElementNotFoundException} when no element has been found
     * Throws an {@link NonUniqueElementException} when more than one element has been found
     * @return A list of {@link WebElement}
     */
    private List<WebElement> findAndFilterWebElements() {
        List<WebElement> elements = null;
        Exception seleniumException=null;
        try {
            Locate locate = guiElementData.getLocate();
            GuiElementData parentGuiElementData = guiElementData.getParent();
            if (parentGuiElementData != null) {
                elements = parentGuiElementData.getGuiElement().getRawCore().findWebElement().findElements(locate.getBy());
            } else {
                elements = guiElementData.getWebDriver().findElements(locate.getBy());
            }

            Predicate<WebElement> filter = locate.getFilter();
            if (filter != null) {
                elements.removeIf(webElement -> !filter.test(webElement));
            }
            if (locate.isUnique() && elements.size() > 1) {
                throw new NonUniqueElementException(String.format("Locator(%s) found more than one %s [%d]", locate, WebElement.class.getSimpleName(), elements.size()));
            }
        } catch(Exception e) {
           seleniumException = e;
        }
        if (elements == null || elements.size() == 0) {
            ElementNotFoundException exception = new ElementNotFoundException(guiElementData.getGuiElement(), seleniumException);
            MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
            if (currentMethodContext != null) {
                currentMethodContext.errorContext().setThrowable(exception.getMessage(), seleniumException);
            }
            throw exception;
        }
        return elements;
    }

    @Override
    public WebElement findWebElement() {
        long start = System.currentTimeMillis();
        // find timings
        Timings.raiseFindCounter();
        WebElement webElement = selectWebElement(findAndFilterWebElements());
        logTimings(start, Timings.getFindCounter());
        if (UiElement.Properties.DELAY_AFTER_FIND_MILLIS.asLong() > 0) {
            TimerUtils.sleep(UiElement.Properties.DELAY_AFTER_FIND_MILLIS.asLong().intValue());
        }
        return webElement;
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        consumer.accept(findWebElement());
    }

    /**
     * Finds the {@link WebElement} from a list of web elements
     * according to it's selector index in {@link GuiElementData#getIndex()}
     * Also prepares shadow roots by {@link GuiElementData#shadowRoot}
     * and wraps its around an {@link WebElementProxy}
     */
    private WebElement selectWebElement(List<WebElement> elements) {
        int numberOfFoundElements = elements.size();
        WebElement webElement;

        if (numberOfFoundElements > 0) {
            // webelement to set
            webElement = elements.get(Math.max(0, guiElementData.getIndex()));

            // check for shadowRoot
            if (guiElementData.shadowRoot) {
                Object shadowedWebElement = JSUtils.executeScript(guiElementData.getWebDriver(), "return arguments[0].shadowRoot", webElement);
                if (shadowedWebElement instanceof WebElement) {
                    webElement = (WebElement) shadowedWebElement;
                }
            }

            // proxy the web element for logging
            WebElementProxy webElementProxy = new WebElementProxy(guiElementData.getWebDriver(), webElement);
            Class[] interfaces = ObjectUtils.getAllInterfacesOf(webElement);
            webElement = ObjectUtils.simpleProxy(WebElement.class, webElementProxy, interfaces);
        } else {
            webElement = null;
        }
        return webElement;
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
                log().warn("find()#" + findCounter + " of GuiElement " + toString() + " took longer than " + limit + " ms.");
            }
        }
    }

    @Override
    public By getBy() {
        return guiElementData.getLocate().getBy();
    }

    @Override
    @Deprecated
    public GuiElementCore scrollToElement() {
        pScrollToElement(0);
        return this;
    }

    @Override
    public GuiElementCore scrollToElement(int yOffset) {
        pScrollToElement(yOffset);
        return this;
    }

    @Override
    public GuiElementCore scrollIntoView(Point offset) {
        JSUtils utils = new JSUtils();
        utils.scrollToCenter(guiElementData.getWebDriver(), findWebElement(), offset);
        return this;
    }

    /**
     * Private scroll to element.
     */
    private void pScrollToElement(int yOffset) {
        final Point location = findWebElement().getLocation();
        final int x = location.getX();
        final int y = location.getY() - yOffset;
        log().trace("Scrolling into view: " + x + ", " + y);

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
            log().warn("Text to type is null. Typing nothing.");
            return;
        }
        if (text.isEmpty()) {
            log().warn("Text to type is empty!");
        }

        WebElement webElement = findWebElement();
        webElement.clear();
        webElement.sendKeys(text);

        String valueProperty = webElement.getAttribute("value");
        if (valueProperty != null) {
            if (!valueProperty.equals(text)) {
                log().warn("Writing text to input field didn't work. Trying again.");

                webElement.clear();
                webElement.sendKeys(text);

                if (!webElement.getAttribute("value").equals(text)) {
                    log().error("Writing text to input field didn't work on second try!");
                }
            }
        } else {
            log().warn("Cannot perform value check after type() because " + this.toString() + " doesn't have a value property. Consider using sendKeys() instead.");
        }
    }

    @Override
    public GuiElementCore click() {
        pClickRelative(this, guiElementData.getWebDriver(), findWebElement());
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
            log().info("Image Click on image webElement at " + x + "," + y);
            JSUtils.executeJavaScriptMouseAction(driver, webElement, JSMouseAction.CLICK, x, y);
        } else {
            log().trace("Standard click on: " + guiElementCore.toString());
            // Start the StopWatch for measuring the loading time of a Page
            StopWatch.startPageLoad(driver);
            webElement.click();
        }
    }

    @Override
    public GuiElementCore submit() {
        findWebElement().submit();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(CharSequence... charSequences) {
        findWebElement().sendKeys(charSequences);
        return this;
    }

    @Override
    public GuiElementCore clear() {
        findWebElement().clear();
        return this;
    }

    @Override
    public String getTagName() {
        return findWebElement().getTagName();
    }

    @Override
    public String getAttribute(String attributeName) {
        return findWebElement().getAttribute(attributeName);
    }

    @Override
    public boolean isSelected() {
        return findWebElement().isSelected();
    }

    @Override
    public boolean isEnabled() {
        return findWebElement().isEnabled();
    }

    @Override
    public String getText() {
        return findWebElement().getText();
    }

    @Override
    public boolean isDisplayed() {
        return findWebElement().isDisplayed();
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
        return findWebElement().getLocation();
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
        pMouseOver();
        return this;
    }

    /**
     * Hidden method.
     */
    private void pMouseOver() {
        highlight(new Color(255, 255, 0));
        WebElement webElement = findWebElement();
        final Point location = webElement.getLocation();
        final int x = location.getX();
        final int y = location.getY();
        webElement.getSize();
        log().debug("MouseOver: " + toString() + " at x: " + x + " y: " + y);

        Actions action = new Actions(guiElementData.getWebDriver());
        action.moveToElement(webElement).build().perform();
    }

    @Override
    public boolean isPresent() {
        try {
            findWebElement();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Select getSelectElement() {
        Select select = new Select(findWebElement());
        return select;
    }


    @Override
    public List<String> getTextsFromChildren() {
        return pGetTextsFromChildren();
    }

    private List<String> pGetTextsFromChildren() {
        // find() not necessary here, because findElements() is called, which calls find().
        List<WebElement> childElements = findWebElement().findElements(By.xpath(".//*"));

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
        WebElement webElement = findWebElement();
        By localBy = getBy();

        WebDriverRequest driverRequest = WebDriverManager.getRelatedWebDriverRequest(guiElementData.getWebDriver());

        if (localBy instanceof ByImage) {
            ByImage byImage = (ByImage) localBy;
            int x = byImage.getCenterX();
            int y = byImage.getCenterY();
            log().info("Image Double Click on image webElement at " + x + "," + y);
            JSUtils.executeJavaScriptMouseAction(guiElementData.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, x, y);
        } else if (Browsers.safari.equalsIgnoreCase(driverRequest.browser)) {
            log().info("Safari double click workaround");
            JSUtils.executeJavaScriptMouseAction(guiElementData.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, 0, 0);
        } else {
            final Actions actions = new Actions(guiElementData.getWebDriver());
            final Action action = actions.moveToElement(webElement).doubleClick(webElement).build();

            try {
                action.perform();
            } catch (InvalidElementStateException e) {
                log().error("Error performing double click", e);
                log().info("Retrying double click with click-click");
                actions.moveToElement(webElement).click().click().build().perform();
            }
        }
        return this;
    }

    @Override
    public GuiElementCore highlight(Color color) {
        JSUtils.highlightWebElement(guiElementData.getWebDriver(), findWebElement(), color);
        return this;
    }

    @Override
    public GuiElementCore swipe(int offsetX, int offSetY) {
        MouseActions.swipeElement(guiElementData.getGuiElement(), offsetX, offSetY);
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
        return findAndFilterWebElements().size();
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
        Actions actions = new Actions(guiElementData.getWebDriver());
        actions.moveToElement(findWebElement()).contextClick().build().perform();
        return this;
    }

    @Override
    public File takeScreenshot() {
        final WebElement element = findWebElement();
        final boolean isSelenium4 = false;

        if (isSelenium4) {
            return element.getScreenshotAs(OutputType.FILE);
        } else {
            if (!isVisible(false)) {
                scrollIntoView();
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
                log().error(String.format("%s unable to take screenshot: %s ", guiElementData, e));
            }
        }
        return null;
    }

    @Override
    public UiElement find(Locate locator) {
        return uiElementFactory.createFromParent(guiElementData.getGuiElement(), locator);
    }
}
