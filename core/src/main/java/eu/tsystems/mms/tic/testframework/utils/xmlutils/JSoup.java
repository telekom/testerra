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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by pele on 06.03.2015.
 */
public class JSoup extends AbstractXMLUtils {

    /**
     * Creates a Jsoup document parsed from a String.
     *
     * @param content The xml or html code to parse.
     * @return A Jsoup Document.
     */
    public Document createJsoupDocument(String content) {
        Document document = Jsoup.parse(content);
        return document;
    }

    /**
     * Creates a Jsoup document parsed from a File.
     *
     * @return A Jsoup Document.
     * @throws IOException
     */
    public Document createJsoupDocument(File file) throws IOException {
        Document document = Jsoup.parse(file, charsetName);
        return document;
    }

    /**
     * Creates a Jsoup document from an url.
     *
     * @param url The url where the document should be parsed from.
     * @return A Jsoup Document.
     * @throws IOException
     */
    public Document createJsoupDocument(URL url) throws IOException {
        Document document = Jsoup.connect(url.toString()).get();
        return document;
    }

    /**
     * Method to parse a jsoup Document with xpath, returning the first result converted to String.
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
        org.w3c.dom.Document mappedDocument = mapJsoupDocumentToDomDocument(document);
        return xPathExpression.evaluate(mappedDocument);
    }

    /**
     * Method to parse a jsoup Document with xpath, returning all fitting elements in a NodeList.
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
        org.w3c.dom.Document mappedDocument = mapJsoupDocumentToDomDocument(document);
        return (NodeList) xPathExpression.evaluate(mappedDocument, XPathConstants.NODESET);
    }

}
