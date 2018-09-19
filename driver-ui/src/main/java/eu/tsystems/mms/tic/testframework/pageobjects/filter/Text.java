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

import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 23.07.2015.
 */
public class Text extends WebElementFilter {

    private String expectedText;
    private StringChecker stringChecker;

    Text() {
    }

    private Text(String expectedText, StringChecker stringChecker) {
        if (expectedText == null) {
            throw new IllegalArgumentException("Attribute 'text' can not be required to be null. " +
                    "If it should be empty, use an empty String!");
        }
        this.expectedText = expectedText;
        this.stringChecker = stringChecker;
    }

    public WebElementFilter is(String expectedText) {
        Text text = new Text(expectedText, new StringChecker.Is());
        return text;
    }

    public WebElementFilter isNot(String expectedText) {
        Text text = new Text(expectedText, new StringChecker.IsNot());
        return text;
    }

    public WebElementFilter contains(String expectedText) {
        Text text = new Text(expectedText, new StringChecker.Contains());
        return text;
    }

    public WebElementFilter containsNot(String expectedText) {
        Text text = new Text(expectedText, new StringChecker.ContainsNot());
        return text;
    }

    @Override
    public boolean isSatisfiedBy(WebElement webElement) {
        checkCorrectUsage(STD_ERROR_MSG, expectedText, stringChecker);
        String text = webElement.getText();
        return stringChecker.check(expectedText, text);
    }

    @Override
    public String toString() {
        return String.format(stringChecker.toString(), "Text", expectedText);
    }
}
