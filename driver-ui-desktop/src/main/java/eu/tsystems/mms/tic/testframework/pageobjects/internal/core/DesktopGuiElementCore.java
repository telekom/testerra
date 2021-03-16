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
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementException;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.internal.Timings;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.DefaultLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.WebElementRetainer;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByImage;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.MouseActions;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebElementProxy;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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

public class DesktopGuiElementCore extends AbstractGuiElementCore implements Loggable {

    private static final Assertion assertion = Testerra.getInjector().getInstance(InstantAssertion.class);

    public DesktopGuiElementCore(GuiElementData guiElementData) {
        super(guiElementData);
    }

    /**
     * Corrects a given {@link By} with xpath to use "./" instead of "/"
     * when passed to a sub element search.
     */
    private By correctToRelativeXPath(GuiElementData parentData, By by) {
        String abstractLocatorString = by.toString();
        if (abstractLocatorString.toLowerCase().contains("xpath")) {
            int i = abstractLocatorString.indexOf(":") + 1;
            String xpath = abstractLocatorString.substring(i).trim();
            boolean corrected = false;
            // Check if locator does not start with dot, ignoring a leading parenthesis for choosing the n-th element
            // /input --> ./input
            // //input --> .//input
            // input --> ./input
            // ./input --> No corrections
            // .//input --> No corrections
            // (.//input) --> No corrections
            if (xpath.startsWith("/")) {
                xpath = xpath.replaceFirst("/", "./");
                corrected = true;
            } else if (!xpath.startsWith("(")) {
                xpath = "./" + xpath;
                corrected = true;
            }

            // 14.01.2021 - To get in touch with shadow roots / dom in firefox
            // we have to point some facts out.
            // 1. We only support xpath, because it the only way we can simply handle locator modification
            if (parentData.isShadowRoot()) {
                // auto correct these sub element locators
                // ./input --> ./../input
                // .//input --> ./..//input
                xpath = "./." + xpath;
                corrected = true;
            }

            if (corrected) {
                by = By.xpath(xpath);
            }
        } else if (parentData.isShadowRoot()) {
            throw new UiElementException(this.guiElementData.getGuiElement(), "Finding sub elements on shadowRoot() only supports " + By.ByXPath.class.getSimpleName());
        }
        return by;
    }

    private void throwNotFoundException(Throwable reason) {
        throw new ElementNotFoundException(guiElementData.getGuiElement(), reason);
    }

    /**
     * Tries to find elements from a given {@link WebDriver}.
     * @throws ElementNotFoundException with the internal selenium exception cause.
     */
    private List<WebElement> findElementsFromWebDriver(WebDriver webDriver, By by) {
        try {
            return webDriver.findElements(by);
        } catch (Throwable throwable) {
            throwNotFoundException(throwable);
        }
        return null;
    }

    /**
     * Tries to find sub elements from a given {@link WebElement}.
     * @throws ElementNotFoundException with the internal selenium exception cause.
     */
    private List<WebElement> findElementsFromWebElement(WebElement webElement, By by) {
        try {
            return webElement.findElements(by);
        } catch (Throwable throwable) {
            throwNotFoundException(throwable);
        }
        return null;
    }

    /**
     * Calls the {@link WebElement#findElements(By)} and passes the results to the consumer.
     * Throws an {@link ElementNotFoundException} when no element has been found
     */
    private void findWebElements(Consumer<List<WebElement>> consumer) {
        WebDriver webDriver = guiElementData.getWebDriver();
        DefaultLocator locate = guiElementData.getLocate();
        By by = locate.getBy();

        GuiElementData parentData = guiElementData.getParent();
        if (parentData != null) {
            parentData.getGuiElement().getCore().findWebElement(parentWebElement -> {
                if (parentData.isFrame()) {
                    webDriver.switchTo().frame(parentWebElement);
                    consumer.accept(findElementsFromWebDriver(webDriver, by));
                } else {
                    consumer.accept(findElementsFromWebElement(parentWebElement, correctToRelativeXPath(parentData, by)));
                }
            });
        } else {
            webDriver.switchTo().defaultContent();
            consumer.accept(findElementsFromWebDriver(webDriver, by));
        }
    }

