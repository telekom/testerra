package eu.tsystems.mms.tic.testframework.test;

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
    }
}
