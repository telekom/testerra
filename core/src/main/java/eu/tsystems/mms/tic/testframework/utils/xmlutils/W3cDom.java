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
package eu.tsystems.mms.tic.testframework.utils.xmlutils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class W3cDom extends AbstractXMLUtils {

    /**
     * Creates a w3c document parsed from a String.
     *
     * @param url The xml or html code to parse.
     * @return A Jsoup Document.
     */
    public Document createW3cDocument(URL url) throws IOException, SAXException {
        Document document = getDocumentBuilder().parse(url.toString());
        return document;
    }

    /**
     * Creates a w3c document parsed from a String.
     *
     * @param content The xml or html code to parse.
     * @return A Jsoup Document.
     */
    public Document createW3cDocument(String content) throws IOException, SAXException {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        Document document = getDocumentBuilder().parse(is);
        return document;
    }

    /**
     * Creates a w3c document parsed from a File.
     *
     * @return A Jsoup Document.
     * @throws IOException
     */
    public Document createW3cDocument(File file) throws IOException, SAXException {
        Document document = getDocumentBuilder().parse(file);
        return document;
    }

    /**
     * Method to parse a w3c.dom.Document with xpath, returning the first result converted to String.
     *
     * @param document The document to parse.
     * @param xpath    The xpath expression to parse with.
     * @return The first element that fits the given xpath as String.
     * @throws XPathExpressionException
     * @throws IOException
     * @throws SAXException
     */
    public String evaluateXPath(Document document, String xpath) throws XPathExpressionException, IOException, SAXException {
        XPathExpression xPathExpression = getXpathFactory().newXPath().compile(xpath);
        return xPathExpression.evaluate(document);
    }

    /**
     * Method to parse a w3c.dom.Document with xpath, returning all fitting elements in a NodeList.
     *
     * @param document The document to parse.
     * @param xpath    The xpath expression to parse with.
     * @return A List of Nodes that fit the given xpath.
     * @throws XPathExpressionException
     * @throws IOException
     * @throws SAXException
     */
    public NodeList evaluateXPathNodes(Document document, String xpath) throws XPathExpressionException, IOException, SAXException {
        XPathExpression xPathExpression = getXpathFactory().newXPath().compile(xpath);
        return (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
    }

}
