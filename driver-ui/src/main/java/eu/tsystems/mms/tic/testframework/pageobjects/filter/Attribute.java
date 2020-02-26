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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.filter;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 28.07.2015.
 */
public class Attribute extends WebElementFilter {

    private String attributeName;
    private String expectedAttributeValue;
    private StringChecker stringChecker;

    Attribute() {
    }

    private Attribute(String attributeName, String expectedAttributeValue, StringChecker stringChecker) {
        if (attributeName == null) {
            throw new TesterraSystemException("Attribute name can not be null.");
        }
        this.attributeName = attributeName;
        this.expectedAttributeValue = expectedAttributeValue;
        this.stringChecker = stringChecker;
    }

    public WebElementFilter is(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.Is());
        return attribute;
    }

    public WebElementFilter isNot(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.IsNot());
        return attribute;
    }

    public WebElementFilter contains(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.Contains());
        return attribute;
    }

    public WebElementFilter containsNot(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.ContainsNot());
        return attribute;
    }

    public WebElementFilter exists(String attributeName) {
        Attribute attribute = new Attribute(attributeName, "", new StringChecker.Exists());
        return attribute;
    }

    public WebElementFilter existsNot(String attributeName) {
        Attribute attribute = new Attribute(attributeName, "", new StringChecker.ExistsNot());
        return attribute;
    }

    @Override
    public boolean isSatisfiedBy(WebElement webElement) {
        checkCorrectUsage(STD_ERROR_MSG, attributeName, expectedAttributeValue, stringChecker);
        String actualAttributeValue = webElement.getAttribute(attributeName);
        return stringChecker.check(expectedAttributeValue, actualAttributeValue);
    }

    @Override
    public String toString() {
        return "Attribute " + String.format(stringChecker.toString(), attributeName, expectedAttributeValue);
    }
}
