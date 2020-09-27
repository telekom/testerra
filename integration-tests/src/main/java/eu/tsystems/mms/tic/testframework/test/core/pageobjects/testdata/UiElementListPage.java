package eu.tsystems.mms.tic.testframework.test.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import eu.tsystems.mms.tic.testframework.test.core.pageobjects.testdata.components.TableRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UiElementListPage extends Page {
    public UiElementListPage(WebDriver driver) {
        super(driver);
    }

    public UiElement getNavigation() {
        return findByQa("section/navigation");
    }

    public UiElement getTable() {
        return findByQa("section/table");
    }

    public UiElement getNavigationSubElementsByTagName() {
        return getNavigation().find(By.tagName("a"));
    }

    public UiElement getNavigationSubElementsByChildrenXPath() {
        return getNavigation().find(By.xpath("./a"));
    }

    public UiElement getNavigationSubElementsByDescendantsXPath() {
        return getNavigation().find(By.xpath(".//a"));
    }

    public UiElement getNavigationSubElementsByAbsoluteChildrenXPath() {
        return find(By.xpath("//nav[@data-qa='section/navigation']/a"));
    }

    public UiElement getNavigationSubElementsByAbsoluteDescendantsXPath() {
        return find(By.xpath("//nav[@data-qa='section/navigation']//a"));
    }

    public TableRow getTableRowsByTagName() {
        UiElement tr = getTable().find(By.tagName("tr"));
        return createComponent(TableRow.class, tr);
    }
}
