package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodEvolutionPage extends MethodDetailsPage {

    private GuiElement graphView = new GuiElement(this.getWebDriver(), By.id("historygrapharea"), mainFrame);

    private GuiElement noEvolutionIndicator = graphView.getSubElement(By.tagName("h6"));


    public MethodEvolutionPage(WebDriver driver) {
        super(driver);
    }

    public GuiElement getNoEvolutionIndicator() {
        return noEvolutionIndicator;
    }
}
