/*
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

import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 28.07.2015.
 */
public class Tag extends WebElementFilter {

    private String expectedTag;
    private StringChecker stringChecker;

    Tag() {}

    private Tag(String expectedTag, StringChecker stringChecker) {
        if (expectedTag == null) {
            throw new IllegalArgumentException("Tag can not be required to be null.");
        }
        this.expectedTag = expectedTag;
        this.stringChecker = stringChecker;
    }

    public WebElementFilter is(String expectedTag) {
        Tag tag = new Tag(expectedTag, new StringChecker.Is());
        return tag;
    }

    public WebElementFilter isNot(String expectedTag) {
        Tag tag = new Tag(expectedTag, new StringChecker.IsNot());
        return tag;
    }

    public WebElementFilter contains(String expectedTag) {
        Tag tag = new Tag(expectedTag, new StringChecker.Contains());
        return tag;
    }

    public WebElementFilter containsNot(String expectedTag) {
        Tag tag = new Tag(expectedTag, new StringChecker.ContainsNot());
        return tag;
    }

    @Override
    public boolean isSatisfiedBy(WebElement webElement) {
        checkCorrectUsage(STD_ERROR_MSG, expectedTag, stringChecker);
        String tag = webElement.getTagName();
        return stringChecker.check(expectedTag, tag);
    }

    @Override
    public String toString() {
        return String.format(stringChecker.toString(), "Tag", expectedTag);
    }
}
