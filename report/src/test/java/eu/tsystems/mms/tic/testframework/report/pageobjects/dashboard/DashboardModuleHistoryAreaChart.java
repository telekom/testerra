/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractFramePage;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleHistoryAreaChart extends AbstractFramePage {

    //TODO identifier for history chart needed, also for Annotation @Check

    //history chart elements
    public final GuiElement historyHoverElementPassed = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[4]"));
    public final GuiElement historyHoverElementPassedInherited = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[7]"));
    public final GuiElement historyHoverElementPassedMinor = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[10]"));
    public final GuiElement historyHoverElementFailed = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[13]"));
    public final GuiElement historyHoverElementFailedInherited = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[16]"));
    public final GuiElement historyHoverElementFailedMinor = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[19]"));
    public final GuiElement historyHoverElementSkipped = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[22]"));
    public final GuiElement historyHoverElementSkippedInherited = mainFrame.getSubElement(By.xpath("(//*[@class='highcharts-tooltip'])[2]/*[5]/*[25]"));
    private GuiElement historyChartTopLine = mainFrame.getSubElement(By.xpath("//*[@class='highcharts-series-group']//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']"));

    public DashboardModuleHistoryAreaChart(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns a list with all the currently displayed segments of the history chart.
     *
     * @return list of GuiElements displaying segments of the history chart
     */
    public List<GuiElement> getHistoryChartSegments() {
        List<GuiElement> segments = new LinkedList<>();
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
    public DashboardPage hoverHistorySegment(GuiElement segment) {
        //TODO why twice?
        segment.mouseOver();
        segment.mouseOver();
        return PageFactory.create(DashboardPage.class, this.getWebDriver());
    }
}
