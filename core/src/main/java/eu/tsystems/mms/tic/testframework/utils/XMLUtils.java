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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.utils.xmlutils.AbstractXMLUtils;
import eu.tsystems.mms.tic.testframework.utils.xmlutils.JDom;
import eu.tsystems.mms.tic.testframework.utils.xmlutils.JSoup;
import eu.tsystems.mms.tic.testframework.utils.xmlutils.W3cDom;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

/**
 * User: rnhb
 * Date: 11.10.13
 */
public class XMLUtils extends AbstractXMLUtils {

    private static final JDom JDOM = new JDom();
    private static final W3cDom W3CDOM = new W3cDom();
    private static final JSoup JSOUP = new JSoup();

    /**
     * Accessor.
     * @return .
     */
    public static JDom jdom() {
        return JDOM;
    }

    /**
     * Accessor.
     * @return .
     */
    public static W3cDom w3cdom() {
        return W3CDOM;
    }

    /**
     * Accessor.
     * @return .
     */
    public static JSoup jsoup() {
        return JSOUP;
    }

    /**
     * Method to parse an xml String with xpathExpression, returning the first result converted to String.
     *
     * @param xml The document to parse.
     * @param xpathExpression    The xpathExpression expression to parse with.
     * @return The first element that fits the given xpathExpression as String.
     * @throws XPathExpressionException
     */
    public static String evaluateXPath(String xml, String xpathExpression) throws XPathExpressionException {
        XPath xPath = getXpathFactory().newXPath();
        String out = xPath.evaluate(xpathExpression, new InputSource(new StringReader(xml)));
        return out;
    }

    /**
     * Method to parse an xml String with xpath, returning all fitting elements in a NodeList.
     *
     * @param xml The document to parse.
     * @param xpath    The xpath expression to parse with.
     * @return A List of Nodes that fit the given xpath.
     * @throws XPathExpressionException
     */
    public static NodeList evaluateXPathNodes(String xml, String xpath) throws XPathExpressionException {
        XPath xPath = getXpathFactory().newXPath();
        NodeList nodeList = (NodeList) xPath.evaluate(xpath, new InputSource(new StringReader(xml)), XPathConstants.NODESET);
        return nodeList;
    }

    /**
     * XML to Object Mapping.
     *
     * @param xml definition.
     * @param tClass the class.
     * @param <T> the type.
     * @return the object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T jaxbXMLToObject(String xml, Class<T> tClass) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        try {
            JAXBContext context = JAXBContext.newInstance(tClass);
            Unmarshaller un = context.createUnmarshaller();

            T tObject = (T) un.unmarshal(inputStream);
            return tObject;
        } catch (JAXBException e) {
            throw new TesterraRuntimeException("Cannot unmarshal xml to object.", e);
        }
    }

    /**
     * Object to XML Mapping.
     *
     * @param tClass the class.
     * @param tObject the object.
     * @param <T> the type.
     * @return the output xml.
     */
    public static <T> String jaxbObjectToXML(Class<T> tClass, T tObject) {
        try {
            JAXBContext context = JAXBContext.newInstance(tClass);
            Marshaller m = context.createMarshaller();
            //for pretty-print XML in JAXB
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Write to File
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            m.marshal(tObject, os);

            byte[] bytes = os.toByteArray();
            return new String(bytes);
        } catch (JAXBException e) {
            throw new TesterraRuntimeException("Cannot marshal object to xml.", e);
        }
    }

}
