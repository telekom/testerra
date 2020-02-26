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
package eu.tsystems.mms.tic.testframework.utils.xmlutils;

import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pele on 06.03.2015.
 */
public class JDom extends AbstractXMLUtils {

    private static final String LOGTEXT = "Changed attribute value %s from element %s from %s to %s";

    private static final String LOGTEXT2 = "Changed attribute value %s from element %s from %s to %s, compare value %s";

    private static final String LOGTEXT3 = "Changed text from element %s to %s";

    /**
     * Reads a string from a file.
     *
     * @param source File source
     * @return String
     * @throws IOException
     */
    public String readStringFromFile(File source) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(source.getAbsolutePath());
        return IOUtils.toString(is, Charsets.UTF_8);
    }

    /**
     * Reads a string from a file.
     *
     * @param is Input stream
     * @return Input stream as string
     * @throws IOException
     */
    public String readStringFromInputStream(InputStream is) throws IOException {
        return IOUtils.toString(is, Charsets.UTF_8);
    }

    /**
     * Parses an String and creates an JDOM Document object.
     *
     * @param source Source
     * @return new Document
     * @throws JDOMException
     * @throws IOException
     */
    public Document createDocumentFromString(String source) throws JDOMException, IOException {
        Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader(source);
        doc = builder.build(reader);
        return doc;
    }

    /**
     * Replace the value of a given element with a new value.
     *
     * @param xmlFile the xml file
     * @return the document
     * @throws JDOMException the JDOM exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Document createDocumentFromFile(String xmlFile) throws JDOMException, IOException {
        Document doc = null;
        File file = new File(xmlFile);
        doc = new SAXBuilder().build(file);
        return doc;
    }

    /**
     * Converts an JDOM Document back to an formatted string.
     *
     * @param doc Document
     * @return Document as string
     */
    public String documentToString(Document doc) {
        Format format = Format.getPrettyFormat();
        XMLOutputter output = new XMLOutputter(format);
        return output.outputString(doc);
    }

    /**
     * Add a new node to an existing parent node and set a defined value and attributes, if given.
     *
     * @param doc Document
     * @param elementName Parent element
     * @param newElement Neuer Element-Wert
     * @param attributes - attributes
     * @return Neues Document
     */
    public Document addNode(Document doc, String elementName, String newElement, String newElementValue, String... attributes) {
        Element rootElement = doc.getRootElement();
        LOGGER.debug("Root: " + rootElement);
        Element element = findElement(rootElement, elementName);
        LOGGER.debug("Element: " + element);
        Element nElement = new Element(newElement, element.getNamespace());
        LOGGER.debug(String.format("Add node '%s' to parent node '%s'.", newElement, elementName));
        if (!StringUtils.isStringEmpty(newElementValue)) {
            nElement.setText(newElementValue);
            LOGGER.debug(String.format("Set new value '%s' to the new node '%s'.", newElementValue, newElement));

        }

        for (int i = 0; i < attributes.length; i = i + 2) {
            Attribute att1 = new Attribute(attributes[i], attributes[i + 1]);
            nElement.setAttribute(att1);
        }
        element.addContent(nElement);
        return doc;
    }

    /**
     * Replace the value of a given element with a new value. Change only the first found element.
     *
     * @param doc Document
     * @param elementName Element-Name
     * @param newValue Neuer Element-Wert
     * @return Neues Document
     */
    public Document changeNodeValue(Document doc, String elementName, String newValue) {
        Element rootElement = doc.getRootElement();
        Element element = findElement(rootElement, elementName);
        element.setText(newValue);
        LOGGER.debug(String.format(LOGTEXT3, element.getName(), newValue));

        return doc;
    }

    /**
     * Replace the value of a given element with a new value. Set only the new value if oldValue was found.
     *
     * @param doc Document
     * @param elementName Element-Name
     * @param oldValue old value of the element
     * @param newValue new new value
     * @return the document
     */
    public Document changeNodeValue(Document doc, String elementName, String oldValue, String newValue) {
        List<Element> elements = findAllElements(doc.getRootElement(), elementName);
        for (Element element : elements) {
            if (element.getText().equals(oldValue)) {
                element.setText(newValue);
                LOGGER.debug(String.format(LOGTEXT3, element.getName(), newValue));
            }
        }

        return doc;
    }

    /**
     * Replace the value of all elements with the given name..
     *
     * @param doc Document
     * @param elementName the element name
     * @param newValue the new value
     * @return the document
     */
    public Document changeAllNodeValues(Document doc, String elementName, String newValue) {
        List<Element> elements = findAllElements(doc.getRootElement(), elementName);
        for (Element element : elements) {
            element.setText(newValue);
        }

        return doc;
    }

    /**
     * Find a named node in document.
     *
     * @param doc the doc
     * @param nodeName the node name
     * @return true, if successful
     */
    public boolean isExistsNode(Document doc, String nodeName) {
        if (doc == null || nodeName == null) {
            return false;
        }
        Element root = doc.getRootElement(); //
        ElementFilter filter = new ElementFilter(nodeName);
        for (Element element : root.getDescendants(filter)) {
            if (nodeName.equals(element.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value of a node.
     *
     * @param doc the doc
     * @param nodeName the node name
     * @return the node value
     */
    public String getNodeValue(Document doc, String nodeName) {
        if (doc == null || nodeName == null) {
            return null;
        }
        Element rootElement = doc.getRootElement();
        Element element = findElement(rootElement, nodeName);
        if (element == null) {
            return null;
        }
        return element.getText();
    }

    /**
     * Add a new attribtue and attribtue value to a given node.
     *
     * @param doc Document
     * @param elementName Element-Name
     * @param newAttributeName newAttributeName
     * @param newAttributeValue newAttributeValue
     * @return Neues Document
     */
    public Document addAttribute(Document doc, String elementName, String newAttributeName, String newAttributeValue) {
        Element rootElement = doc.getRootElement();
        Element element = findElement(rootElement, elementName);
        element.setAttribute(newAttributeName, newAttributeValue);
        return doc;
    }

    /**
     * Check if the attribute in the element exists.
     *
     * @param doc Document
     * @param elementName Element-Name
     * @param attributeName Attribut-Name
     * @return <true> Attribute exists in the element, <false> Attribut doesn't exist
     */
    public boolean isExistsAttribute(Document doc, String elementName, String attributeName) {
        Element rootElement = doc.getRootElement();
        Element element = findElement(rootElement, elementName);

        return element.getAttribute(attributeName) != null;
    }

    /**
     * Replaces all values of a given attribute, all attributes matching the given name will be replaced.
     *
     * @param doc Document
     * @param attributeName Attribut-Name
     * @param newValue Neuer Attribut-Wert
     * @return neues Document
     */
    public Document changeAttributeValues(Document doc, String attributeName, String newValue) {
        Element rootElement = doc.getRootElement();
        Element rootEdited = changeAttributeValueRecursive(rootElement,
                attributeName, newValue, null);
        doc.removeContent(rootElement);
        doc.addContent(rootEdited);
        return doc;
    }

    /**
     * Replaces all values of a given attribute based on its value, all attributes matching the given name and the
     * original value will be replaced.
     *
     * @param doc Orginal document
     * @param attributeName Attribute name
     * @param newValue New value for attribute
     * @param originalValue original value of attribute
     * @return New Document
     */
    public Document changeAttributeValues(Document doc, String attributeName, String newValue,
            String originalValue) {
        Element rootElement = doc.getRootElement();
        Element rootEdited = changeAttributeValueRecursive(rootElement,
                attributeName, newValue, originalValue);
        doc.removeContent(rootElement);
        doc.addContent(rootEdited);
        return doc;
    }

    /**
     * Replaces a value of a given attribute of a given element.
     *
     * @param doc Document
     * @param elementName Element-Name
     * @param attributeName Attribut-Name
     * @param newValue Neuer Attribut-Wert
     * @return neues Document
     */
    public Document changeAttributeValue(Document doc, String elementName, String attributeName, String newValue) {
        Element rootElement = doc.getRootElement();
        Element element = findElement(rootElement, elementName);
        element.getAttribute(attributeName).setValue(newValue);
        return doc;
    }

    /**
     * Removes all occurrences of a certain attribute.
     *
     * @param doc Document
     * @param elementName Element name.
     * @param attributeName Attribute name
     * @return New Document
     */
    public Document removeAttribute(Document doc, String elementName, String attributeName) {
        removeAttributeRecursive(doc.getRootElement(), elementName, attributeName);
        return doc;
    }

    /**
     * Find all elements in a root object via filtering.
     *
     * @param root Root element
     * @param tag Element name
     * @return the list
     */
    private static List<Element> findAllElements(Element root, String tag) {
        if (root == null) {
            throw new IllegalArgumentException("Root element is null.");
        }

        ElementFilter filter = new ElementFilter(tag);
        List<Element> elements = new ArrayList<Element>();
        for (Element element : root.getDescendants(filter)) {
            elements.add(element);
        }
        return elements;
    }

    /**
     * Find recursive a given element name in a element object.
     *
     * @param root Root-Element
     * @param tag Element name
     * @return Found element
     * @throws IllegalArgumentException
     */
    private Element findElement(Element root, String tag) throws IllegalArgumentException {

        if (root == null) {
            throw new IllegalArgumentException("Element is null.");
        }

        List<Element> enumChilds = root.getChildren();
        for (Element childElement : enumChilds) {
            if (childElement.getName().equals(tag)) {
                return childElement;
            }
            Element foundElement = findElement(childElement, tag);
            if (foundElement != null) {
                return foundElement;
            }
        }
        return null;
    }

    /**
     * Internal replacement method, used by all public attribute replacements.
     *
     * @param element Element
     * @param attributeName Attribut-Name
     * @param newValue Neuer Attribut-Wer
     * @param originalValue Original-wert
     * @return Neues Element
     */
    private Element changeAttributeValueRecursive(Element element, String attributeName, String newValue,
            String originalValue) {
        if (element.hasAttributes()) {
            List<Attribute> attributes = element.getAttributes();
            for (Attribute att : attributes) {
                if (att.getName().compareTo(attributeName) == 0) {
                    if (originalValue != null) {
                        String oldval = att.getValue();
                        if (oldval.compareTo(originalValue) == 0) {
                            att.setValue(newValue);
                            LOGGER.debug(String.format(LOGTEXT2, attributeName, element.getName(), oldval, newValue,
                                    originalValue));
                        }
                    } else {
                        String oldval = att.getValue();
                        att.setValue(newValue);
                        LOGGER.debug(String.format(LOGTEXT, attributeName, element.getName(), oldval, newValue));
                    }
                }
            }
        }

        List<Element> children = element.getChildren();
        List<Element> newChildren = new ArrayList<Element>();
        int childrenCount = children.size();
        if (childrenCount > 0) {
            for (Element child : children) {
                newChildren.add(changeAttributeValueRecursive(child,
                        attributeName, newValue, originalValue));
            }
        }
        if (childrenCount > 0) {
            element.removeContent();
            element.addContent(newChildren);
        }
        return element;
    }

    /**
     * Recursively remove an attribute to an element with a given name.
     *
     * @param element Element object
     * @param elementName Element name
     * @param attributeName Attribute name
     */
    private void removeAttributeRecursive(Element element, String elementName, String attributeName) {

        // gefunden?
        if (element.getName().equals(elementName)) {
            element.removeAttribute(attributeName);
            return;
        }
        for (Element child : element.getChildren()) {
            removeAttributeRecursive(child, elementName, attributeName);
        }
    }
}
