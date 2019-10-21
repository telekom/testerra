package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by jlma on 25.01.2017.
 */
public class MethodScreenshotPage extends MethodDetailsPage {
    @Check
    private IGuiElement screenShotInfoButton = new GuiElement(this.driver, By.xpath("//i[@class='bgwhite fa fa-info']"), mainFrame);

    private IGuiElement screenShot = new GuiElement(this.driver, By.xpath("//div[@class='fotorama__html']"), mainFrame);

    public MethodScreenshotPage(WebDriver driver) {
        super(driver);
        checkPage();
    }
    public IGuiElement getScreenShot() {
        return screenShot;
    }

}
