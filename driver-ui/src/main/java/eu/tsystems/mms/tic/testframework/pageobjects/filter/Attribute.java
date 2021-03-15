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

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import java.util.function.Predicate;
import org.openqa.selenium.WebElement;

@Deprecated
public class Attribute implements Predicate<WebElement> {

    private String attributeName;
    private String expectedAttributeValue;
    private StringChecker stringChecker;

    Attribute() {
    }

    private Attribute(String attributeName, String expectedAttributeValue, StringChecker stringChecker) {
        if (attributeName == null) {
            throw new SystemException("Attribute name can not be null.");
        }
        this.attributeName = attributeName;
        this.expectedAttributeValue = expectedAttributeValue;
        this.stringChecker = stringChecker;
    }

    public Predicate<WebElement> is(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.Is());
        return attribute;
    }

    public Predicate<WebElement> isNot(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.IsNot());
        return attribute;
    }

    public Predicate<WebElement> contains(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.Contains());
        return attribute;
    }

    public Predicate<WebElement> containsNot(String attributeName, String attributeValue) {
        Attribute attribute = new Attribute(attributeName, attributeValue, new StringChecker.ContainsNot());
        return attribute;
    }

    public Predicate<WebElement> exists(String attributeName) {
        Attribute attribute = new Attribute(attributeName, "", new StringChecker.Exists());
        return attribute;
    }

    public Predicate<WebElement> existsNot(String attributeName) {
        Attribute attribute = new Attribute(attributeName, "", new StringChecker.ExistsNot());
        return attribute;
    }

    @Override
    public boolean test(WebElement webElement) {
        String actualAttributeValue = webElement.getAttribute(attributeName);
        return stringChecker.check(expectedAttributeValue, actualAttributeValue);
    }

    @Override
    public String toString() {
        return "Attribute " + String.format(stringChecker.toString(), attributeName, expectedAttributeValue);
    }
}
