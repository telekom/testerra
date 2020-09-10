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

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.test.testdata.implementation.XMLUtilTestSupport;
import eu.tsystems.mms.tic.testframework.utils.XMLUtils;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * XMLUtils - JDOM lib tests.
 *
 * @author mgn, rest
 */
public class XMLUtilsJDOMAttributeTest extends AbstractWebDriverTest {

    @Test
    public void testT01_XMLUtilsJDOM_isExistsAttribute_true() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild2";
        final String attributeName = "attr2";

        boolean result = XMLUtils.jdom().isExistsAttribute(docSource, nodeName, attributeName);

        Assert.assertTrue(result, "Attribute name is existing.");
    }

    @Test
    public void testT02_XMLUtilsJDOM_isExistsAttribute_false() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild2";
        final String attributeName = "unknown";

        boolean result = XMLUtils.jdom().isExistsAttribute(docSource, nodeName, attributeName);

        Assert.assertFalse(result, "Attribute does not exist.");
    }

    @Test
    public void testT03_XMLUtilsJDOM_addNewAttribute() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild4";
        final String newAttributeName = "newattribute";
        final String newAttributeValue = "newvalue";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMAttributeTest_T03_expected.xml");

        XMLUtils.jdom().addAttribute(docSource, nodeName, newAttributeName, newAttributeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void testT04_XMLUtilsJDOM_changeAttributeValue() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "child1";
        final String attribtueName = "attr1";
        final String newAttributeValue = "newValue\"";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMAttributeTest_T04_expected.xml");

        XMLUtils.jdom().changeAttributeValue(docSource, nodeName, attribtueName, newAttributeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void testT05_XMLUtilsJDOM_changeAttributeValues_recursiv() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String attribtueName = "attr1";
        final String newAttributeValue = "newValue\"";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMAttributeTest_T05_expected.xml");

        XMLUtils.jdom().changeAttributeValues(docSource, attribtueName, newAttributeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void testT06_XMLUtilsJDOM_changeAttributeValue_oldValue() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String attributeName = "attr3";
        final String newAttributeValue = "newlorem";
        final String oldAttributeValue = "lorem2";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMAttributeTest_T06_expected.xml");

        XMLUtils.jdom().changeAttributeValues(docSource, attributeName, newAttributeValue, oldAttributeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void testT07_XMLUtilsJDOM_removeAttribute() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild2";
        final String attributeName = "attr2";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMAttributeTest_T07_expected.xml");

        XMLUtils.jdom().removeAttribute(docSource, nodeName, attributeName);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

}
