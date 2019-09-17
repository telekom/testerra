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
package eu.tsystems.mms.tic.testframework.utils.xmlutils;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract XML Utils class with initializer and helper methods.
 *
 * Created by pele on 06.03.2015.
 */
public abstract class AbstractXMLUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractXMLUtils.class);
    protected static String charsetName = "UTF-8";

    protected static DocumentBuilder getDocumentBuilder() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(
                "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", Thread.currentThread().getContextClassLoader());
        factory.setValidating(false);
        factory.setNamespaceAware(false);
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            throw new TesterraSystemException("Error disabling loading external dtd's. Document type definitions will be loaded externally.", e);
        }
        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new TesterraSystemException("Error creating a DocumentBuilder. Won't be able to parse xPath", e);
        }
    }

    protected static XPathFactory getXpathFactory() {
        return XPathFactory.newInstance();
    }

    /**
     * Method to create a org.w3c.dom.Document from a org.jsoup.nodes.Document.
     * Jsoup (as for v1.7.2) does not support xPath queries, so we use the Java XML-Parser, which
     * requires this w3c.dom Documents. We still load the Documents with Jsoup to utilize the loading
     * and cleaning functionality.
     *
     * @param jsoupDocument Document to be converted.
     * @return The converted w3c.org.Document.
     * @throws IOException
     * @throws SAXException
     */
    protected static org.w3c.dom.Document mapJsoupDocumentToDomDocument(Document jsoupDocument) throws IOException, SAXException {
        String html = jsoupDocument.outerHtml().replace("\n", "");
        InputStream inputStream = IOUtils.toInputStream(html);
        org.w3c.dom.Document domDocument = getDocumentBuilder().parse(inputStream);

        return domDocument;
    }

    public static String getCharsetName() {
        return charsetName;
    }

    public static void setCharsetName(String charsetName) {
        AbstractXMLUtils.charsetName = charsetName;
    }
}
