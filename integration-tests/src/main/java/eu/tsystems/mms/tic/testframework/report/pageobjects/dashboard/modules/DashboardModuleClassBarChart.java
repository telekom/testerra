package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardModuleClassBarChart extends AbstractFramePage {

    @Check
    private IGuiElement barChart = new GuiElement(this.driver, By.id("classesBars"), mainFrame);

    //bar chart elements
    public final IGuiElement barChartBars = new GuiElement(this.driver, By.xpath("//*[@class='highcharts-series-group']"), mainFrame);
    public final IGuiElement barChartClassNames = new GuiElement(this.driver, By.className("highcharts-xaxis-labels"), mainFrame);

    public DashboardModuleClassBarChart(WebDriver driver) {
        super(driver);
    }

    /**
     * This method returns a list that contains all the separate bars that are currently displayed
     *
     * @return List of separate bars currently displayed
     */
    public List<IGuiElement> getCurrentBars() {
        List<IGuiElement> bars = new LinkedList<>();
        barChartBars.waits().waitForIsDisplayed();
        IGuiElement displayedBars = barChartBars.getSubElement(By.xpath(".//*[contains(@class,'highcharts-tracker')]/*[@height!='0']"));
        displayedBars.setName("displayedBars");
        int barCount = displayedBars.getList().size();
        for (int i = 0; i < barCount; i++) {
            IGuiElement currentBar = displayedBars.getList().get(i);
            currentBar.setName("currentBarForPosition" + i);
            bars.add(currentBar);
        }
        return bars;
    }

    /**
     * Returns a bar chart legend IGuiElement by its position.
     *
     * @param elementNumber the position of the bar chart legend IGuiElement
     * @return the bar chart legend IGuiElement
     */
    public IGuiElement getBarChartLegendElementByPosition(int elementNumber) {
        int domPosition = elementNumber + 1;
        IGuiElement barChartLegendElement = barChartClassNames.getSubElement(By.xpath(".//*[name()='text'][" + domPosition + "]"));
        barChartLegendElement.setName("barChartLegendElement");
        return barChartLegendElement;
    }

    /**
     * Returns the Number of a bar chart IGuiElement by its class name.
     *
     * @param barChartClassName the class name of the bar chart IGuiElement
     * @return the index of the bar chart IGuiElement
     */
    public int getBarChartElementNumberByClassName(String barChartClassName) {
        List<String> barChartLegendNames = barChartClassNames.getTextsFromChildren();
        barChartLegendNames = barChartLegendNames.stream().distinct().collect(Collectors.toList());
        return barChartLegendNames.indexOf(barChartClassName);
    }

    /**
     * Returns the bar chart IGuiElement by its class name.
     *
     * @param barChartClassName the class name of the bar chart IGuiElement
     * @return the bar chart IGuiElement
     */
    public IGuiElement getBarChartElementByClassName(String barChartClassName) {
        return getBarChartLegendElementByPosition(getBarChartElementNumberByClassName(barChartClassName));
    }
}
