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

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.XMLUtils;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.xpath.XPathExpressionException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;

public class XMLUtilsTest extends TesterraTest {

    String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><AuthenticationResult><authInfo><accountName>-LWQyJq8qWlVKqJYdanTyFkYcAQqg5SAxAQD04JIrgc</accountName><accountUuid>c99738f1-437b-41c1-993c-9354da167c60</accountUuid><userName>TAAccount11</userName><userUuid>b44bac92-2302-4f9a-8608-8c0911f1dce7</userUuid><authLevel>NORMAL</authLevel><authMethod>USERNAME_PASSWORD</authMethod><steps>USER</steps></authInfo><authTimestamp>2014-11-20T09:23:34.057+01:00</authTimestamp><nextPWChange>2015-11-20T09:23:31.254+01:00</nextPWChange><externalReferenceID>3287850142</externalReferenceID></AuthenticationResult>";
    String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns2:exportRuleData xmlns:ns2=\"http://demail.t-systems/demail-export/rule\">\n" +
            "      <name>Regel1</name>\n" +
            "      <index>1</index>\n" +
            "      <active>true</active>\n" +
            "      <operator>OR</operator>\n" +
            "      <criteria>\n" +
            "         <criteria some=\"attribute\" xsi:type=\"ns2:HeaderCriterion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            +
            "            <value>true</value>\n" +
            "            <attribute>ATTACHMENTS</attribute>\n" +
            "         </criteria>\n" +
            "         <criteria xsi:type=\"ns2:MessageTypeCriterion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <value>true</value>\n" +
            "            <type>NORMAL</type>\n" +
            "         </criteria>\t\t \n" +
            "         <criteria xsi:type=\"ns2:MessageStateCriterion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <value>true</value>\n" +
            "            <type>SENT</type>\n" +
            "         </criteria>\n" +
            "         <criteria xsi:type=\"ns2:NumericCriterion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <attribute>MESSAGE_SIZE</attribute>\n" +
            "            <operator>GREATER_EQUALS</operator>\n" +
            "            <value>125952</value>\n" +
            "         </criteria>\n" +
            "         <criteria xsi:type=\"ns2:TextCriterion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <attribute>SUBJECT</attribute>\n" +
            "            <operator>EQUALS</operator>\n" +
            "            <value>Test</value>\n" +
            "         </criteria>\t\t \n" +
            "      </criteria>\n" +
            "</ns2:exportRuleData>\n";

    String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns2:exportRuleData xmlns:ns2=\"http://demail.t-systems/demail-export/rule\">\n" +
            "   <name>Regel1</name>\n" +
            "    <index>1</index>\n" +
            "   <active>true</active>\n" +
            "   <operator>OR</operator>\n" +
            "   <criteria>\n" +
            "       <criteria xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" some=\"attribute\" xsi:type=\"ns2:HeaderCriterion\">\n"
            +
            "           <value>true</value>\n" +
            "           <attribute>ATTACHMENTS</attribute>\n" +
            "       </criteria>\n" +
            "       <criteria xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:MessageTypeCriterion\">\n" +
            "           <value>true</value>\n" +
            "           <type>NORMAL</type>\n" +
            "       </criteria>\n" +
            "       <criteria xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:MessageStateCriterion\">\n" +
            "           <value>true</value>\n" +
            "           <type>SENT</type>\n" +
            "       </criteria>\n" +
            "       <criteria xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:NumericCriterion\">\n" +
            "           <attribute>MESSAGE_SIZE</attribute>\n" +
            "           <operator>GREATER_EQUALS</operator>\n" +
            "           <value>125952</value>\n" +
            "       </criteria>\n" +
            "       <criteria xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TextCriterion\">\n" +
            "           <attribute>SUBJECT</attribute>\n" +
            "           <operator>EQUALS</operator>\n" +
            "           <value>Test</value>\n" +
            "       </criteria>\n" +
            "   </criteria>\n" +
            "</ns2:exportRuleData>\n";

    String xml4 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "     <book>\n" +
            "       <author>Abc def</author>\n" +
            "       <isbn>1234-5-6789-0</isbn>\n" +
            "       <title>Ghi</title>\n" +
            "     </book>\n";

