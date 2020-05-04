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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.core.test.testdata.implementation.XMLUtilTestSupport;
import eu.tsystems.mms.tic.testframework.utils.XMLUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * XMLUtil - JDOM node tests.
 *
 * @author mgn
 */
public class XMLUtilsJDOMNodeTest extends AbstractWebDriverTest {

    @Test
    public void testT01_XMLUtilsJDOM_getNodeValue() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild3";
        final String nodeValue = "FooBar";

        String value = XMLUtils.jdom().getNodeValue(docSource, nodeName);
        Assert.assertEquals(nodeValue, value, "Value of given existing nodename");
    }

    @Test
    public void testT02_XMLUtilsJDOM_getNodeValue_Null_Node() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        String result = XMLUtils.jdom().getNodeValue(docSource, null);
        Assert.assertNull(result, "Value of a given null node ");
    }

    @Test
    public void testT03_XMLUtilsJDOM_getNodeValue_Null_Doc() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild3";

        String result = XMLUtils.jdom().getNodeValue(null, nodeName);
        Assert.assertNull(result, "Value of a given null document ");
    }

    @Test
    public void testT04_XMLUtilsJDOM_getNodeValue_unknown_Node() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "unknown";

        String value = XMLUtils.jdom().getNodeValue(docSource, nodeName);
        Assert.assertNull(value, "Node value of a unknown nodename ");
    }

    @Test
    public void testT05_XMLUtilsJDOM_isExistNode_True() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild1";

        boolean result = XMLUtils.jdom().isExistsNode(docSource, nodeName);
        Assert.assertTrue(result, "Nodename exists in the document.");
    }

    @Test
    public void testT06_XMLUtilsJDOM_isExistNode_False() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "unknown";

        boolean result = XMLUtils.jdom().isExistsNode(docSource, nodeName);
        Assert.assertFalse(result, "Unknown nodename does not exist in the document.");
    }

    @Test
    public void testT07_XMLUtilsJDOM_isExistNode_Null() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = null;

        boolean result = XMLUtils.jdom().isExistsNode(docSource, nodeName);
        Assert.assertFalse(result, "null nodename does not exist in the document.");
    }

    @Test
    public void test_T10_XMLUtilsJDOM_addNode() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String parentNodeName = "subchild2";
        final String newNodeName = "newnode";
        final String newNodeValue = "";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_T10_expected.xml");

        XMLUtils.jdom().addNode(docSource, parentNodeName, newNodeName, newNodeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void test_T11_XMLUtilsJDOM_addNode_Value() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String parentNodeName = "subchild2";
        final String newNodeName = "newnode";
        final String newNodeValue = "newvalue";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_T11_expected.xml");

        XMLUtils.jdom().addNode(docSource, parentNodeName, newNodeName, newNodeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void test_T12_XMLUtilsJDOM_addNode_Attributes() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String parentNodeName = "subchild2";
        final String newNodeName = "newnode";
        final String newNodeValue = "newvalue";
        final String newAttribute = "attribute1";
        final String newAttributeValue = "attribute value";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_T12_expected.xml");

        XMLUtils.jdom().addNode(docSource, parentNodeName, newNodeName, newNodeValue, newAttribute, newAttributeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void test_T13_XMLUtilsJDOM_changeNodeValue() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild3";
        final String newNodeValue = "Value was changed.";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_T13_expected.xml");

        XMLUtils.jdom().changeNodeValue(docSource, nodeName, newNodeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void test_T14_XMLUtilsJDOM_changeNodeValue_check_change() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild3";
        final String nodeNameValue = "FooBar";
        final String newNodeValue = "Value was changed.";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_T13_expected.xml");

        XMLUtils.jdom().changeNodeValue(docSource, nodeName, nodeNameValue, newNodeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void test_T15_XMLUtilsJDOM_changeNodeValue_check_no_change() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild3";
        final String nodeNameValue = "FooBar-invalid";
        final String newNodeValue = "Value was changed.";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");

        XMLUtils.jdom().changeNodeValue(docSource, nodeName, nodeNameValue, newNodeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

    @Test
    public void test_T16_XMLUtilsJDOM_changeAllNodeValues() throws IOException, JDOMException {
        Document docSource = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_valid.xml");
        final String nodeName = "subchild3";
        final String newNodeValue = "Value was changed.";
        Document expected = XMLUtilTestSupport.getDocumentFromResource("/testfiles/xmlutils/JDOMNodeTest_T16_expected.xml");

        XMLUtils.jdom().changeAllNodeValues(docSource, nodeName, newNodeValue);

        XMLUtilTestSupport.verifyDocuments(docSource, expected);
    }

}
