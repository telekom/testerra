package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.components.TableRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NewGuiElementListPage extends Page {
    public NewGuiElementListPage(WebDriver driver) {
        super(driver);
    }

    public IGuiElement getNavigation() {
        return findByQa("section/navigation");
    }

    public IGuiElement getTable() {
        return findByQa("section/table");
    }

    public IGuiElement getNavigationSubElementsByTagName() {
        return getNavigation().find(By.tagName("a"));
    }

    public IGuiElement getNavigationSubElementsByChildrenXPath() {
        return getNavigation().find(By.xpath("./a"));
    }

    public IGuiElement getNavigationSubElementsByDescendantsXPath() {
        return getNavigation().find(By.xpath(".//a"));
    }

    public IGuiElement getNavigationSubElementsByAbsoluteChildrenXPath() {
        return find(By.xpath("//nav[@data-qa='section/navigation']/a"));
    }

    public IGuiElement getNavigationSubElementsByAbsoluteDescendantsXPath() {
        return find(By.xpath("//nav[@data-qa='section/navigation']//a"));
    }

    public TableRow getTableRowsByTagName() {
        IGuiElement tr = getTable().find(By.tagName("tr"));
        return createComponent(TableRow.class, tr);
    }
}
