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

package eu.tsystems.mms.tic.testframework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.exceptions.NotYetImplementedException;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaScript Utils.
 *
 * @author pele
 * // TODO Move this class to driver-ui-desktop
 */
public final class JSUtils {

    private enum Snippet {
        HIGHLIGHT("snippets/highlight.js"),
        ;
        private final String resourcePath;
        Snippet(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public String getResourcePath() {
            return this.resourcePath;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JSUtils.class);

    /**
     * try to implement javascript on page
     *
     * @param driver       .
     * @param resourceFile .
     * @param id           .
     * @deprecated Use {@link #readSnippets(Snippet...)} instead
     */
    public static void implementJavascriptOnPage(final WebDriver driver, final String resourceFile, final String id) {

        // TODO rnhb&pele: find a solution that is not conflicting with GuiElement activity (maybe run this before
        // GuiElement) --> Switch to default content will end in "GuiElement not found", because the frame of GuiElement
        // isn't active anymore
        // driver.switchTo().defaultContent();
        if (isJavascriptImplementedOnPage(driver, id)) {
            LOGGER.debug("Already injected: " + resourceFile);
            return;
        }

        LOGGER.debug("JS inject: " + resourceFile);
        /*
         * Build inline string
         */
        String inline = "";

        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFile)) {

            if (inputStream == null) {
                throw new SystemException("Could not load resource file: " + resourceFile);
            }

            inline = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SystemException(e);
        }

        implementJavascriptOnPage(id, driver, inline);
    }

    /**
     * @deprecated Use {@link #readSnippets(Snippet...)} instead
     */
    public static void implementJavascriptOnPage(
            String scriptId,
            WebDriver driver,
            String script
    ) {
        try {
            if (script.length() > 0) {
                Gson gson = new GsonBuilder().create();
                JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
                String injectedScript = String.format("var script=document.createElement('script');" +
                                "var text=document.createTextNode(%s);" +
                                "script.type='text/javascript';" +
                                "script.id=%s;" +
                                "script.appendChild(text);" +
                                "document.body.appendChild(script);",
                        gson.toJson(script),
                        gson.toJson(scriptId)
                );
                javascriptExecutor.executeScript(injectedScript);
            }
        } catch (Exception e) {
            LOGGER.error("Error executing javascript: " + e.getMessage(), e);
        }
    }