    String html1 = "<html>\n" +
            "           <head>\n" +
            "               <title>Testwebsite</title>\n" +
            "           </head>\n" +
            "           <body>\n" +
            "               Text\n" +
            "           </body>\n" +
            "       </html>";

    @XmlRootElement(name = "book")
    public static class Book {
        String author = "Abc def";
        String isbn = "1234-5-6789-0";
        String title = "Ghi";

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public boolean equals(Book other) {
            return this.author.equals(other.author) && this.isbn.equals(other.isbn) && this.title.equals(other.title);
        }
    }

    private static final Book book = new Book();
    private final String replacePattern = " |\\r|\\t";

    @Test
    public void testT01_XMLUtils_evaluateXPath() throws Exception {
        String out = XMLUtils.evaluateXPath(xml1, "/AuthenticationResult/authInfo/accountName");
        Assert.assertEquals(out, "-LWQyJq8qWlVKqJYdanTyFkYcAQqg5SAxAQD04JIrgc", "Text of a node");
    }

    @Test
    public void testT02_XMLUtils_evaluateXPathNodes() throws Exception {
        NodeList nodeList = XMLUtils.evaluateXPathNodes(xml2, "//criteria");
        Assert.assertEquals(nodeList.getLength(), 6, "NodeList length");
    }

    @Test
    public void testT03_XMLUtils_jaxbXMLToObject() throws Exception {
        Book book = XMLUtils.jaxbXMLToObject(xml4, Book.class);
        Assert.assertTrue(new Book().equals(book), "Converted Object from given XML Code");
    }

    @Test
    public void testT04_XMLUtils_jaxbObjectToXML() throws Exception {
        String out = XMLUtils.jaxbObjectToXML(Book.class, book);
        String acutal = out.replaceAll(replacePattern, "");
        String expected = xml4.replaceAll(replacePattern, "");

        Assert.assertEquals(acutal, expected, "Converted XML Code of given Object");
    }

    @Test
    public void testT05_JSoup_evaluateXPath() throws Exception {
        org.jsoup.nodes.Document document = XMLUtils.jsoup().createJsoupDocument(html1);
        String out = XMLUtils.jsoup().evaluateXPath(document, "html/head/title/text()");
        Assert.assertEquals(out, "Testwebsite", "Text of element in Jsoup document");
    }

    @Test
    public void testT06_JSoup_evaluateXPathNodes() throws Exception {
        org.jsoup.nodes.Document docuemnt = XMLUtils.jsoup().createJsoupDocument(xml2);
        NodeList nodeList = XMLUtils.jsoup().evaluateXPathNodes(docuemnt, "//criteria");
        Assert.assertEquals(nodeList.getLength(), 6, "Number of elements in Jsoup Document");
    }

    @Test
    public void testT07_W3cDom_evaluateXPath() throws Exception {
        org.w3c.dom.Document document = XMLUtils.w3cdom().createW3cDocument(xml1);
        String out = XMLUtils.w3cdom().evaluateXPath(document, "/AuthenticationResult/authInfo/accountName");
        Assert.assertEquals(out, "-LWQyJq8qWlVKqJYdanTyFkYcAQqg5SAxAQD04JIrgc", "Text of element in W3cDom document");
    }

    @Test
    public void testT08_W3cDom_evaluateXPathNodes() throws Exception {
        org.w3c.dom.Document document = XMLUtils.w3cdom().createW3cDocument(xml2);
        NodeList nodeList = XMLUtils.w3cdom().evaluateXPathNodes(document, "//criteria");
        Assert.assertEquals(nodeList.getLength(), 6, "Number of elements in w3cdom document");
    }

    @Test(expectedExceptions = { XPathExpressionException.class })
    public void testT14_XMLUtils_evaluateXPathInvalidXPath() throws Exception {
        String out = XMLUtils.evaluateXPath(xml1, "/AuthenticationResult <>?/authInfo/");
    }

    @Test(expectedExceptions = XPathExpressionException.class)
    public void testT15_XMLUtils_evaluateXPathInvalidXML() throws Exception {
        final String invalidXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><AuthenticationResult><authInfo>";
        String out = XMLUtils.evaluateXPath(invalidXML, "/AuthenticationResult");
    }

}
