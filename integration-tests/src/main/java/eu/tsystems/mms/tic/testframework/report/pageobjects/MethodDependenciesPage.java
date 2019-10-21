package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by fakr on 11.10.2017
 */
public class MethodDependenciesPage extends MethodDetailsPage {

    private IGuiElement details = new GuiElement(driver, By.id("details"), mainFrame);
    private IGuiElement clickPath = new GuiElement(driver, By.id("clickpath"), mainFrame);
    private IGuiElement videoArea = new GuiElement(driver, By.id("videoarea"), mainFrame);

    @Check
    private IGuiElement dependenciesArea = new GuiElement(driver, By.id("depsarea"), mainFrame);

    public MethodDependenciesPage(WebDriver driver) {
        super(driver);
    }
}
