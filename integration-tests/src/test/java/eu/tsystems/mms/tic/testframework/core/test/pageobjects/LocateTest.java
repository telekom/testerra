package eu.tsystems.mms.tic.testframework.core.test.pageobjects;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LocateTest extends AbstractTestSitesTest {

    private WebTestPage page;

    @BeforeMethod
    private void createPage() {
        page = pageFactory.createPage(WebTestPage.class);
        page.getWebDriver().navigate().to(TestPage.INPUT_TEST_PAGE.getUrl());
    }

    @Test
    public void testLocateByClass() {
        IGuiElement div = page.find(Locate.by(XPath.node("div").withClass("element", "multiple")));
        div.text().contains("This element has multiple classes");
    }

    @Test
    public void testLocateByText() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(XPath.node("a").withWords("This link has some text!")));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void testLocateByWords() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(XPath.node("a").withWords("some", "text!")));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void testLocateByContains() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(XPath.node("a").withWords("some", "text!").contains("span").withWords("Subtext")));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void testLocateBySelect() {
        IGuiElement a = page.find(Locate.by(XPath.node("a").withWords("This link").contains("span").withWords("Subtext")));
        a.value("data-qa").contains("linkWithFormattedText");
    }
}
