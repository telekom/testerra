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
package eu.tsystems.mms.tic.testframework.pageobjects.filter;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 28.07.2015.
 */
public class Css extends WebElementFilter {

    private String cssName;
    private String expectedCssValue;
    private StringChecker stringChecker;

    Css() {}

    private Css(String cssName, String expectedCssValue, StringChecker stringChecker) {
        if (cssName == null) {
            throw new FennecSystemException("Css name can not be null.");
        }
        this.cssName = cssName;
        this.expectedCssValue = expectedCssValue;
        this.stringChecker = stringChecker;
    }

    public WebElementFilter is(String cssName, String cssValue) {
        Css css = new Css(cssName, cssValue, new StringChecker.Is());
        return css;
    }

    public WebElementFilter isNot(String cssName, String cssValue) {
        Css css =    new Css(cssName, cssValue, new StringChecker.IsNot());
        return css;
    }

    public WebElementFilter contains(String cssName, String cssValue) {
        Css css = new Css(cssName, cssValue, new StringChecker.Contains());
        return css;
    }

    public WebElementFilter containsNot(String cssName, String cssValue) {
        Css css = new Css(cssName, cssValue, new StringChecker.ContainsNot());
        return css;
    }

    public WebElementFilter exists(String cssName) {
        Css css = new Css(cssName, "", new StringChecker.Exists());
        return css;
    }

    public WebElementFilter existsNot(String cssName) {
        Css css = new Css(cssName, "", new StringChecker.ExistsNot());
        return css;
    }

    @Override
    public boolean isSatisfiedBy(WebElement webElement) {
        checkCorrectUsage(STD_ERROR_MSG, cssName, expectedCssValue, stringChecker);
        String actualCssValue = webElement.getCssValue(cssName);
        return stringChecker.check(expectedCssValue, actualCssValue);
    }

    @Override
    public String toString() {
        return "Css " + String.format(stringChecker.toString(), cssName, expectedCssValue);
    }
}
