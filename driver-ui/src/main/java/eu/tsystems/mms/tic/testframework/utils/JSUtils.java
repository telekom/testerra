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
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaScript Utils.
 *
 * @author pele
 * // TODO Move this class to driver-ui-desktop
 */
public final class JSUtils implements Loggable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSUtils.class);

    public void addJavascriptResources(WebDriver webDriver, Stream<String> resourceFiles) {
        executeScript(webDriver, readScriptResources(resourceFiles));
    }

    /**
     * try to implement javascript on page
     *
     * @param driver .
     * @param resourceFile .
     * @param id .
     * @deprecated Use {@link #addJavascriptResources(WebDriver, Stream)} instead
     */
    @Deprecated
    public static void implementJavascriptOnPage(final WebDriver driver, final String resourceFile, final String id) {

        // TODO rnhb&pele: find a solution that is not conflicting with GuiElement activity (maybe run this before
        // GuiElement) --> Switch to default content will end in "GuiElement not found", because the frame of GuiElement
        // isn't active anymore
        // driver.switchTo().defaultContent();
        if (isJavascriptImplementedOnPage(driver, id)) {
            LOGGER.warn("Already injected: " + resourceFile);
            return;
        }
        String inline = readScriptResources(Stream.of(resourceFile));
        implementJavascriptOnPage(id, driver, inline);
    }

    /**
     * @deprecated Use {@link #addJavascriptResources(WebDriver, Stream)} instead
     */
    private static void implementJavascriptOnPage(
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
     * @param driver .
     * @param script .
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
     * @param driver .
     * @param script .
     * @param parameters .
     * @return .
     */
    public static Object executeScriptWOCatch(final WebDriver driver, final String script, final Object... parameters) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        return javascriptExecutor.executeScript(script, parameters);
    }

    /**
     * Try to execute javascript. If an error occurs it will be logged only.
     *
     * @param driver .
     * @param script .
     * @param parameters .
     * @return .
     */
    public static Object executeScript(final WebDriver driver, final String script, final Object... parameters) {
        try {
            return executeScriptWOCatch(driver, script, parameters);
        } catch (Exception e) {
            String message = String.format("Error executing Javascript\n-----\n%s\n-----", script);
            if (parameters.length > 0) {
                message += "\nwith parameters:\n" + Arrays.stream(parameters).map(o -> (o == null ? "null" : o.toString())).collect(Collectors.joining("\n"));
                message += "\n-----";
            }
            LOGGER.error(message, e);
            return null;
        }
    }

    public static String readScriptResources(Stream<String> resourceFiles) {
        StringBuilder sb = new StringBuilder();
        resourceFiles.forEach(resourceFile -> {
            try {
                sb.append(new String(IOUtils.toByteArray(Objects.requireNonNull(JSUtils.class.getClassLoader().getResourceAsStream(resourceFile)))));
            } catch (IOException e) {
                LOGGER.error("Unable to read resourceFile", e);
            }
        });
        return sb.toString();
    }

    private static boolean isJavascriptImplementedOnPage(final WebDriver driver, final String id) {
        List<WebElement> script = driver.findElements(By.xpath(".//script[@id='" + id + "']"));
        return script.size() > 0;
    }

    /**
     * Execute a JS MouseAction on a driver session.
     *
     * @param driver .
     * @param containerWebElement .
     * @param type .
     * @param x .
     * @param y .
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
     * @param webDriver WebDriver instance.
     */
    public void scrollToTop(WebDriver webDriver) {
        final JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        jsExecutor.executeScript("scroll(0,0);");
    }

    /**
     * Scrolls an element to the top of a page.
     *
     * @param webDriver
     * @param webElement
     */
    public void scrollElementToTop(WebDriver webDriver, WebElement webElement) {
        executeScript(
                webDriver,
                "window.scrollTo({ top: arguments[0].offsetTop, left: arguments[0].offsetLeft });",
                webElement
        );
    }

    /**
     * Get a javascript based selector including frame hierarchy by extracting the By-Property of GuiElement
     * Uses Reflection
     *
     * @param element GuiElement
     * @return String
     */
    @Deprecated
    public static String getJavaScriptSelector(GuiElement element) {

        final String jsFrameExpander = ".contentDocument";
        String hierarchyFrameSelector = "document";

        GuiElementData guiElementData = element.getData();

        // Run through frame hierarchy
        GuiElementData parentData = guiElementData.getParent();
        while (parentData != null && parentData.isFrame()) {
            hierarchyFrameSelector = pGetSimpleJsSelector(parentData, hierarchyFrameSelector) + jsFrameExpander;
            parentData = parentData.getParent();
        }

        // get js selector
        final String fullSelector = pGetSimpleJsSelector(guiElementData, hierarchyFrameSelector);

        return fullSelector;
    }

    /**
     * Gets the selector without frame hierarchy
     *
     * @param guiElementData GuiElement
     * @param documentSelector String Current Selector
     * @return String
     */
    private static String pGetSimpleJsSelector(GuiElementData guiElementData, final String documentSelector) {

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
        final By by = guiElementData.getLocate().getBy();
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

//    /**
//     * @deprecated Use by deprecated {@link Layout}
//     */
//    @Deprecated
//    public static Map<String, Long> getElementInnerBorders(UiElement guiElement) {
//        String cmd = "el = arguments[0];" +
//                "bl = window.getComputedStyle(el, null).getPropertyValue('border-left-width');" +
//                "br = window.getComputedStyle(el, null).getPropertyValue('border-right-width');" +
//                "bt = window.getComputedStyle(el, null).getPropertyValue('border-top-width');" +
//                "bb = window.getComputedStyle(el, null).getPropertyValue('border-bottom-width');" +
//                "" +
//                "pl = window.getComputedStyle(el, null).getPropertyValue('padding-left');" +
//                "pr = window.getComputedStyle(el, null).getPropertyValue('padding-right');" +
//                "pt = window.getComputedStyle(el, null).getPropertyValue('padding-top');" +
//                "pb = window.getComputedStyle(el, null).getPropertyValue('padding-bottom');" +
//                "" +
//                "bl = bl.replace('px','');" +
//                "br = br.replace('px','');" +
//                "bt = bt.replace('px','');" +
//                "bb = bb.replace('px','');" +
//                "" +
//                "pl = pl.replace('px','');" +
//                "pr = pr.replace('px','');" +
//                "pt = pt.replace('px','');" +
//                "pb = pb.replace('px','');" +
//                "" +
//                "l = el.getBoundingClientRect().x;" +
//                "w = el.getBoundingClientRect().width;" +
//                "t = el.getBoundingClientRect().y;" +
//                "h = el.getBoundingClientRect().height;" +
//                "" +
//                "il = parseInt(l);" +
//                "iw = parseInt(w);" +
//                "it = parseInt(t);" +
//                "ih = parseInt(h);" +
//                "" +
//                "ibl = parseInt(bl);" +
//                "ibr = parseInt(br);" +
//                "ibt = parseInt(bt);" +
//                "ibb = parseInt(bb);" +
//                "" +
//                "ipl = parseInt(pl);" +
//                "ipr = parseInt(pr);" +
//                "ipt = parseInt(pt);" +
//                "ipb = parseInt(pb);" +
//                "" +
//                "x = il + ipl + ibl;" +
//                "xx = il + iw - ibr - ipr;" +
//                "y = it + ipt + ibt;" +
//                "yy = it + ih - ibb - ipb;" +
//                "" +
//                "return {left:x, right:xx, top:y, bottom:yy};";
//
//        WebDriver driver = guiElement.getWebDriver();
//        Map<String, Long> out = new ConcurrentHashMap<>();
//        guiElement.findWebElement(webElement -> {
//            try {
//                Object o = executeScript(driver, cmd, webElement);
//                if (o != null && o instanceof Map) {
//                    Map map = (Map) o;
//                    for (Object key : map.keySet()) {
//                        Object value = map.get(key);
//                        if (key instanceof String && value instanceof Long) {
//                            out.put((String) key, (Long) value);
//                        }
//                    }
//
//                    out.keySet().forEach(key -> LOGGER.info(key + "=" + out.get(key)));
//                    if (out.keySet().size() != 4) {
//                        throw new RuntimeException("Could not get element border via JS call");
//                    }
//
//                }
//            } catch (Exception e) {
//                LOGGER.error("Could not determine element inner left position", e);
//            }
//        });
//
//        return out;
//    }

    /**
     * Will get the viewport by executing JavaScript to determine innerwith and offsets
     *
     * @param driver {@link WebDriver}
     * @return Rectangle
     */
    public Rectangle getViewport(WebDriver driver) {
        Object result = JSUtils.executeScript(driver, "return [window.pageXOffset.toString(), window.pageYOffset.toString(), window.innerWidth.toString(), window.innerHeight.toString()];");
        if (result != null) {
            final ArrayList<String> list = (ArrayList<String>) result;
            List<Double> numbers = list.stream().map(Double::valueOf).collect(Collectors.toList());
            return new Rectangle(numbers.get(0).intValue(), numbers.get(1).intValue(), numbers.get(3).intValue(), numbers.get(2).intValue());
        } else {
            return new Rectangle(-1, -1, -1, -1);
        }
    }

    /**
     * Get the element position via element.getBoundingClientRect().
     * Note, that values can be double like top: 10.876 -> rounded to 11 px
     */
    public Point getElementLocationInParent(UiElement uiElement) {
        AtomicReference<Point> atomicPoint = new AtomicReference<>();

        uiElement.findWebElement(webElement -> {
            Object o = JSUtils.executeScript(uiElement.getWebDriver(), "return arguments[0].getBoundingClientRect();", webElement);

            if (o instanceof Map) {
                Map<String, Object> boundingRect = (Map<String, Object>) o;
                int left = getJSValueAsInt(boundingRect.get("left"));
                int top = getJSValueAsInt(boundingRect.get("top"));
                atomicPoint.set(new Point(left, top));
            } else {
                atomicPoint.set(new Point(-1, -1));
            }

        });
        return atomicPoint.get();
    }

    private int getJSValueAsInt(Object in) {
        if (in instanceof Double) {
            // Always round up, because partly pixels became full pixel in a screenshot
            return Double.valueOf(Math.ceil((Double) in)).intValue();
        }
        if (in instanceof Long) {
            return Math.toIntExact((Long) in);
        }
        log().error("Cannot cast JS value into int: " + in);
        return 0;
    }

    /**
     * Scrolls the element to the center of the viewport
     *
     * @deprecated Use {@link #scrollToCenter(UiElement, Point)} instead
     */
    @Deprecated
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

    /**
     * Scrolls the element to the center of the viewport using 'element.scrollIntoView'.
     * Also working with elements in frames/iframes
     */
    public void scrollToCenter(UiElement uiElement, Point offset) {
        // Get location via JSUtils method to support frames/iframes
        Point elementLocationInParent = new JSUtils().getElementLocationInParent(uiElement);

        final String js = String.format(
                "const elementRect = arguments[0].getBoundingClientRect();" +
                        "const scrollX = %d + (elementRect.width /2) - (window.innerWidth / 2);" +
                        "const scrollY = %d + (elementRect.height / 2) - (window.innerHeight / 2);" +
                        "window.scrollBy(scrollX + %s, scrollY + %s)",
                elementLocationInParent.getX(),
                elementLocationInParent.getY(),
                offset.getX(),
                offset.getY());

        uiElement.findWebElement(webElement -> {
            JSUtils.executeScript(
                    uiElement.getWebDriver(),
                    js,
                    webElement
            );
        });

    }

    /**
     * Scrolls horizontally and vertically by the given values
     *
     * @param driver {@link WebDriver}
     * @param offset the values of x and y for scrolling horizontally and vertically
     */
    public void scrollByOffset(final WebDriver driver, Point offset) {
        JSUtils.executeScript(driver, "window.scrollBy(" + offset.getX() + ", " + offset.getY() + ");");
    }

    /**
     * Determines and returns the height, the viewport is currently at
     */
    public int getCurrentScrollHeight(final WebDriver driver) {
        return ((Long) JSUtils.executeScriptWOCatch(driver, "return (window.innerHeight + window.scrollY)")).intValue();
    }

    /**
     * Determines and returns the height of the current page
     */
    public int getDocumentHeight(final WebDriver driver) {
        return ((Long) JSUtils.executeScriptWOCatch(driver, "return document.body.scrollHeight")).intValue();
    }

}
