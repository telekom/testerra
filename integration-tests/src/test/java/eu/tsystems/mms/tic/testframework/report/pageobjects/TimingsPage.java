package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TimingsPage extends AbstractReportPage {

    @Check
    private GuiElement headLine = new GuiElement(this.getWebDriver(), By.xpath("//h5[text()='Timings']"), mainFrame);

    public TimingsPage(WebDriver driver) {
        super(driver);
    }

    public void assertPageIsDisplayedCorrectly(){
        GuiElement barChartNumberOfActionTypes = new GuiElement(this.getWebDriver(), By.id("bars"), mainFrame);
        GuiElement dotplotValuesPerActionType = new GuiElement(this.getWebDriver(), By.id("points"), mainFrame);

        barChartNumberOfActionTypes.asserts().assertIsDisplayed();
        dotplotValuesPerActionType.asserts().assertIsDisplayed();
    }
}
