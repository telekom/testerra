package eu.tsystems.mms.tic.testframework.pageobjects.google;

import eu.tsystems.mms.tic.testframework.pageobjects.FluentPage;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ResultPage extends FluentPage {
    public ResultPage(WebDriver driver) {
        super(driver);
    }

    public ResultItem result(int position) {
        IGuiElement resultItem = find(By.xpath(String.format("//div[@class='rc'][%d]", position)));
        return withAncestor(resultItem).createComponent(ResultItem.class);
    }

    @Override
    protected ResultPage self() {
        return this;
    }
}
