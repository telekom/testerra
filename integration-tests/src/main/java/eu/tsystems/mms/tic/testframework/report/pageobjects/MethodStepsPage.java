package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by riwa on 04.01.2017.
 */
public class MethodStepsPage extends MethodDetailsPage {


    private final String INTERNAL_STEP_LOCATOR = "//a[@id='link_viewclass_";
    private final String BROWSER_LOG_INDICATOR = "Browser:";

    private GuiElement button1Point1 = new GuiElement(this.driver, By.xpath(INTERNAL_STEP_LOCATOR + "1_1']"), mainFrame);
    private GuiElement button2Point1 = new GuiElement(this.driver, By.xpath(INTERNAL_STEP_LOCATOR + "2_1']"), mainFrame);
    private GuiElement button3Point1 = new GuiElement(this.driver, By.xpath(INTERNAL_STEP_LOCATOR + "3_1']"), mainFrame);

    private final String EXTERNAL_STEP_LOCATOR = "//div[@class='list-group textleft listitems']//a[contains(text(),";

    private GuiElement testStep1Button = new GuiElement(this.driver, By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-1')]"), mainFrame);
    private GuiElement testStep2Button = new GuiElement(this.driver, By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-2')]"), mainFrame);
    private GuiElement testStep3Button = new GuiElement(this.driver, By.xpath(EXTERNAL_STEP_LOCATOR + "'Test-Step-3')]"), mainFrame);


    public MethodStepsPage(WebDriver driver) {

        super(driver);
        checkPage();
    }

    public GuiElement getButton1Point1() {
        return button1Point1;
    }

    public GuiElement getButton2Point1() {
        return button2Point1;
    }

    public GuiElement getButton3Point1() {
        return button3Point1;
    }

    public GuiElement getTestStep1Button() {
        return testStep1Button;
    }

    public GuiElement getTestStep2Button() {
        return testStep2Button;
    }

    public GuiElement getTestStep3Button() {
        return testStep3Button;
    }

    public List<GuiElement> getLoggingRowsByActionStep(int majorStep, int minorStep) {
        List<GuiElement> rows = new GuiElement(driver, By.xpath("//tr[contains(@class,'viewclass_" + majorStep + "_" + minorStep + "')]"), mainFrame).getList();
        return rows;
    }

}
