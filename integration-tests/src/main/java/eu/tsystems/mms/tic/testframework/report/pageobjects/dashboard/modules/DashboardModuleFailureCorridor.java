package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleFailureCorridor extends AbstractFramePage {

    @Check
    public final IGuiElement failureCorridor = new GuiElement(this.driver, By.xpath("//div[@id='FailureCorridor']"), mainFrame);

    //failure corridor elements
    public final IGuiElement failureCorridorDescription = failureCorridor.getSubElement(By.xpath("./*[1]"));
    public final IGuiElement highCorridorLimitButton = failureCorridor.getSubElement(By.id("failureCorridorHighTotalSum"));
    public final IGuiElement midCorridorLimitButton = failureCorridor.getSubElement(By.id("failureCorridorMidTotalSum"));
    public final IGuiElement lowCorridorLimitButton = failureCorridor.getSubElement(By.id("failureCorridorLowTotalSum"));
    public final IGuiElement highCorridorActualButton = failureCorridor.getSubElement(By.id("failedHighLevelTests"));
    public final IGuiElement midCorridorActualButton = failureCorridor.getSubElement(By.id("failedMidLevelTests"));
    public final IGuiElement lowCorridorActualButton = failureCorridor.getSubElement(By.id("failedLowLevelTests"));

    public DashboardModuleFailureCorridor(WebDriver driver) {
        super(driver);
    }
}
