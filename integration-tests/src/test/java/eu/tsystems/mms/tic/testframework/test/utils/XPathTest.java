/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.Test;

public class XPathTest extends TesterraTest  {

    @Test
    public void textFunctions() {
        Assert.assertEquals(XPath.from("myElement").text().is("Exakt").toString(), "//myElement[.//text()='Exakt']");
        Assert.assertEquals(XPath.from("myElement").text("Exakt").toString(), "//myElement[.//text()='Exakt']");
        Assert.assertEquals(XPath.from("myElement").text().contains("Contains").toString(), "//myElement[contains(.//text(),'Contains')]");
        Assert.assertEquals(XPath.from("myElement").text().startsWith("Start").toString(), "//myElement[starts-with(.//text(),'Start')]");
        Assert.assertEquals(XPath.from("myElement").text().endsWith("End").toString(), "//myElement[ends-with(.//text(),'End')]");
    }

    @Test
    public void attributeFunctions() {
        Assert.assertEquals(XPath.from("myElement").attribute("data-qa").hasWords("test_id").toString(), "//myElement[contains(concat(' ', normalize-space(@data-qa), ' '), ' test_id ')]");
        Assert.assertEquals(XPath.from("myElement").attribute("data-qa").present().toString(), "//myElement[@data-qa]");
    }

    @Test
    public void deepSelection() {
        Assert.assertEquals(XPath.from("button").attribute("data-qa").present().contains("span").text().is("Klick mich").toString(), "//button[@data-qa and descendant::span[.//text()='Klick mich']]");
        Assert.assertEquals(XPath.from("h2").attribute(Attribute.TITLE).is("Head").select("div").toString(), "//h2[@title='Head']//div");
        Assert.assertEquals(XPath.from("h2").attribute(Attribute.TITLE).is("Head").select("/span").toString(), "//h2[@title='Head']/span");
        Assert.assertEquals(XPath.from("table").contains("th").text("Quote ID").select("tbody").toString(), "//table[descendant::th[.//text()='Quote ID']]//tbody");
    }
}
