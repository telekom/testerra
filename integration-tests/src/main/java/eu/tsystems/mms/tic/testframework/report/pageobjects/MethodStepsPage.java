package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.model.TestReportSevenWebDriverSetupConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

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

    public void assertBrowserInLogsIsConfiguredBrowser(TestReportSevenWebDriverSetupConfig browserConfig) {
        List<GuiElement> browserEntries = new GuiElement(driver, By.xpath("//td[text()[contains(.,'" + BROWSER_LOG_INDICATOR + "')]]"), mainFrame).getList();
        int browserCount = 0;
        for (GuiElement browserEntry: browserEntries) {
            browserCount++;
            browserEntry.setName("BrowserEntryOnStepPage_#" + browserCount);
        }
        assertBrowserLogContent(browserEntries.size() > 1 ? browserEntries.get(browserEntries.size() - 1) : browserEntries.get(0), browserConfig);
    }

    public void assertBrowserInLogsIsConfiguredBrowser(TestReportSevenWebDriverSetupConfig browserConfig, int majorStep, int minorStep) {
        List<GuiElement> rows = getLoggingRowsByActionStep(majorStep, minorStep);
        for (GuiElement row : rows) {
            GuiElement messageColumn = row.getSubElement(By.xpath(".//td[6]"));
            if (messageColumn.getText().contains(BROWSER_LOG_INDICATOR)) {
                assertBrowserLogContent(messageColumn, browserConfig);
                return;
            }
        }
        Assert.fail("No Browser Entry found in logs in Method Details");
    }

    public void assertBrowserLogContent(GuiElement logDataCell, TestReportSevenWebDriverSetupConfig wdsConfig) {
        String messageText = logDataCell.getText().toLowerCase();
        AssertCollector.assertTrue(messageText.contains(wdsConfig.getBrowser()), "Method Details Step logs should contain browser: " + wdsConfig.getBrowser() + ". Message is " + messageText);
        AssertCollector.assertTrue(messageText.contains(wdsConfig.getVersion()), "Method Details Step logs should contain browser version: " + wdsConfig.getVersion() + ". Message is " + messageText);
    }

}
