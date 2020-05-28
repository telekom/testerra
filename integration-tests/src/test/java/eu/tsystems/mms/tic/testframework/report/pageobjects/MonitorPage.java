package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MonitorPage extends AbstractReportPage {

    @Check
    private GuiElement headLine = new GuiElement(this.getWebDriver(), By.xpath("//h5[text()='JVM Monitor']"), mainFrame);

    public MonitorPage(WebDriver driver) {
        super(driver);
    }

    public void assertPageIsDisplayedCorrectly(){
        GuiElement memoryUsageChart = new GuiElement(this.getWebDriver(), By.id("ConsumptionMeasurementsView1"), mainFrame);
        GuiElement memoryReservedChart = new GuiElement(this.getWebDriver(), By.id("ConsumptionMeasurementsView2"), mainFrame);
        GuiElement processorUsageChart = new GuiElement(this.getWebDriver(), By.id("ConsumptionMeasurementsView3"), mainFrame);

        memoryUsageChart.asserts().assertIsDisplayed();
        memoryReservedChart.asserts().assertIsDisplayed();
        processorUsageChart.asserts().assertIsDisplayed();
    }
}
