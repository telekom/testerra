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
/*
 * Created on 13.08.2013
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.exceptions.NotYetImplementedException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.Viewport;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JavaScript Utils.
 *
 * @author pele
 */
public final class JSUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSUtils.class);

    private JSUtils() {
    }

    /**
     * try to implement javascript on page
     *
     * @param driver .
     * @param resourceFile .
     * @param id .
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

        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFile);
        } catch (Exception e) {
            LOGGER.error("Error loading javascript file", e);
        }

        if (inputStream == null) {
            throw new TesterraSystemException("Could not load: " + resourceFile);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        /*
         * Build inline string
         */
        String inline = "";
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                inline += line;
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

        } catch (IOException e) {
            throw new TesterraSystemException(e);
        }

        implementJavascriptOnPage(id, driver, inline);
    }

    public static void implementJavascriptOnPage(
            String scriptId,
            WebDriver driver,
            String script
    ) {
        try {
            if (script.length() > 0) {
                JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
                String injectedScript = String.format("var script=document.createElement('script');" +
                                "var text=document.createTextNode(%s);" +
                                "script.type='text/javascript';" +
                                "script.id=%s;" +
                                "script.appendChild(text);" +
                                "document.body.appendChild(script);",
                        JSONObject.quote(script),
                        JSONObject.quote(scriptId)
                );
                javascriptExecutor.executeScript(injectedScript);
            }
        } catch (Exception e) {
            LOGGER.error("Error executing javascript", e);
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
        String msg = "Executing script >>" + script + "<<";
        if (parameters != null) {
            for (Object parameter : parameters) {
                msg += "\n\n with parameters: " + parameter;
            }
        }

        LOGGER.debug(msg);

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
            LOGGER.error("Error executing javascript", e);
            return null;
        }
    }

    /**
     * Highlight a webelement on an actual driver session
     *
     * @param driver .
     * @param webElement .
     * @param r .
     * @param g .
     * @param b .
     */
    public static void highlightWebElement(
        final WebDriver driver,
        final WebElement webElement,
        final int r,
        final int g,
        final int b
    ) {
        if (!POConfig.isDemoMode()) {
            return;
        }

        /*
         * Try to inject JS for demo mode before executing highlight
         */
        turnOnDemoModeForCurrentPage(driver);

        executeScript(driver, "highlightElement(arguments[0]," + r + "," + g + "," + b + ")",
                webElement);
    }

    /**
     * Highlight a webelement on an actual driver session
     *
     * @param driver .
     * @param webElement .
     * @param r .
     * @param g .
     * @param b .
     */
    public static void highlightWebElementStatic(
        final WebDriver driver,
        final WebElement webElement,
        final int r,
        final int g,
        final int b
    ) {
        if (!POConfig.isDemoMode()) {
            return;
        }

        /*
         * Try to inject JS for demo mode before executing highlight
         */
        turnOnDemoModeForCurrentPage(driver);

        LOGGER.debug("Static highlighting WebElement " + webElement);
        try {
            executeScriptWOCatch(driver, "highlightElementStatic(arguments[0]," + r + "," + g + "," + b + ")",
                    webElement);
        } catch (Throwable t) {
            LOGGER.warn("Could not highlight webElement", t);
        }
        LOGGER.debug("Finished static highlighting WebElement" + webElement);
    }

    /**
     * Turn the demo mode on by injecting js into the current page source.
     *
     * @param driver .
     */
    public static void turnOnDemoModeForCurrentPage(final WebDriver driver) {
        JSUtils.implementJavascriptOnPage(driver, "js/inject/highlightElement.js", "DemoModeForCurrentPage");
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
     * Gets the inner html string from an element with the specified id.
     *
     * @param driver WebDriver instance.
     * @param elementId Id of element to get innerhtml from.
     * @return innerhtml as String.
     */
    public static String getElementInnerHTMLById(final WebDriver driver, final String elementId) {
        final Object out = ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('" + elementId + "').innerHTML;");
        return (String) out;
    }

    /**
     * Gets the inner html string from an element with the specified name.
     *
     * @param driver WebDriver instance.
     * @param elementName Name of element to get innerhtml from.
     * @return innerhtml as String.
     */
    public static String getElementInnerHTMLByName(final WebDriver driver, final String elementName) {
        final Object out = ((JavascriptExecutor) driver).executeScript(
                "return document.getElementByName('" + elementName + "').innerHTML;");
        return (String) out;
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
            List<IGuiElement> allFramesInOrder = frameLogic.getAllFramesInOrder();
            for (IGuiElement guiElement : allFramesInOrder) {
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
     * @param element GuiElement
     * @param documentSelector String Current Selector
     * @return String
     */
    private static String pGetSimpleJsSelector(final IGuiElement element, final String documentSelector) {

        final String jsById = documentSelector + ".getElementById(\"###\")";
        final String jsByClassName = documentSelector + ".getElementsByClassName(\"###\")[0]";
        final String jsByTagName = documentSelector + "..getElementsByTagName(\"###\")[0]";

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

    public static Map<String, Long> getElementInnerBorders(IGuiElement guiElement) {
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
        IFrameLogic frameLogic = guiElement.getFrameLogic();
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
                    throw new TesterraRuntimeException("Could not get element border via JS call");
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
        throw new TesterraRuntimeException("Could not get element border via JS call");
    }

    public static Viewport getViewport(WebDriver driver) {
        if (driver == null) {
            return null;
        }

        try {
            Integer position = null;
            Integer width = null;
            Integer height = null;

            String script = "var doc = document.documentElement;" +
                    "var top = (window.pageYOffset || doc.scrollTop) - (doc.clientTop || 0);" +
                    "return top;";
            Object jsposition = executeScript(driver, script, "");
            if (jsposition != null) {
                position = Integer.valueOf("" + jsposition);
            }

            script = "var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);" +
                    "return w;";
            Object jswidth = executeScript(driver, script, "");
            if (jswidth!= null) {
                width = Integer.valueOf("" + jswidth);
            }

            script = "var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
                    "return h;";
            Object jsheight = executeScript(driver, script, "");
            if (jsheight != null) {
                height = Integer.valueOf("" + jsheight);
            }

            if (!ObjectUtils.isNull(position, width, height)) {
                LOGGER.debug("viewport position: " + position);
                LOGGER.debug("viewport width: " + width);
                LOGGER.debug("viewport height: " + height);
                return new Viewport(position, width, height);
            }
            return null;
        } catch (Throwable e) {
            LOGGER.warn("Error getting viewport", e);
            return null;
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
            throw new TesterraSystemException("Could not get information about web element, please see the logs");
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
}
