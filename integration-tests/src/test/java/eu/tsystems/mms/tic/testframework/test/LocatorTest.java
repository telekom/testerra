package eu.tsystems.mms.tic.testframework.test;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LocatorTest extends AbstractTestSitesTest implements UiElementFinderFactoryProvider {

    private final String textToFind = "This link has some text!";

    private UiElementFinder getFinder() {
        return uiElementFinderFactory.create(getClassExclusiveWebDriver());
    }

    @Test
    public void locateByClass() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(XPath.from("div").classes("element", "multiple"));
        div.expect().text().contains("This element has multiple classes");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByText_fails() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(XPath.from("a").text().is(textToFind));
        a.expect().text().is(realA.waitFor().text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByTextContains_fails() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(XPath.from("a").text().contains(textToFind));
        a.expect().text().is(realA.waitFor().text().getActual());
    }

    @Test
    public void locateByWords() {
        UiElementFinder finder = getFinder();
        UiElement realA = finder.findByQa("action/linkWithFormattedText");
        UiElement a = finder.find(XPath.from("a").text().hasWords(textToFind));
        a.expect().text().is(realA.waitFor().text().getActual());
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
        a.expect().text().is(realA.waitFor().text().getActual());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void locateByClassName_fails() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(By.className("header"));
        div.expect().text().is("You found me");
    }

    @Test
    public void testLocateByClassWord() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(XPath.from("*").classes("header","large"));
        div.expect().text().is("You found me");
    }

    @Test
    public void testLocatePosition() {
        UiElementFinder finder = getFinder();
        UiElement li;
        li = finder.find(XPath.from("ul").classes("list-group").select("li",1));
        li.expect().text().is("Affe");

        li = finder.find(XPath.from("ul").classes("list-group").select("li",2));
        li.expect().text().is("Katze");

        li = finder.find(XPath.from("ul").classes("list-group").select("li",-1));
        li.expect().text().is("Kuh");
    }

    @Test
    public void testLocateByWords() {
        UiElementFinder finder = getFinder();
        UiElement div = finder.find(XPath.from("*").text().hasWords("Login here"));
        div.expect().attribute("data-qa").is("action/login");
    }
}
