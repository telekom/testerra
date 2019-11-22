package eu.tsystems.mms.tic.testframework.pageobjects;

import java.util.ArrayList;

/**
 * Prototype of XPath Builder
 * @author Mike Reiche
 */
public class XPath {
    private final String selector;
    private final ArrayList<String> attributes = new ArrayList<>();
    private final ArrayList<XPath> contains = new ArrayList<>();
    private XPath sub;
    private final int pos;
    protected XPath root;

    protected XPath(String selector, int position) {
        this.selector = selector;
        if (position == 0) position = 1;
        pos = position;
    }
    protected XPath(String selector) {
        this.selector = selector;
        pos = 0;
    }
    public static XPath node(String selector) {
        XPath xPath = new XPath(translateSubSelection(selector));
        xPath.root = xPath;
        return xPath;
    }
    public static XPath node(String selector, int position) {
        XPath xPath = new XPath(translateSubSelection(selector), position);
        xPath.root = xPath;
        return xPath;
    }

    public XPath hasClass(Object ... classes) {
        return attributeWords("class", classes);
    }

    private void somethingIs(String something, Object string) {
        attributes.add(String.format("%s='%s'", something, string));
    }
    private void somethingContains(String something, Object string) {
        attributes.add(String.format("contains(%s, '%s')", something, string));
    }
    private void somethingContainsWord(String something, Object string) {
        /**
         * @see {https://stackoverflow.com/questions/1390568/how-can-i-match-on-an-attribute-that-contains-a-certain-string}
         */
        attributes.add(String.format("contains(concat(' ', normalize-space(%s), ' '), ' %s ')", something, string));
        // Regex match with XPath 2.0
        //attributes.add(String.format("contains(@class, '[\\s|\\W]%s[\\s|\\W]')", className));
    }
    private void somethingContainsWords(String something, Object ... texts) {
        for (Object text : texts) {
            for (String word : text.toString().split("\b+")) {
                somethingContainsWord(something, word);
            }
        }
    }

    public XPath attribute(Attribute attribute, Object string) {
        return attribute(attribute.toString(), string);
    }

    public XPath attribute(String attribute, Object string) {
        somethingIs(String.format("@%s", attribute), string);
        return this;
    }

    public XPath attributeContains(Attribute attribute, Object string) {
        return attributeContains(attribute.toString(), string);
    }

    public XPath attributeContains(String attribute, Object string) {
        somethingContains(String.format("@%s", attribute), string);
        return this;
    }

    public XPath attributeWords(Attribute attribute, Object string) {
        return attributeWords(attribute.toString(), string);
    }

    public XPath attributeWords(String attribute, Object ... words) {
        somethingContainsWords(String.format("@%s", attribute), words);
        return this;
    }

    public XPath textIs(Object string) {
        somethingIs("text()", string);
        return this;
    }

    public XPath textContains(Object string) {
        somethingContains("text()", string);
        return this;
    }

    public XPath textWords(Object ... words) {
        somethingContainsWords("text()", words);
        return this;
    }

    private static String translateSubSelection(String selector) {
        if (!selector.startsWith("/")) {
            selector = "//"+selector;
        }
        return selector;
    }

    private String translateInnerSelection(String selector) {
        if (selector.startsWith("//")) {
            return selector.replace("//","descendant::");
        } else if (selector.startsWith("/")) {
            return selector.replace("/","child::");
        } else {
            return "descendant::"+selector;
        }
    }

    public XPath contains(String selector, int position) {
        XPath element = new XPath(translateInnerSelection(selector), position);
        element.root = root;
        contains.add(element);
        return element;
    }

    public XPath contains(String selector) {
        XPath element = new XPath(translateInnerSelection(selector));
        element.root = root;
        contains.add(element);
        return element;
    }

    public XPath select(String selector) {
        sub = new XPath(translateSubSelection(selector));
        sub.root = root;
        return sub;
    }
    public XPath select(String selector, int position) {
        sub = new XPath(translateSubSelection(selector), position);
        sub.root = root;
        return sub;
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
        if (sub != null) {
            xPath.append(sub.build());
        }
        return xPath.toString();
    }

    @Override
    public String toString() {
        return root.build();
    }
}
