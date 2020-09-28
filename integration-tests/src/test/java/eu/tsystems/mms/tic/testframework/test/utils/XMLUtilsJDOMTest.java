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
 package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.test.testdata.implementation.XMLUtilTestSupport;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.XMLUtils;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.testng.annotations.Test;

/**
 * XMLUtils - JDOM lib tests.
 *
 * @author mgn, rest
 */
public class XMLUtilsJDOMTest extends TesterraTest {

    // Same as JDOMNodeTest_valid.xml
    private String xmlFromString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<test>\n"
            + "    <child1 attr1=\"default\">\n"
            + "        <subchild1 attr1=\"default\"/>\n"
            + "        <subchild2 attr2=\"foobar\"/>\n"
            + "        <subchild3>FooBar</subchild3>\n"
            + "    </child1>"
            + "    <child2>\n"
            + "        <subchild3>BarFoo</subchild3>\n"
            + "        <subchild4>Lorem ipsum</subchild4>\n"
            + "    </child2>\n"
            + "    <child3>\n"
            + "        <subchild5 attr3=\"lorem1\"/>\n"
            + "        <subchild5 attr3=\"lorem2\"/>\n"
            + "    </child3>\n"
            + "</test>";

    @Test
    public void testT01_XMLUtilsJDOM_documentToString() throws Exception {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        Document documentFromString = XMLUtils.jdom().createDocumentFromString(xmlFromString);
        XMLUtilTestSupport.verifyDocuments(docSource, documentFromString);
    }

    @Test(expectedExceptions = JDOMParseException.class)
    public void testT02_XMLUtilsJDOM_createDocumentFromInvalidXML() throws IOException, JDOMException {
        String xmlSource = XMLUtilTestSupport.getStringFromResource("/testfiles/xmlutils/JDOMtest_invalid.xml");
        XMLUtils.jdom().createDocumentFromString(xmlSource);
    }

}
