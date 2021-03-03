/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.utils.Condition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;

/**
 * Prototype of XPath Builder
 * @author Mike Reiche
 */
public class XPath {
    private final String selector;
    private final ArrayList<String> attributes = new ArrayList<>();
    private final ArrayList<XPath> contains = new ArrayList<>();
    private XPath subSelect;
    private final int pos;
    private XPath parentSelect;
    private XPath root;

    protected XPath(String selector, int position) {
        this.selector = selector;
        if (position == 0) position = 1;
        pos = position;
    }
    protected XPath(String selector) {
        this.selector = selector;
        pos = 0;
    }
    public static XPath from(String selector) {
        XPath from = new XPath(translateSubSelection(selector));
        prepareFromSelect(from);
        return from;
    }
    public static XPath from(String selector, int position) {
        XPath from = new XPath(translateSubSelection(selector), position);
        prepareFromSelect(from);
        return from;
    }

    private static void prepareFromSelect(XPath from) {
        from.root = from;
        from.parentSelect = from;
    }

    public static Condition createAttributeCondition() {
        return new Condition("and", "or");
    }

    /**
     * Utility function for generating words search
     */
    public static String somethingContainsWord(String something, Object string) {
        /**
         * @see {https://stackoverflow.com/questions/1390568/how-can-i-match-on-an-attribute-that-contains-a-certain-string}
         */
        // Regex match with XPath 2.0
        //attributes.add(String.format("contains(@class, '[\\s|\\W]%s[\\s|\\W]')", className));
        return String.format("contains(concat(' ', normalize-space(%s), ' '), ' %s ')", something, string);
    }

    public static String somethingIs(String something, Object string) {
        return String.format("%s='%s'", something, string);
    }

    public static String somethingIsNot(String something, Object string) {
        return String.format("%s!='%s'", something, string);
    }

    public static String somethingMatches(String operation, String something, Object string) {
        return String.format("%s(%s,'%s')", operation, something, string);
    }

    static String byToXPath(By by) {
        String[] split = by.toString().split("\\: ", 2);
        if (split[0].startsWith("By.xpath")) {
            return split[1];
        } else if (split[0].startsWith("By.name")) {
            return "//*[" + somethingIs("@name", split[1]) + "]";
        } else if (split[0].startsWith("By.className")) {
            return "//*[" + somethingContainsWord("@class", split[1]) + "]";
        } else if (split[0].startsWith("By.id")) {
            return "//*[" + somethingIs("@id", split[1]) + "]";
        } else if (split[0].startsWith("By.tagName")) {
            return "//" + split[1];
        }
        return "(not-supported)";
    }

    public class Test {
        static final String CONTAINS="contains";
        static final String START="starts-with";
        static final String END="ends-with";

        XPath xPath;
        String function;

        Test(XPath xPath, String function) {
            this.xPath = xPath;
            this.function = function;
        }
        public XPath is(Object value) {
            attributeIs(function, value);
            return xPath;
        }
        public XPath present() {
            attributes.add(String.format("%s", function));
            return xPath;
        }
        public XPath contains(Object value) {
            attributeMatches(CONTAINS, function, value);
            return xPath;
        }

        public XPath hasWords(String ... words) {
            return hasWords(Arrays.stream(words).collect(Collectors.toList()));
        }

        public XPath hasWords(List<String> words) {
            attributeContainsWords(function, words);
            return xPath;
        }

        public XPath startsWith(Object value) {
            attributeMatches(START, function, value);
            return xPath;
        }

        public XPath endsWith(Object value) {
            attributeMatches(END, function, value);
            return xPath;
        }

        private void attributeIs(String something, Object string) {
            attributes.add(somethingIs(something, string));
        }

        private void attributeMatches(String operation, String something, Object string) {
            attributes.add(somethingMatches(operation, something, string));
        }
        private void attributeContainsWord(String something, Object string) {
            attributes.add(somethingContainsWord(something, string));
        }
        private void attributeContainsWords(String something, List<String> words) {
            for (Object text : words) {
                for (String word : text.toString().split("\\s+")) {
                    attributeContainsWord(something, word);
                }
            }
        }
    }

    public XPath classes(String ... classes) {
        return attribute(Attribute.CLASS).hasWords(classes);
    }

    public XPath classes(List<String> classes) {
        return attribute(Attribute.CLASS).hasWords(classes);
    }

    public Test attribute(Attribute attribute) {
        return attribute(attribute.toString());
    }

    public XPath attribute(Attribute attribute, Object value) {
        return attribute(attribute.toString(), value);
    }

    public Test text() {
        return new Test(this, ".//text()");
    }

    public XPath text(Object value) {
        return text().is(value);
    }

    public Test attribute(String attribute) {
        return new Test(this, "@"+attribute);
    }

    public XPath attribute(String attribute, Object value) {
        if (value == null) {
            return attribute(attribute).present();
        } else {
            return attribute(attribute).is(value);
        }
    }

    protected static String translateSubSelection(String selector) {
        selector = selector.trim();
        if (selector.startsWith("./")) {
            selector = selector.replaceFirst("^\\./", "/");
        } else if (!selector.startsWith("/")) {
            selector = "//" + selector;
        }
        return selector;
    }

    protected static String translateInnerSelection(String selector) {
        selector = selector.trim();
        if (selector.startsWith("//")) {
            return selector.replaceFirst("^//","descendant::");
        } else if (selector.startsWith("/")) {
            return selector.replaceFirst("^/", "child::");
        } else if (selector.startsWith("./")) {
            return selector.replaceFirst("^\\./", "child::");
        } else {
            return "descendant::"+selector;
        }
    }

    public XPath encloses(String selector, int position) {
        XPath contains = new XPath(translateInnerSelection(selector), position);
        prepareContainsSelect(contains);
        return contains;
    }

    public XPath encloses(String selector) {
        XPath contains = new XPath(translateInnerSelection(selector));
        prepareContainsSelect(contains);
        return contains;
    }

    public XPath encloses(XPath selector) {
        XPath contains = new XPath(selector.toString());
        prepareContainsSelect(contains);
        return contains;
    }

    private void prepareContainsSelect(XPath contains) {
        contains.root = this.root;
        contains.parentSelect = this.parentSelect;
        this.contains.add(contains);
    }

    public XPath select(String selector) {
        XPath sub = new XPath(translateSubSelection(selector));
        prepareSubSelect(sub);
        return sub;
    }
    public XPath select(String selector, int position) {
        XPath sub = new XPath(translateSubSelection(selector), position);
        prepareSubSelect(sub);
        return sub;
    }

    public XPath select(XPath selector) {
        XPath sub = new XPath(translateSubSelection(selector.toString()));
        prepareSubSelect(sub);
        return sub;
    }

    private void prepareSubSelect(XPath sub) {
        sub.root = this.root;
        this.parentSelect.subSelect = sub;
        sub.parentSelect = sub;
    }

    protected String build() {
        StringBuilder xPath = new StringBuilder();
        xPath.append(selector);
        ArrayList<String> attributes = new ArrayList<>(this.attributes);
        contains.stream().forEach(xpath -> attributes.add(xpath.build()));
        if (attributes.size() > 0) {
            xPath.append(String.format("[%s]", String.join(" and ", attributes)));
        }
        if (pos < 0) {
            xPath.append("[last()]");
        } else if (pos != 0) {
            xPath.append(String.format("[%d]", pos));
        }
        if (subSelect != null) {
            xPath.append(subSelect.build());
        }
        return xPath.toString();
    }

    @Override
    public String toString() {
        return root.build();
    }
}
