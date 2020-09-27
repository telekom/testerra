package eu.tsystems.mms.tic.testframework.test;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.testing.UiElementCreator;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LocatorTest extends AbstractTestSitesTest implements UiElementCreator {

    private final String textToFind = "This link has some text!";

    @Test
    public void locateByClass() {
        UiElement div = find(XPath.from("div").classes("element", "multiple"));
        div.text().contains("This element has multiple classes");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByText_fails() {
        UiElement realA = findByQa("action/linkWithFormattedText");
        UiElement a = find(XPath.from("a").text().is(textToFind));
        a.text().is(realA.text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByTextContains_fails() {
        UiElement realA = findByQa("action/linkWithFormattedText");
        UiElement a = find(XPath.from("a").text().contains(textToFind));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void locateByWords() {
        UiElement realA = findByQa("action/linkWithFormattedText");
        UiElement a = find(XPath.from("a").text().hasWords(textToFind));
        a.text().is(realA.text().getActual());
    }

    @Test
    public void locateByContains() {
        UiElement realA = findByQa("action/linkWithFormattedText");
        UiElement a = find(
            XPath.from("a")
                .text().hasWords("This link has some text!")
                .contains("span")
                    .text().hasWords("Subtext")
        );
        a.text().is(realA.text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByClassName_fails() {
        UiElement div = find(By.className("header"));
        div.text().is("You found me");
    }

    @Test
    public void testLocateByClassWord() {
        UiElement div = find(XPath.from("*").classes("header","large"));
        div.text().is("You found me");
    }

    @Test
    public void testLocatePosition() {
        UiElement li;
        li = find(XPath.from("ul").classes("list-group").select("li",1));
        li.text().is("Affe");

        li = find(XPath.from("ul").classes("list-group").select("li",2));
        li.text().is("Katze");

        li = find(XPath.from("ul").classes("list-group").select("li",-1));
        li.text().is("Kuh");
    }

    @Test
    public void testLocateByWords() {
        UiElement div = find(XPath.from("*").text().hasWords("Login here"));
        div.value("data-qa").is("action/login");
    }
}
