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
import org.openqa.selenium.By;
import org.testng.Assert;
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
        Assert.assertEquals(XPath.from("myElement").attribute("data-qa").hasWords("hello","world").toString(), "//myElement[contains(concat(' ', normalize-space(@data-qa), ' '), ' hello ') and contains(concat(' ', normalize-space(@data-qa), ' '), ' world ')]");
        Assert.assertEquals(XPath.from("myElement").attribute("data-qa").hasWords("hello world").toString(), "//myElement[contains(concat(' ', normalize-space(@data-qa), ' '), ' hello world ')]");
        Assert.assertEquals(XPath.from("myElement").attribute("data-qa").present().toString(), "//myElement[@data-qa]");
        Assert.assertEquals(XPath.from("/*").attribute("local-name()", "svg").attribute("role", "img").toString(), "/*[local-name()='svg' and @role='img']");
    }

    @Test
    public void deepSelection() {
        Assert.assertEquals(XPath.from("button").attribute("data-qa").present().encloses("span").text("Klick mich").toString(), "//button[@data-qa and descendant::span[.//text()='Klick mich']]");
        Assert.assertEquals(XPath.from("h2").attribute(Attribute.TITLE).is("Head").select("div").toString(), "//h2[@title='Head']//div");
        Assert.assertEquals(XPath.from("h2").attribute(Attribute.TITLE).is("Head").select("/span").toString(), "//h2[@title='Head']/span");
        Assert.assertEquals(XPath.from("h2").attribute(Attribute.TITLE, null).select("./span").toString(), "//h2[@title]/span");
        Assert.assertEquals(XPath.from("h2").encloses("./span").text("title").toString(), "//h2[child::span[.//text()='title']]");
        Assert.assertEquals(XPath.from("table").encloses("th").text("Quote ID").select("tbody").toString(), "//table[descendant::th[.//text()='Quote ID']]//tbody");
    }

    @Test
    public void classSelection() {
        Assert.assertEquals(XPath.from("body").select("nav").classes("container").toString(), "//body//nav[contains(concat(' ', normalize-space(@class), ' '), ' container ')]");
    }

    @Test
    public void xPath_with_xPath() {
        Assert.assertEquals(XPath.from("body").encloses(XPath.from("div")).toString(), "//body[descendant::div]");
        Assert.assertEquals(XPath.from("body").encloses(XPath.from("./div")).toString(), "//body[child::div]");
        Assert.assertEquals(XPath.from("body").select(XPath.from("div")).toString(), "//body//div");
        Assert.assertEquals(XPath.from("body").select(XPath.from("./div")).toString(), "//body/div");
    }

    @Test
    public void xPath_with_By() {
        Assert.assertEquals(XPath.from("body").encloses(By.name("name")).toString(), "//body[descendant::*[@name='name']]");
        Assert.assertEquals(XPath.from("body").select(By.id("id")).toString(), "//body//*[@id='id']");
    }

    @Test
    public void XPath_from_Group() {
        Assert.assertEquals(XPath.from("(//body|//iframe)").toString(), "(//body|//iframe)");
        Assert.assertEquals(XPath.from("(//body|//iframe)").select("textarea").toString(), "(//body|//iframe)//textarea");
        Assert.assertEquals(XPath.from("(//body|//iframe)").encloses("textarea").toString(), "(//body|//iframe)[descendant::textarea]");
    }
}
