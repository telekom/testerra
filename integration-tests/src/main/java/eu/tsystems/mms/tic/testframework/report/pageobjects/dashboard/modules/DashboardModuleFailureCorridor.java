package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleFailureCorridor extends AbstractFramePage {

    @Check
    public final GuiElement failureCorridor = new GuiElement(this.getWebDriver(), By.xpath("//div[@id='FailureCorridor']"), mainFrame);

    //failure corridor elements
    public final GuiElement failureCorridorDescription = failureCorridor.getSubElement(By.xpath("./*[1]"));
    public final GuiElement highCorridorLimitButton = failureCorridor.getSubElement(By.id("failureCorridorHighTotalSum"));
    public final GuiElement midCorridorLimitButton = failureCorridor.getSubElement(By.id("failureCorridorMidTotalSum"));
    public final GuiElement lowCorridorLimitButton = failureCorridor.getSubElement(By.id("failureCorridorLowTotalSum"));
    public final GuiElement highCorridorActualButton = failureCorridor.getSubElement(By.id("failedHighLevelTests"));
    public final GuiElement midCorridorActualButton = failureCorridor.getSubElement(By.id("failedMidLevelTests"));
    public final GuiElement lowCorridorActualButton = failureCorridor.getSubElement(By.id("failedLowLevelTests"));

    public DashboardModuleFailureCorridor(WebDriver driver) {
        super(driver);
    }
}
