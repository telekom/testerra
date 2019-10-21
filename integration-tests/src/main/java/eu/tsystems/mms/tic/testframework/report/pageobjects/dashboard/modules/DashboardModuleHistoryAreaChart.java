package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.LinkedList;
import java.util.List;

public class DashboardModuleHistoryAreaChart extends AbstractFramePage {

    //TODO identifier for history chart needed, also for Annotation @Check

    //history chart elements
    public final IGuiElement historyHoverElementPassed = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[4]"), mainFrame);
    public final IGuiElement historyHoverElementPassedInherited = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[7]"), mainFrame);
    public final IGuiElement historyHoverElementPassedMinor = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[10]"), mainFrame);
    public final IGuiElement historyHoverElementFailed = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[13]"), mainFrame);
    public final IGuiElement historyHoverElementFailedInherited = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[16]"), mainFrame);
    public final IGuiElement historyHoverElementFailedMinor = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[19]"), mainFrame);
    public final IGuiElement historyHoverElementSkipped = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[22]"), mainFrame);
    public final IGuiElement historyHoverElementSkippedInherited = new GuiElement(this.driver, By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[25]"), mainFrame);
    private IGuiElement historyChartTopLine = new GuiElement(this.driver, By.xpath("//*[@class='highcharts-series-group']//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']"), mainFrame);

    public DashboardModuleHistoryAreaChart(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns a list with all the currently displayed segments of the history chart.
     *
     * @return list of GuiElements displaying segments of the history chart
     */
    public List<IGuiElement> getHistoryChartSegments() {
        List<IGuiElement> segments = new LinkedList<>();
        int numberOfSegments = historyChartTopLine.getSubElement(By.xpath("./*")).getNumberOfFoundElements();
        for (int i = 1; i <= numberOfSegments; i++) {
            segments.add(historyChartTopLine.getSubElement(By.xpath("./*[" + i + "]")));
        }
        return segments;
    }

    /**
     * Triggers a hover action for a specific segment of the history chart.
     *
     * @param segment the segment of the history chart
     * @return a refreshed DashboardPage Object
     */
    public DashboardPage hoverHistorySegment(IGuiElement segment) {
        //TODO why twice?
        segment.mouseOver();
        segment.mouseOver();
        return PageFactory.create(DashboardPage.class, this.driver);
    }
}