    /**
     * Filters a list of web elements by given {@link Locator}
     */
    private void filterWebElements(List<WebElement> webElements) {
        Predicate<WebElement> filter = guiElementData.getLocate().getFilter();
        if (filter != null) {
            webElements.removeIf(webElement -> !filter.test(webElement));
        }
    }

    /**
     * Finds the {@link WebElement} from a list of web elements
     * according to it's selector index in {@link GuiElementData#getIndex()}.
     * Also prepares shadow roots by {@link GuiElementData#isShadowRoot()}
     * and wraps its around an {@link WebElementProxy}.
     * More information see {@link WebElementRetainer#findWebElement(Consumer)}
     */
    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        // find timings
        long start = System.currentTimeMillis();
        Timings.raiseFindCounter();

        this.findWebElements(webElements -> {

            int numElementsBeforeFilter = webElements.size();

            this.filterWebElements(webElements);

            DefaultLocator locate = guiElementData.getLocate();
            if (locate.isUnique() && webElements.size() > 1) {
                throwNotFoundException(new AssertionError(assertion.formatExpectEquals(webElements.size(), 1, formatLocateSubject(locate, numElementsBeforeFilter))));
            }

            if (webElements.size() > 0) {
                /**
                 * Selected the right element from {@link GuiElementData#getIndex()}
                 */
                WebElement webElement = webElements.get(Math.max(0, guiElementData.getIndex()));
                WebDriver webDriver = guiElementData.getWebDriver();

                // check for shadowRoot
                if (guiElementData.isShadowRoot()) {
                    // 14.01.2021: Gheckodriver throw an internal exception when using the command above,
                    // therefore the result in the JS snippet "return arguments[0].shadowRoot" will be null.
                    // To handle firefox shadow roots we decided to handle it this way and implement an automatic resolver in getSubElement
                    try {
                        final Object shadowedWebElement = JSUtils.executeScriptWOCatch(webDriver, "return arguments[0].shadowRoot.firstChild", webElement);
                        if (shadowedWebElement instanceof WebElement) {
                            webElement = (WebElement) shadowedWebElement;
                        }
                    } catch (Exception e) {
                        log().error("Could not detect shadow root for " + guiElementData.toString() + ": " + e.getMessage());
                    }
                } else if (webElement.getTagName().equals("frame") || webElement.getTagName().equals("iframe")) {
                    guiElementData.setIsFrame(true);
                }

                // proxy the web element for logging
                WebElementProxy webElementProxy = new WebElementProxy(webDriver, webElement);
                Class[] interfaces = ObjectUtils.getAllInterfacesOf(webElement);
                webElement = ObjectUtils.simpleProxy(WebElement.class, webElementProxy, interfaces);

                // Finally pass the web element to the consumer
                consumer.accept(webElement);

                // We have to switch back to the default content at the end
                if (guiElementData.isFrame()) {
                    webDriver.switchTo().defaultContent();
                }

                logTimings(start, Timings.getFindCounter());
            } else {
                throwNotFoundException(new AssertionError(assertion.formatExpectGreaterEqualThan(assertion.toBigDecimal(0), assertion.toBigDecimal(1), formatLocateSubject(locate, numElementsBeforeFilter))));
            }
        });
//        if (UiElement.Properties.DELAY_AFTER_FIND_MILLIS.asLong() > 0) {
//            TimerUtils.sleep(UiElement.Properties.DELAY_AFTER_FIND_MILLIS.asLong().intValue());
//        }
    }

    private String formatLocateSubject(DefaultLocator locator, int numElementsBeforeFilter) {
        if (locator.getFilter() != null) {
            return "found elements";
        } else {
            return String.format("[%d] filtered elements", numElementsBeforeFilter);
        }
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
    public void scrollToElement(int yOffset) {
        pScrollToElement(yOffset);
    }

    @Override
    public void scrollIntoView(Point offset) {
        this.findWebElement(webElement -> {
            JSUtils utils = new JSUtils();
            utils.scrollToCenter(guiElementData.getWebDriver(), webElement, offset);
        });
    }

    /**
     * Private scroll to element.
     */
    private void pScrollToElement(int yOffset) {
        this.findWebElement(webElement -> {
            final Point location = webElement.getLocation();
            final int x = location.getX();
            final int y = location.getY() - yOffset;
            log().trace("Scrolling into view: " + x + ", " + y);

            JSUtils.executeScript(guiElementData.getWebDriver(), "scroll(" + x + ", " + y + ");");
        });
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
            log().warn("Text to type is null. Typing nothing.");
            return;
        }
        if (text.isEmpty()) {
            log().warn("Text to type is empty!");
        }

        findWebElement(webElement -> {
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
                log().warn("Cannot perform value check after type() because " + this.toString() +
                    " doesn't have a value property. Consider using sendKeys() instead.");
            }
        });
    }

    @Override
    public void click() {
        By by = this.guiElementData.getLocate().getBy();
        this.findWebElement(webElement -> {
            if (by instanceof ByImage) {
                ByImage byImage = (ByImage) by;
                int x = byImage.getCenterX();
                int y = byImage.getCenterY();
                log().info("Image Click on image webElement at " + x + "," + y);
                JSUtils.executeJavaScriptMouseAction(this.guiElementData.getWebDriver(), webElement, JSMouseAction.CLICK, x, y);
            } else {
                // Start the StopWatch for measuring the loading time of a Page
                //StopWatch.startPageLoad(this.guiElementData.getWebDriver());
                webElement.click();
            }
        });
    }

    @Override
    public void submit() {
        this.findWebElement(WebElement::submit);
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        this.findWebElement(webElement -> {
            webElement.sendKeys(charSequences);
        });
    }

    @Override
    public void clear() {
        this.findWebElement(WebElement::clear);
    }

    @Override
    public String getTagName() {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        findWebElement(webElement -> atomicReference.set(webElement.getTagName()));
        return atomicReference.get();
    }

    @Override
    public String getAttribute(String attributeName) {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        findWebElement(webElement -> atomicReference.set(webElement.getAttribute(attributeName)));
        return atomicReference.get();
    }

    @Override
    public boolean isSelected() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        findWebElement(webElement -> atomicBoolean.set(webElement.isSelected()));
        return atomicBoolean.get();
    }

    @Override
    public boolean isEnabled() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        findWebElement(webElement -> atomicBoolean.set(webElement.isEnabled()));
        return atomicBoolean.get();
    }

    @Override
    public String getText() {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        this.findWebElement(webElement -> atomicReference.set(webElement.getText()));
        return atomicReference.get();
    }

    @Override
    public boolean isDisplayed() {
        if (guiElementData.isShadowRoot()) return false;
        else {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            findWebElement(webElement -> atomicBoolean.set(webElement.isDisplayed()));
            return atomicBoolean.get();
        }
    }

    @Override
    public Rectangle getRect() {
        AtomicReference<Rectangle> atomicReference = new AtomicReference<>();
        this.findWebElement(webElement -> atomicReference.set(webElement.getRect()));
        return atomicReference.get();
    }

    @Override
    public boolean isVisible(boolean complete) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        this.findWebElement(webElement -> {
            if (!webElement.isDisplayed()) {
            return;
        }
            Rectangle viewport = WebDriverUtils.getViewport(guiElementData.getWebDriver());
            // getRect doesn't work
            Point elementLocation = webElement.getLocation();
            Dimension elementSize = webElement.getSize();
            java.awt.Rectangle viewportRect = new java.awt.Rectangle(viewport.x, viewport.y, viewport.width, viewport.height);
            java.awt.Rectangle elementRect = new java.awt.Rectangle(elementLocation.x, elementLocation.y, elementSize.width, elementSize.height);
            atomicBoolean.set(((complete && viewportRect.contains(elementRect)) || viewportRect.intersects(elementRect)));
        });
        return atomicBoolean.get();
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
        AtomicReference<Point> atomicReference = new AtomicReference<>();
        this.findWebElement(webElement -> atomicReference.set(webElement.getLocation()));
        return atomicReference.get();
    }

    @Override
    public Dimension getSize() {
        AtomicReference<Dimension> atomicReference = new AtomicReference<>();
        this.findWebElement(webElement -> atomicReference.set(webElement.getSize()));
        return atomicReference.get();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        AtomicReference<String> atomicReference = new AtomicReference<>();
        this.findWebElement(webElement -> atomicReference.set(webElement.getCssValue(cssIdentifier)));
        return atomicReference.get();
    }

    @Override
    public void hover() {
        pMouseOver();
    }

    /**
     * Hidden method.
     */
    private void pMouseOver() {
        this.findWebElement(webElement -> {
            JSUtils utils = new JSUtils();
            utils.highlight(guiElementData.getWebDriver(), webElement, new Color(255, 255, 0));
            final Point location = webElement.getLocation();
            final int x = location.getX();
            final int y = location.getY();
            webElement.getSize();
            log().debug("MouseOver: " + toString() + " at x: " + x + " y: " + y);

            Actions action = new Actions(guiElementData.getWebDriver());
            action.moveToElement(webElement).build().perform();
        });
    }

    @Override
    public boolean isPresent() {
        try {
            this.findWebElement(webElement -> {});
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> getTextsFromChildren() {
        return pGetTextsFromChildren();
    }

    private List<String> pGetTextsFromChildren() {
        AtomicReference<ArrayList<String>> atomicReference = new AtomicReference<>();
        // find() not necessary here, because findElements() is called, which calls find().
        this.findWebElement(webElement -> {
            List<WebElement> childElements = webElement.findElements(By.xpath(".//*"));
            ArrayList<String> childTexts = new ArrayList<>();
            for (WebElement childElement : childElements) {
                String text = childElement.getText();
                if (text != null && !text.equals("")) {
                    childTexts.add(text);
                }
            }
            atomicReference.set(childTexts);
        });
        return atomicReference.get();
    }

    @Override
    public void doubleClick() {
        By localBy = this.guiElementData.getLocate().getBy();

        this.findWebElement(webElement -> {
            if (localBy instanceof ByImage) {
                ByImage byImage = (ByImage) localBy;
                int x = byImage.getCenterX();
                int y = byImage.getCenterY();
                log().info("Image Double Click on image webElement at " + x + "," + y);
                JSUtils.executeJavaScriptMouseAction(guiElementData.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, x, y);
            } else if (Browsers.safari.equalsIgnoreCase(Testerra.getInjector().getInstance(IWebDriverManager.class).getRequestedBrowser(this.guiElementData.getWebDriver()).orElse(null))) {
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
        });
    }

    @Override
    public void highlight(Color color) {
        this.findWebElement(webElement -> {
            JSUtils utils = new JSUtils();
            utils.highlight(guiElementData.getWebDriver(), webElement, color);
        });
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        MouseActions.swipeElement(guiElementData.getGuiElement(), offsetX, offSetY);
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
        AtomicInteger atomicInteger = new AtomicInteger();
        this.findWebElements(webElements -> {
            filterWebElements(webElements);
            atomicInteger.set(webElements.size());
        });
        return atomicInteger.get();
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
    public void contextClick() {
        this.findWebElement(webElement -> {
            Actions actions = new Actions(guiElementData.getWebDriver());
            actions.moveToElement(webElement).contextClick().build().perform();
        });
    }

    @Override
    public File takeScreenshot() {
        final boolean isSelenium4 = false;
        if (isSelenium4) {
            return super.takeScreenshot();
        }

        AtomicReference<File> atomicReference = new AtomicReference<>();
        this.findWebElement(webElement -> {
            if (!isVisible(false)) {
                scrollIntoView();
            }
            Rectangle viewport = WebDriverUtils.getViewport(guiElementData.getWebDriver());
            try {
                final TakesScreenshot driver = ((TakesScreenshot) guiElementData.getWebDriver());

                File screenshot = driver.getScreenshotAs(OutputType.FILE);
                BufferedImage fullImg = ImageIO.read(screenshot);

                Point point = webElement.getLocation();
                int eleWidth = webElement.getSize().getWidth();
                int eleHeight = webElement.getSize().getHeight();

                BufferedImage eleScreenshot = fullImg.getSubimage(
                        point.getX() - viewport.getX(),
                        point.getY() - viewport.getY(),
                        eleWidth,
                        eleHeight
                );
                ImageIO.write(eleScreenshot, "png", screenshot);
                atomicReference.set(screenshot);
            } catch (IOException e) {
                log().error(String.format("%s unable to take screenshot: %s ", guiElementData, e));
            }
        });
        return atomicReference.get();
    }
}
