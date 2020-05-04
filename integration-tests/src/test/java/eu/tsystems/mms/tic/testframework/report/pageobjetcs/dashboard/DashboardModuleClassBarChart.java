package eu.tsystems.mms.tic.testframework.report.pageobjetcs.dashboard;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractFramePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardModuleClassBarChart extends AbstractFramePage {

    @Check
    private GuiElement barChart = new GuiElement(this.getWebDriver(), By.id("classesBars"), mainFrame);

    //bar chart elements
    public final GuiElement barChartBars = new GuiElement(this.getWebDriver(), By.xpath("//*[@class='highcharts-series-group']"), mainFrame);
    public final GuiElement barChartClassNames = new GuiElement(this.getWebDriver(), By.className("highcharts-xaxis-labels"), mainFrame);

    public DashboardModuleClassBarChart(WebDriver driver) {
        super(driver);
    }

    /**
     * This method returns a list that contains all the separate bars that are currently displayed
     *
     * @return List of separate bars currently displayed
     */
    public List<GuiElement> getCurrentBars() {
        List<GuiElement> bars = new LinkedList<>();
        barChartBars.waits().waitForIsDisplayed();
        GuiElement displayedBars = barChartBars.getSubElement(By.xpath(".//*[contains(@class,'highcharts-tracker')]/*[@height!='0']"));
        displayedBars.setName("displayedBars");
        int barCount = displayedBars.getList().size();
        for (int i = 0; i < barCount; i++) {
            GuiElement currentBar = displayedBars.getList().get(i);
            currentBar.setName("currentBarForPosition" + i);
            bars.add(currentBar);
        }
        return bars;
    }

    /**
     * Returns a bar chart legend GuiElement by its position.
     *
     * @param elementNumber the position of the bar chart legend GuiElement
     * @return the bar chart legend GuiElement
     */
    public GuiElement getBarChartLegendElementByPosition(int elementNumber) {
        int domPosition = elementNumber + 1;
        GuiElement barChartLegendElement = barChartClassNames.getSubElement(By.xpath(".//*[name()='text'][" + domPosition + "]"));
        barChartLegendElement.setName("barChartLegendElement");
        return barChartLegendElement;
    }

    /**
     * Returns the Number of a bar chart GuiElement by its class name.
     *
     * @param barChartClassName the class name of the bar chart GuiElement
     * @return the index of the bar chart GuiElement
     */
    public int getBarChartElementNumberByClassName(String barChartClassName) {
        List<String> barChartLegendNames = barChartClassNames.getTextsFromChildren();
        barChartLegendNames = barChartLegendNames.stream().distinct().collect(Collectors.toList());
        return barChartLegendNames.indexOf(barChartClassName);
    }

    /**
     * Returns the bar chart GuiElement by its class name.
     *
     * @param barChartClassName the class name of the bar chart GuiElement
     * @return the bar chart GuiElement
     */
    public GuiElement getBarChartElementByClassName(String barChartClassName) {
        return getBarChartLegendElementByPosition(getBarChartElementNumberByClassName(barChartClassName));
    }
}
