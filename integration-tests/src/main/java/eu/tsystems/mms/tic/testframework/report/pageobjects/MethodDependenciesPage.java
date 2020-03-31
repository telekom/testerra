package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodDependenciesPage extends MethodDetailsPage {

    private GuiElement details = new GuiElement(driver, By.id("details"), mainFrame);
    private GuiElement clickPath = new GuiElement(driver, By.id("clickpath"), mainFrame);
    private GuiElement videoArea = new GuiElement(driver, By.id("videoarea"), mainFrame);

    @Check
    private GuiElement dependenciesArea = new GuiElement(driver, By.id("depsarea"), mainFrame);

    public MethodDependenciesPage(WebDriver driver) {
        super(driver);
    }
}
