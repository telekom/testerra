package eu.tsystems.mms.tic.testframework.core.test.pageobjects;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
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
    }

    @Test
    public void locateByClass() {
        UiElement div = page.findByXPath(XPath.from("div").classNames("element", "multiple"));
        div.text().contains("This element has multiple classes");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByText_fails() {
        UiElement realA = page.findByQa("action/linkWithFormattedText");
        UiElement a = page.findByXPath(XPath.from("a").text(textToFind));
        a.text().is(realA.text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByTextContains_fails() {
        UiElement realA = page.findByQa("action/linkWithFormattedText");
        UiElement a = page.findByXPath(XPath.from("a").textContains(textToFind));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void locateByWords() {
        UiElement realA = page.findByQa("action/linkWithFormattedText");
        UiElement a = page.findByXPath(XPath.from("a").textWords(textToFind));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void locateByContains() {
        UiElement realA = page.findByQa("action/linkWithFormattedText");
        UiElement a = page.findByXPath(
            XPath.from("a")
                .textWords("This link has some text!")
                .contains("span")
                    .textWords("Subtext")
        );
        a.text().is(realA.text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByClassName_fails() {
        UiElement div = page.find(By.className("header"));
        div.text().is("You found me");
    }

    @Test
    public void testLocateByClassWord() {
        UiElement div = page.findByXPath(XPath.from("*").classNames("header","large"));
        div.text().is("You found me");
    }

    @Test
    public void testLocatePosition() {
        UiElement li;
        li = page.findByXPath(XPath.from("ul").classNames("list-group").select("li",1));
        li.text().is("Affe");

        li = page.findByXPath(XPath.from("ul").classNames("list-group").select("li",2));
        li.text().is("Katze");

        li = page.findByXPath(XPath.from("ul").classNames("list-group").select("li",-1));
        li.text().is("Kuh");
    }

    @Test
    public void testLocateByWords() {
        UiElement div = page.findByXPath(XPath.from("*").textWords("Login here"));
        div.value("data-qa").is("action/login");
    }
}
