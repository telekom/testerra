package eu.tsystems.mms.tic.testframework.test;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LocatorTest extends AbstractTestSitesTest {

    private final String textToFind = "This link has some text!";

    @Test
    public void locateByClass() {
        UiElement div = getUiElementFinder().find(XPath.from("div").classes("element", "multiple"));
        div.expectThat().text().contains("This element has multiple classes");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByText_fails() {
        UiElement realA = getUiElementFinder().findByQa("action/linkWithFormattedText");
        UiElement a = getUiElementFinder().find(XPath.from("a").text().is(textToFind));
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByTextContains_fails() {
        UiElement realA =  getUiElementFinder().findByQa("action/linkWithFormattedText");
        UiElement a =  getUiElementFinder().find(XPath.from("a").text().contains(textToFind));
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test
    public void locateByWords() {
        UiElement realA =  getUiElementFinder().findByQa("action/linkWithFormattedText");
        UiElement a =  getUiElementFinder().find(XPath.from("a").text().hasWords(textToFind));
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test
    public void locateByContains() {
        UiElement realA =  getUiElementFinder().findByQa("action/linkWithFormattedText");
        UiElement a =  getUiElementFinder().find(
            XPath.from("a")
                .text().hasWords("This link has some text!")
                .contains("span")
                    .text().hasWords("Subtext")
        );
        a.expectThat().text().is(realA.waitFor().text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByClassName_fails() {
        UiElement div =  getUiElementFinder().find(By.className("header"));
        div.expectThat().text().is("You found me");
    }

    @Test
    public void testLocateByClassWord() {
        UiElement div =  getUiElementFinder().find(XPath.from("*").classes("header","large"));
        div.expectThat().text().is("You found me");
    }

    @Test
    public void testLocatePosition() {
        UiElement li;
        li =  getUiElementFinder().find(XPath.from("ul").classes("list-group").select("li",1));
        li.expectThat().text().is("Affe");

        li =  getUiElementFinder().find(XPath.from("ul").classes("list-group").select("li",2));
        li.expectThat().text().is("Katze");

        li =  getUiElementFinder().find(XPath.from("ul").classes("list-group").select("li",-1));
        li.expectThat().text().is("Kuh");
    }

    @Test
    public void testLocateByWords() {
        UiElement div =  getUiElementFinder().find(XPath.from("*").text().hasWords("Login here"));
        div.expectThat().value("data-qa").is("action/login");
    }
}
