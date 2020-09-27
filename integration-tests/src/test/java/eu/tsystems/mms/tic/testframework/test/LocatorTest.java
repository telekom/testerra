package eu.tsystems.mms.tic.testframework.test;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFinder;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LocatorTest extends AbstractTestSitesTest {

    private final String textToFind = "This link has some text!";

    private UiElementFinder getFinder() {
        return finderFactory.create(getWebDriver());
    }

    @Test
    public void locateByClass() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(XPath.from("div").classes("element", "multiple"));
        div.expectThat().text().contains("This element has multiple classes");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByText_fails() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(XPath.from("a").text().is(textToFind));
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByTextContains_fails() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(XPath.from("a").text().contains(textToFind));
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test
    public void locateByWords() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(XPath.from("a").text().hasWords(textToFind));
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test
    public void locateByContains() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(
            XPath.from("a")
                .text().hasWords("This link has some text!")
                .contains("span")
                    .text().hasWords("Subtext")
        );
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByClassName_fails() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(By.className("header"));
        div.expectThat().text().is("You found me");
    }

    @Test
    public void testLocateByClassWord() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(XPath.from("*").classes("header","large"));
        div.expectThat().text().is("You found me");
    }

    @Test
    public void testLocatePosition() {
        UiElementFinder finder = getFinder();
        UiElement li;
        li = finder.find(XPath.from("ul").classes("list-group").select("li",1));
        li.expectThat().text().is("Affe");

        li = finder.find(XPath.from("ul").classes("list-group").select("li",2));
        li.expectThat().text().is("Katze");

        li = finder.find(XPath.from("ul").classes("list-group").select("li",-1));
        li.expectThat().text().is("Kuh");
    }

    @Test
    public void testLocateByWords() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(XPath.from("*").text().hasWords("Login here"));
        div.expectThat().value("data-qa").is("action/login");
    }
}
