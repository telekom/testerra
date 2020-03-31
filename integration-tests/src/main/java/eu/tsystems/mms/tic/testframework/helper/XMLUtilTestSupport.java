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
package eu.tsystems.mms.tic.testframework.helper;

import eu.tsystems.mms.tic.testframework.utils.XMLUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.testng.Assert;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.io.IOException;
import java.io.InputStream;

/**
 * XMLUtil test support class.
 *
 * @author mgn
 */
public class XMLUtilTestSupport {

    public static synchronized Document getDocumentFromResource(String fileName) throws IOException, JDOMException {
        String xmlString = getStringFromResource(fileName);
        return XMLUtils.jdom().createDocumentFromString(xmlString);
    }

    public static synchronized String getStringFromResource(String fileName) throws IOException, JDOMException {
        InputStream is = XMLUtilTestSupport.class.getResourceAsStream(fileName);
        return IOUtils.toString(is, Charsets.UTF_8);
    }

    public static synchronized void verifyDocuments(Document source, Document expected) {
        XMLOutputter outputter = new XMLOutputter();

        String sourceString = outputter.outputString(source);
        String expectedString = outputter.outputString(expected);

        Diff diff = DiffBuilder.compare(sourceString).withTest(expectedString).ignoreComments().ignoreWhitespace().build();
        Assert.assertFalse(diff.hasDifferences(), "XML Documents are equal\n" + diff.getDifferences().toString());
    }
}
