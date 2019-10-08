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
 * Created on 10.02.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects.location;

import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Advanced selector for elements as replacement for By
 *
 * @author Mike Reiche <mike.reiche@t-systems.com>
 */
public class Locate {

    private List<WebElementFilter> filters;
    private boolean unique = false;
    private By by;

    public Locate(By by) {
        this.by = by;
    }

    /**
     * @param id The value of the "id" attribute to search for.
     * @return A By which locates elements by the value of the "id" attribute.
     */
    public static Locate id(String id) {
        return new Locate(new By.ById(id)).unique();
    }

    /**
     * @param linkText The exact text to match against.
     * @return A By which locates A elements by the exact text it displays.
     */
    public static Locate linkText(String linkText) {
        return new Locate(new By.ByLinkText(linkText));
    }

    /**
     * @param partialLinkText The partial text to match against
     * @return a By which locates elements that contain the given link text.
     */
    public static Locate partialLinkText(String partialLinkText) {
        return new Locate(new By.ByPartialLinkText(partialLinkText));
    }

    /**
     * @param name The value of the "name" attribute to search for.
     * @return A By which locates elements by the value of the "name" attribute.
     */
    public static Locate name(String name) {
        return new Locate(new By.ByName(name));
    }

    /**
     * @param tagName The element's tag name.
     * @return A By which locates elements by their tag name.
     */
    public static Locate tagName(String tagName) {
        return new Locate(new By.ByTagName(tagName));
    }

    /**
     * @param xpathExpression The XPath to use.
     * @return A By which locates elements via XPath.
     */
    public static Locate xpath(String xpathExpression) {
        return new Locate(new By.ByXPath(xpathExpression));
    }

    /**
     * Find elements based on the value of the "class" attribute. If an element has multiple classes, then
     * this will match against each of them. For example, if the value is "one two onone", then the
     * class names "one" and "two" will match.
     *
     * @param className The value of the "class" attribute to search for.
     * @return A By which locates elements by the value of the "class" attribute.
     */
    public static Locate className(String className) {
        return new Locate(new By.ByClassName(className));
    }

    /**
     * Find elements via the driver's underlying W3C Selector engine. If the browser does not
     * implement the Selector API, a best effort is made to emulate the API. In this case, we strive
     * for at least CSS2 support, but offer no guarantees.
     *
     * @param cssSelector CSS expression.
     * @return A By which locates elements by CSS.
     */
    public static Locate css(String cssSelector) {
        return new Locate(new By.ByCssSelector(cssSelector));
    }
    /**
     * tt. by coordinates using webdriver
     *
     * @param driver .
     * @param x      .
     * @param y      .
     * @return .
     */
    public static Locate coordinates(final WebDriver driver, final int x, final int y) {
        return new Locate(new ByCoordinates(driver, x, y));
    }

    public static Locate qa(final String string) {
        return new Locate(new By.ByXPath(String.format("//*[@data-qa='%s']", string))).unique();
    }

    public By getBy() {
        return this.by;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public Locate unique() {
        this.unique = true;
        return this;
    }

    public Locate displayed() {
        return filter(WebElementFilter.DISPLAYED.is(true));
    }
    public Locate notDisplayed() {
        return filter(WebElementFilter.DISPLAYED.is(false));
    }

    public Locate filter(WebElementFilter... filters) {
        if (filters != null) {
            Collections.addAll(getFilters(), filters);
        }
        return this;
    }

    public List<WebElementFilter> getFilters() {
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }
        return this.filters;
    }

    @Override
    public String toString() {
        String toString = by.toString() + (unique ? " (unique)" : "");
        if (filters != null && !filters.isEmpty()) {
            toString += " with WebElementFilter" + (filters.size() > 1 ? "s" : "") + " { ";
            for (WebElementFilter webElementFilter : filters) {
                toString += webElementFilter.toString() + ", ";
            }
            // cut the last comma with space
            toString = toString.substring(0, toString.length() - 2) + " }";
        }
        return toString;
    }
}
