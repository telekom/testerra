package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by riwa on 04.01.2017.
 */
public class MethodStackPage extends MethodDetailsPage {

    private GuiElement stackTraceString = new GuiElement(this.driver, By.xpath("//*[@id='exception']//div[@class='textLeft']"), mainFrame);

    public MethodStackPage(WebDriver driver) {

        super(driver);
        checkPage();
    }

    public String getStackTrace() {
        return stackTraceString.getText();
    }

}
