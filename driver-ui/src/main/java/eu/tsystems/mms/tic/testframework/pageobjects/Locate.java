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
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.location.ByCoordinates;
import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Advanced selector for elements as replacement for By
 *
 * @author Mike Reiche
 */
public class Locate {
    private Predicate<WebElement> filter;
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

    /**
     * tt. by coordinates using webdriver
     *
     * @param driver .
     * @param x      .
     * @param y      .
     * @return .
     */
    public Locate coordinates(final WebDriver driver, final int x, final int y) {
        by = new ByCoordinates(driver, x, y);
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
        return displayed(true);
    }
    public Locate displayed(boolean displayed) {
        return filter(webElement -> webElement.isDisplayed()==displayed);
    }

    public Locate filter(Predicate<WebElement> filter) {
        this.filter = filter;
        return this;
    }

    public Predicate<WebElement> getFilter() {
        return this.filter;
    }

    @Override
    public String toString() {
        String toString = by.toString() + (unique ? " (unique)" : "");
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
        locate.unique = this.unique;
        locate.filter = this.filter;
        return locate.xpath(String.format(preparedFormat, args));
    }
}