    /**
     * executing async script
     *
     * @param driver     .
     * @param script     .
     * @param parameters .
     * @return .
     */
    public static Object executeAsyncScript(final WebDriver driver, final String script, final Object... parameters) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        return javascriptExecutor.executeAsyncScript(script, parameters);
    }

    /**
     * Try to execute javascript. If an error occurs it will be thrown.
     *
     * @param driver     .
     * @param script     .
     * @param parameters .
     * @return .
     */
    public static Object executeScriptWOCatch(final WebDriver driver, final String script, final Object... parameters) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        String msg = "Executing script >>" + script + "<<";
        if (parameters != null) {
            for (Object parameter : parameters) {
                msg += "\n\n with parameters: " + parameter;
            }
        }

        LOGGER.trace(msg);

        return javascriptExecutor.executeScript(script, parameters);
    }

    /**
     * Try to execute javascript. If an error occurs it will be logged only.
     *
     * @param driver     .
     * @param script     .
     * @param parameters .
     * @return .
     */
    public static Object executeScript(final WebDriver driver, final String script, final Object... parameters) {
        try {
            return executeScriptWOCatch(driver, script, parameters);
        } catch (Exception e) {
            LOGGER.error(String.format("Error executing script\n-----\n%s\n-----", script), e);
            return null;
        }
    }

    /**
     * Highlight a webelement on an actual driver session
     *
     * @param driver     .
     * @param webElement .
     * @param r          .
     * @param g          .
     * @param b          .
     */
    @Deprecated
    public static void highlightWebElement(
            final WebDriver driver,
            final WebElement webElement,
            final int r,
            final int g,
            final int b
    ) {
        highlightWebElement(driver, webElement, new Color(r, g, b));
    }

    /**
     * Highlights an element for a specified time.
     */
    public static void highlightWebElement(
            WebDriver driver,
            WebElement webElement,
            Color color
    ) {
        int ms = 2000;
        executeScript(
                driver,
                String.format(
                    "%s\n"+
                    "ttHighlight(arguments[0], '%s', %d)",
                    readSnippets(Snippet.HIGHLIGHT),
                    toHex(color),
                    ms
                ),
                webElement
        );
    }

    /**
     * Highlight a webelement on an actual driver session
     *
     * @param driver     .
     * @param webElement .
     * @param r          .
     * @param g          .
     * @param b          .
     */
    @Deprecated
    public static void highlightWebElementStatic(
            final WebDriver driver,
            final WebElement webElement,
            final int r,
            final int g,
            final int b
    ) {
        highlightWebElementStatic(driver, webElement, new Color(r, g, b));
    }


    private static String readSnippets(Snippet...snippets) {
        StringBuilder sb = new StringBuilder();
        for (Snippet snippet : snippets) {
            try {
                sb.append(new String(IOUtils.toByteArray(JSUtils.class.getClassLoader().getResourceAsStream(snippet.getResourcePath()))));
            } catch (IOException e) {
                LOGGER.error("Unable to read snippet", e);
            }
        }
        return sb.toString();
    }

    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Static element highlight doesn't fade out.
     */
    public static void highlightWebElementStatic(
            WebDriver driver,
            WebElement webElement,
            Color color
    ) {
        LOGGER.debug("Static highlighting WebElement " + webElement);
        executeScript(
                driver,
                String.format(
                        "%s\n"+
                        "ttAddStyle(arguments[0], 'outline: 5px dotted %s !important');",
                        readSnippets(Snippet.HIGHLIGHT),
                        toHex(color)
                ),
                webElement
        );
    }

    private static boolean isJavascriptImplementedOnPage(final WebDriver driver, final String id) {
        List<WebElement> script = driver.findElements(By.xpath(".//script[@id='" + id + "']"));
        return script.size() > 0;
    }

    /**
     * Execute a JS MouseAction on a driver session.
     *
     * @param driver              .
     * @param containerWebElement .
     * @param type                .
     * @param x                   .
     * @param y                   .
     */
    public static void executeJavaScriptMouseAction(
            final WebDriver driver,
            WebElement containerWebElement,
            JSMouseAction type,
            int x,
            int y
    ) {
        String aimedElement = "arguments[0]";
        if (containerWebElement == null) {
            aimedElement = "document";
        }
        ((JavascriptExecutor) driver).executeScript("var evt = document.createEvent('MouseEvents');"
                + " evt.initMouseEvent('" + type.getActionCommand() + "',true, true, window, 0, 0, 0,"
                + x + "," + y + ","
                + " false, false, false, false, 0,null);" +
                " " + aimedElement + ".dispatchEvent(evt);", containerWebElement);
    }

    /**
     * Scroll to top of page.
     *
     * @param driver WebDriver instance.
     */
    public static void scrollToTop(final WebDriver driver) {
        final JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("scroll(0,0);");
    }

    /**
     * Get a javascript based selector including frame hierarchy by extracting the By-Property of GuiElement
     * Uses Reflection
     *
     * @param element GuiElement
     * @return String
     */
    public static String getJavaScriptSelector(final GuiElement element) {

        final String jsFrameExpander = ".contentDocument";
        String hierarchyFrameSelector = "document";

        // Run through hierarchy
        final FrameLogic frameLogic = element.getFrameLogic();

        if (frameLogic != null) {
            List<GuiElement> allFramesInOrder = frameLogic.getAllFramesInOrder();
            for (GuiElement guiElement : allFramesInOrder) {
                hierarchyFrameSelector = pGetSimpleJsSelector(guiElement, hierarchyFrameSelector) + jsFrameExpander;
            }
        }

        // get js selector
        final String fullSelector = pGetSimpleJsSelector(element, hierarchyFrameSelector);

        return fullSelector;
    }

    /**
     * Gets the selector without frame hierarchy
     *
     * @param element          GuiElement
     * @param documentSelector String Current Selector
     * @return String
     */
    private static String pGetSimpleJsSelector(final GuiElement element, final String documentSelector) {

        final String jsById = documentSelector + ".getElementById(\"###\")";
        final String jsByClassName = documentSelector + ".getElementsByClassName(\"###\")[0]";
        final String jsByTagName = documentSelector + ".getElementsByTagName(\"###\")[0]";

        //
        final String jsByCssSelector = documentSelector + ".querySelector(\"###\")";
        final String jsByName = documentSelector + ".querySelector(*[name='###'])";

        final String jsByXpath = documentSelector + ".evaluate(\"###\", " + documentSelector
                + ", null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue";
        // TODO [erku] that is xpath ..maybe we can create some TesterraJS-methods
        final String jsByLinkText = documentSelector + ".evaluate(\".//a[text()='###']\", " + documentSelector
                + ", null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue";
        final String jsByPartialLinkText = documentSelector + ".evaluate(\".//a[contains(text(), '###')]\", "
                + documentSelector + ", null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue";

        // By-Class does not allow to access the selector field
        // ... use .toString() and extract selector.
        final By by = element.getBy();
        final String beautifiedSelector = beautifySelectorString(by);
        String jsSelector = "";

        if (by instanceof By.ById) {
            jsSelector = jsById;
        } else if (by instanceof By.ByCssSelector) {
            jsSelector = jsByCssSelector;
        } else if (by instanceof By.ByXPath) {
            jsSelector = jsByXpath;
        } else if (by instanceof By.ByClassName) {
            jsSelector = jsByClassName;
        } else if (by instanceof By.ByLinkText) {
            jsSelector = jsByLinkText;
        } else if (by instanceof By.ByPartialLinkText) {
            jsSelector = jsByPartialLinkText;
        } else if (by instanceof By.ByTagName) {
            jsSelector = jsByTagName;
        } else if (by instanceof By.ByName) {
            jsSelector = jsByName;
        }

        return jsSelector.replace("###", beautifiedSelector);
    }

    /**
     * Replaces unnecessary parts of String
     *
     * @param selector By Selector
     * @return String
     */
    private static String beautifySelectorString(By selector) {

        return selector.toString().replaceFirst("By.*:", "").trim();
    }

    public static Map<String, Long> getElementInnerBorders(GuiElement guiElement) {
        String cmd = "el = arguments[0];" +
                "bl = window.getComputedStyle(el, null).getPropertyValue('border-left-width');" +
                "br = window.getComputedStyle(el, null).getPropertyValue('border-right-width');" +
                "bt = window.getComputedStyle(el, null).getPropertyValue('border-top-width');" +
                "bb = window.getComputedStyle(el, null).getPropertyValue('border-bottom-width');" +
                "" +
                "pl = window.getComputedStyle(el, null).getPropertyValue('padding-left');" +
                "pr = window.getComputedStyle(el, null).getPropertyValue('padding-right');" +
                "pt = window.getComputedStyle(el, null).getPropertyValue('padding-top');" +
                "pb = window.getComputedStyle(el, null).getPropertyValue('padding-bottom');" +
                "" +
                "bl = bl.replace('px','');" +
                "br = br.replace('px','');" +
                "bt = bt.replace('px','');" +
                "bb = bb.replace('px','');" +
                "" +
                "pl = pl.replace('px','');" +
                "pr = pr.replace('px','');" +
                "pt = pt.replace('px','');" +
                "pb = pb.replace('px','');" +
                "" +
                "l = el.getBoundingClientRect().x;" +
                "w = el.getBoundingClientRect().width;" +
                "t = el.getBoundingClientRect().y;" +
                "h = el.getBoundingClientRect().height;" +
                "" +
                "il = parseInt(l);" +
                "iw = parseInt(w);" +
                "it = parseInt(t);" +
                "ih = parseInt(h);" +
                "" +
                "ibl = parseInt(bl);" +
                "ibr = parseInt(br);" +
                "ibt = parseInt(bt);" +
                "ibb = parseInt(bb);" +
                "" +
                "ipl = parseInt(pl);" +
                "ipr = parseInt(pr);" +
                "ipt = parseInt(pt);" +
                "ipb = parseInt(pb);" +
                "" +
                "x = il + ipl + ibl;" +
                "xx = il + iw - ibr - ipr;" +
                "y = it + ipt + ibt;" +
                "yy = it + ih - ibb - ipb;" +
                "" +
                "return {left:x, right:xx, top:y, bottom:yy};";
        WebDriver driver = guiElement.getWebDriver();
        FrameLogic frameLogic = guiElement.getFrameLogic();
        WebElement webElement = guiElement.getWebElement();
        if (frameLogic != null) {
            frameLogic.switchToCorrectFrame();
        }
        try {
            Object o = executeScript(driver, cmd, webElement);
            if (o != null && o instanceof Map) {
                Map map = (Map) o;
                Map<String, Long> out = new ConcurrentHashMap<String, Long>(map.size());
                for (Object key : map.keySet()) {
                    Object value = map.get(key);
                    if (key instanceof String && value instanceof Long) {
                        out.put((String) key, (Long) value);
                    }
                }

                out.keySet().forEach(key -> LOGGER.info(key + "=" + out.get(key)));
                if (out.keySet().size() != 4) {
                    throw new RuntimeException("Could not get element border via JS call");
                }

                return out;
            }
        } catch (Exception e) {
            LOGGER.error("Could not determine element inner left position", e);
        } finally {
            if (frameLogic != null) {
                frameLogic.switchToDefaultFrame();
            }
        }
        throw new RuntimeException("Could not get element border via JS call");
    }

    /**
     * Will get the viewport by executing JavaScript to determine innerwith and offsets
     *
     * @param driver {@link WebDriver}
     * @return Rectangle
     */
    public Rectangle getViewport(WebDriver driver) {
        Object result = JSUtils.executeScript(driver, "return [window.pageXOffset.toString(), window.pageYOffset.toString(), window.innerWidth.toString(), window.innerHeight.toString()];");
        if (result != null) {
            final ArrayList<String> list = (ArrayList<String>)result;
            List<Double> numbers = list.stream().map(Double::valueOf).collect(Collectors.toList());
            return new Rectangle(numbers.get(0).intValue(), numbers.get(1).intValue(), numbers.get(3).intValue(), numbers.get(2).intValue());
        } else {
            return new Rectangle(-1,-1,-1,-1);
        }
    }

    private static int getJSValueAsInt(Object in) {
        if (in instanceof Double) {
            return (int) (double) (Double) in;
        }
        if (in instanceof Long) {
            return (int) (long) (Long) in;
        }

        LOGGER.error("Cannot cast JS value into int: " + in);
        if (in == null) {
            return 0;
        }
        return 0;
    }

    public static Point getElementLocationInViewPort(GuiElement guiElement) {
        int x = 0;
        int y = 0;

        /*
        calculate frames
         */
        if (guiElement.getFrameLogic() != null) {
            GuiElement[] frames = guiElement.getFrameLogic().getFrames();
            for (GuiElement frame : frames) {
                Point elementLocationInParent = getElementLocationInParent(frame, Where.TOP_LEFT);
                x += elementLocationInParent.x;
                y += elementLocationInParent.y;
            }
        }

        /*
        calculate element
         */
        Point point = getElementLocationInParent(guiElement, Where.CENTER);
        x += point.x;
        y += point.y;

        return new Point(x, y);
    }

    enum Where {
        CENTER,
        TOP_LEFT
    }

    public static Point getElementLocationInParent(GuiElement guiElement, Where where) {
        WebElement webElement = guiElement.getWebElement();
        FrameLogic frameLogic = guiElement.getFrameLogic();

        if (frameLogic != null) {
            frameLogic.switchToCorrectFrame();
        }

        Object o = executeScript(guiElement.getDriver(), "return arguments[0].getBoundingClientRect();", webElement);

        if (o == null) {
            throw new SystemException("Could not get information about web element, please see the logs");
        }

        Map m = (Map) o;

        int left = getJSValueAsInt(m.get("left"));
        int width = getJSValueAsInt(m.get("width"));
        int height = getJSValueAsInt(m.get("height"));
        int top = getJSValueAsInt(m.get("top"));

        if (frameLogic != null) {
            frameLogic.switchToDefaultFrame();
        }

        switch (where) {
            case CENTER:
                return new Point(left + width / 2, top + height / 2);
            case TOP_LEFT:
                return new Point(left, top);
            default:
                throw new NotYetImplementedException("" + where);
        }
    }

    public static Point getRelativePositionVector(GuiElement from, GuiElement to) {
        Point start = getElementLocationInViewPort(from);
        Point end = getElementLocationInViewPort(to);

        return new Point(end.x - start.x, end.y - start.y);
    }

    /**
     * Scrolls the element to the center of the viewport
     */
    public void scrollToCenter(WebDriver webDriver, WebElement webElement, Point offset) {
        JSUtils.executeScript(
                webDriver,
                String.format("const elementRect = arguments[0].getBoundingClientRect();\n" +
                        "const absoluteElementTop = elementRect.top + window.pageYOffset;\n" +
                        "const absoluteElementLeft = elementRect.left + window.pageXOffset;\n" +
                        "const middle = absoluteElementTop - (window.innerHeight / 2);\n" +
                        "const center = absoluteElementLeft - (window.innerWidth / 2);\n" +
                        "window.scrollTo(center+%d, middle+%d);", offset.x, offset.y),
                webElement
        );
    }

    public void mouseOver(final WebDriver webDriver, final WebElement webElement) {
        demoMouseOver(webDriver, webElement);
        pMouseOverJS(webDriver, webElement);
    }

    public void doubleClick(final WebDriver webDriver, final WebElement webElement) {
        Point location = webElement.getLocation();
        JSUtils.executeJavaScriptMouseAction(webDriver, webElement, JSMouseAction.DOUBLE_CLICK, location.getX(), location.getY());
    }

    public void rightClick(final WebDriver webDriver, final WebElement webElement) {
        String script = "var element = arguments[0];" +
                "var e = element.ownerDocument.createEvent('MouseEvents');" +
                "e.initMouseEvent('contextmenu', true, true,element.ownerDocument.defaultView, 1, 0, 0, 0, 0, false,false, false, false,2, null);" +
                "return !element.dispatchEvent(e);";

        JSUtils.executeScript(webDriver, script, webElement);
    }

    public void click(final WebDriver webDriver, final WebElement webElement) {
        executeScript(webDriver, "arguments[0].click();", webElement);
    }

    private void demoMouseOver(final WebDriver webDriver, final WebElement webElement) {
        if (POConfig.isDemoMode()) {
            highlightWebElement(webDriver, webElement, new Color(255, 255, 0));
        }
    }

    public void clickAbsolute(final WebDriver webDriver, final WebElement webElement) {
        pClickAbsolute(webDriver, webElement);
    }

    public void mouseOverAbsolute2Axis(final WebDriver webDriver, final WebElement webElement) {
        demoMouseOver(webDriver, webElement);
        pMouseOverAbsolute2Axis(webDriver, webElement);
    }

    private void pMouseOverJS(final WebDriver webDriver, final WebElement webElement) {
        final String code = "var fireOnThis = arguments[0];"
                + "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initEvent( 'mouseover', true, true );"
                + "fireOnThis.dispatchEvent(evObj);";

        ((JavascriptExecutor) webDriver).executeScript(code, webElement);
    }

    private void pClickAbsolute(WebDriver driver, WebElement webElement) {
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

    private void pMouseOverAbsolute2Axis(WebDriver driver, WebElement webElement) {
        Actions action = new Actions(driver);

        Point point = webElement.getLocation();

        // goto 0,0
        action.moveToElement(webElement, 1 + -point.getX(), 1 + -point.getY()).perform();

        // move y, then x
        action.moveByOffset(0, point.getY()).moveByOffset(point.getX(), 0).perform();

        // move to webElement
        action.moveToElement(webElement).perform();
    }
}
