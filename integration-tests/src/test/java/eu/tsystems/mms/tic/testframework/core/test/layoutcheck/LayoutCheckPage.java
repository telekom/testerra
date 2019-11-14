package eu.tsystems.mms.tic.testframework.core.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.WebDriver;

public class LayoutCheckPage extends Page {
    public LayoutCheckPage(WebDriver driver) {
        super(driver);
    }
    public IGuiElement getGuiElementQa(String qa) {
        return findByQa(qa);
    }
}
