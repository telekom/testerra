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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Advanced selector for elements as replacement for By
 *
 * @author Mike Reiche
 */
public class Locate {

    private List<WebElementFilter> filters;
    private boolean unique = false;
    private By by;
    private String preparedFormat;

    private Locate() {

    }

    public static Locate by() {
        return new Locate();
    }
    public static Locate by(By by) {
        final Locate locate = new Locate();
        locate.by = by;
        return locate;
    }

    /**
     * @param id The value of the "id" attribute to search for.
     * @return A By which locates elements by the value of the "id" attribute.
     */
    public Locate id(String id) {
        this.by = new By.ById(id);
        return this;
    }

    /**
     * @param linkText The exact text to match against.
     * @return A By which locates A elements by the exact text it displays.
     */
    public Locate linkText(String linkText) {
        by = new By.ByLinkText(linkText);
        return this;
    }

    /**
     * @param partialLinkText The partial text to match against
     * @return a By which locates elements that contain the given link text.
     */
    public Locate partialLinkText(String partialLinkText) {
        by = new By.ByPartialLinkText(partialLinkText);
        return this;
    }

    /**
     * @param name The value of the "name" attribute to search for.
     * @return A By which locates elements by the value of the "name" attribute.
     */
    public Locate name(String name) {
        by = new By.ByName(name);
        return this;
    }

    /**
     * @param tagName The element's tag name.
     * @return A By which locates elements by their tag name.
     */
    public Locate tagName(String tagName) {
        by = new By.ByTagName(tagName);
        return this;
    }

    /**
     * @param xpathExpression The XPath to use.
     * @return A By which locates elements via XPath.
     */
    public Locate xpath(String xpathExpression) {
        by = new By.ByXPath(xpathExpression);
        return this;
    }

    /**
     * Find elements based on the value of the "class" attribute. If an element has multiple classes, then
     * this will match against each of them. For example, if the value is "one two onone", then the
     * class names "one" and "two" will match.
     *
     * @param className The value of the "class" attribute to search for.
     * @return A By which locates elements by the value of the "class" attribute.
     */
    public Locate className(String className) {
        by = new By.ByClassName(className);
        return this;
    }

    /**
     * Find elements via the driver's underlying W3C Selector engine. If the browser does not
     * implement the Selector API, a best effort is made to emulate the API. In this case, we strive
     * for at least CSS2 support, but offer no guarantees.
     *
     * @param cssSelector CSS expression.
     * @return A By which locates elements by CSS.
     */
    public Locate cssSelector(String cssSelector) {
        by = new By.ByCssSelector(cssSelector);
        return this;
    }

    public Locate qa(final String string) {
        by = new By.ByXPath(String.format("//*[@data-qa='%s']", string));
        return this;
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
        String toString = (unique ? "unique " : "") + by.toString();
        if (filters != null && !filters.isEmpty()) {
            toString += String.format("with filters %s", filters.stream().map(WebElementFilter::toString).collect(Collectors.joining(", ")));
        }
        return toString;
    }

    /**
     * Prepares a xpath selector
     * @param format
     * @return
     */
    public static Locate prepare(final String format) {
        final Locate locate = new Locate();
        locate.preparedFormat = format;
        return locate;
    }

    /**
     * Formats an xpath selector
     * @param args
     * @return
     */
    public Locate with(Object... args) {
        Locate locate = by();
        locate.unique = unique;
        locate.filters = filters;
        return locate.xpath(String.format(preparedFormat, args));
    }
}
