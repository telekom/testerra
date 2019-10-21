package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by riwa on 04.01.2017.
 */
public class MethodStepsPage extends MethodDetailsPage {


    private final String INTERNAL_STEP_LOCATOR = "//a[@id='link_viewclass_";
    private final String BROWSER_LOG_INDICATOR = "Browser:";

    private IGuiElement button1Point1 = new GuiElement(this.driver, By.xpath(INTERNAL_STEP_LOCATOR + "1_1']"), mainFrame);
    private IGuiElement button2Point1 = new GuiElement(this.driver, By.xpath(INTERNAL_STEP_LOCATOR + "2_1']"), mainFrame);
    private IGuiElement button3Point1 = new GuiElement(this.driver, By.xpath(INTERNAL_STEP_LOCATOR + "3_1']"), mainFrame);

    private final String EXTERNAL_STEP_LOCATOR = "//div[@class='list-group textleft listitems']//a[contains(text(),";

    private IGuiElement testStep1Button = new GuiElement(this.driver, By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-1')]"), mainFrame);
    private IGuiElement testStep2Button = new GuiElement(this.driver, By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-2')]"), mainFrame);
    private IGuiElement testStep3Button = new GuiElement(this.driver, By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-3')]"), mainFrame);


    public MethodStepsPage(WebDriver driver) {

        super(driver);
        checkPage();
    }

    public IGuiElement getButton1Point1() {
        return button1Point1;
    }

    public IGuiElement getButton2Point1() {
        return button2Point1;
    }

    public IGuiElement getButton3Point1() {
        return button3Point1;
    }

    public IGuiElement getTestStep1Button() {
        return testStep1Button;
    }

    public IGuiElement getTestStep2Button() {
        return testStep2Button;
    }

    public IGuiElement getTestStep3Button() {
        return testStep3Button;
    }

    public List<IGuiElement> getLoggingRowsByActionStep(int majorStep, int minorStep) {
        List<IGuiElement> rows = new GuiElement(driver, By.xpath("//tr[contains(@class,'viewclass_" + majorStep + "_" + minorStep + "')]"), mainFrame).getList();
        return rows;
    }

}
