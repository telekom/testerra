package eu.tsystems.mms.tic.testframework.pageobjects.google;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class StartPage extends Page {
    private IGuiElement searchField = find(By.name("q"));
    private IGuiElement searchBtn = find(By.name("btnK"));

    public StartPage(WebDriver driver) {
        super(driver);
    }

    public ResultPage search(String string) {
        searchField.type(string);
        searchBtn.click();
        return pageFactory.createPage(ResultPage.class);
    }
}
