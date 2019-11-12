package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

public class GuiElementListPage extends Page {
    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public GuiElementListPage(WebDriver driver) {
        super(driver);
    }

    public GuiElement getNavigation() {
        return new GuiElement(driver, Locate.by().qa("section/navigation"));
    }

    public GuiElement getNavigationSubElementsByTagName() {
        return getNavigation().getSubElement(Locate.by().tagName("a"));
    }

    public GuiElement getNavigationSubElementsByChildrenXPath() {
        return getNavigation().getSubElement(Locate.by().xpath("./a"));
    }

    public GuiElement getNavigationSubElementsByDescendantsXPath() {
        return getNavigation().getSubElement(Locate.by().xpath(".//a"));
    }

    public GuiElement getNavigationSubElementsByAbsoluteChildrenXPath() {
        return new GuiElement(driver, Locate.by().xpath("//nav[@data-qa='section/navigation']/a"));
    }

    public GuiElement getNavigationSubElementsByAbsoluteDescendantsXPath() {
        return new GuiElement(driver, Locate.by().xpath("//nav[@data-qa='section/navigation']//a"));
    }
}
