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
 package eu.tsystems.mms.tic.testframework.pageobjects.filter;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import java.util.function.Predicate;
import org.openqa.selenium.WebElement;

@Deprecated
public class Css implements Predicate<WebElement> {

    private String cssName;
    private String expectedCssValue;
    private StringChecker stringChecker;

    Css() {}

    private Css(String cssName, String expectedCssValue, StringChecker stringChecker) {
        if (cssName == null) {
            throw new TesterraSystemException("Css name can not be null.");
        }
        this.cssName = cssName;
        this.expectedCssValue = expectedCssValue;
        this.stringChecker = stringChecker;
    }

    public Predicate<WebElement> is(String cssName, String cssValue) {
        Css css = new Css(cssName, cssValue, new StringChecker.Is());
        return css;
    }

    public Predicate<WebElement> isNot(String cssName, String cssValue) {
        Css css =    new Css(cssName, cssValue, new StringChecker.IsNot());
        return css;
    }

    public Predicate<WebElement> contains(String cssName, String cssValue) {
        Css css = new Css(cssName, cssValue, new StringChecker.Contains());
        return css;
    }

    public Predicate<WebElement> containsNot(String cssName, String cssValue) {
        Css css = new Css(cssName, cssValue, new StringChecker.ContainsNot());
        return css;
    }

    public Predicate<WebElement> exists(String cssName) {
        Css css = new Css(cssName, "", new StringChecker.Exists());
        return css;
    }

    public Predicate<WebElement> existsNot(String cssName) {
        Css css = new Css(cssName, "", new StringChecker.ExistsNot());
        return css;
    }

    @Override
    public boolean test(WebElement webElement) {
        String actualCssValue = webElement.getCssValue(cssName);
        return stringChecker.check(expectedCssValue, actualCssValue);
    }

    @Override
    public String toString() {
        return "Css " + String.format(stringChecker.toString(), cssName, expectedCssValue);
    }
}
