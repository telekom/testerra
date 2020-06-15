package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodStackPage extends MethodDetailsPage {

    private GuiElement stackTraceString = new GuiElement(this.getWebDriver(), By.xpath("//*[@id='exception']//div[@class='textLeft']"), mainFrame);

    public MethodStackPage(WebDriver driver) {

        super(driver);
        checkPage();
    }

    public String getStackTrace() {
        return stackTraceString.getText();
    }

}
