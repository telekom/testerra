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
    private final String textToFind = "This link has some text!";

    @BeforeMethod
    private void createPage() {
        page = pageFactory.createPage(WebTestPage.class);
        page.getWebDriver().navigate().to(TestPage.INPUT_TEST_PAGE.getUrl());
    }

    @Test
    public void locateByClass() {
        IGuiElement div = page.find(Locate.by(XPath.node("div").hasClass("element", "multiple")));
        div.text().contains("This element has multiple classes");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByText_fails() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(XPath.node("a").textIs(textToFind)));
        a.text().is(realA.text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByTextContains_fails() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(XPath.node("a").textContains(textToFind)));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void locateByWords() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(XPath.node("a").textWords(textToFind)));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void locateByContains() {
        IGuiElement realA = page.findByQa("action/linkWithFormattedText");
        IGuiElement a = page.find(Locate.by(
            XPath.node("a")
                .attribute("data-qa", "action/linkWithFormattedText")
                .textWords(textToFind)
                .contains("span")
                    .textWords("Subtext"))
        );
        a.text().is(realA.text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByClassName_fails() {
        IGuiElement div = page.find(By.className("header"));
        div.text().is("You found me");
    }

    @Test
    public void testLocateByClassWord() {
        IGuiElement div = page.find(Locate.by(XPath.node("*").hasClass("header","large")));
        div.text().is("You found me");
    }

    @Test
    public void testLocatePosition() {
        IGuiElement li;
        li = page.find(Locate.by(XPath.node("ul").hasClass("list-group").select("li",1)));
        li.text().is("Affe");

        li = page.find(Locate.by(XPath.node("ul").hasClass("list-group").select("li",2)));
        li.text().is("Katze");

        li = page.find(Locate.by(XPath.node("ul").hasClass("list-group").select("li",-1)));
        li.text().is("Kuh");
    }
}
