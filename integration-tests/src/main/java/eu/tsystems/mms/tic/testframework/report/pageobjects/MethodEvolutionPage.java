package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by riwa on 04.01.2017.
 */
public class MethodEvolutionPage extends MethodDetailsPage {

    private IGuiElement graphView = new GuiElement(this.driver, By.id("historygrapharea"), mainFrame);

    private IGuiElement noEvolutionIndicator = graphView.getSubElement(By.tagName("h6"));


    public MethodEvolutionPage(WebDriver driver) {
        super(driver);
    }

    public IGuiElement getNoEvolutionIndicator() {
        return noEvolutionIndicator;
    }
}
