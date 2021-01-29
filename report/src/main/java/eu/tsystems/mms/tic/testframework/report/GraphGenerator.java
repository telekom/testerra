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
 */
package eu.tsystems.mms.tic.testframework.report;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public final class GraphGenerator {

    public enum DataType {
        DATE,
        NUMBER
    }

    private GraphGenerator() {

    }

    /**
     * creates a LineChart
     *
     * @param xyDataset
     * @param title
     * @param xLabel
     * @param yLabel
     * @return
     * @throws IOException
     */
    public static JFreeChart createLineChart(XYDataset xyDataset, String title, String xLabel,
                                             String yLabel, DataType dataTypeXAxis) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                xyDataset,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.white);

        /*
         * canvas configuration
         */
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        /*
         * renderer configuration
         */
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        renderer.setDrawSeriesLineAsPath(true);
        plot.setRenderer(renderer);

        /*
         * axis configuration
         */
        NumberAxis yAxis = new NumberAxis(yLabel);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.setRangeAxis(yAxis);

        switch (dataTypeXAxis) {
            case DATE: {
                final DateAxis xAxis = new DateAxis(xLabel);
                xAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
                plot.setDomainAxis(xAxis);
            }
            break;
            case NUMBER: {
                final NumberAxis xAxis = new NumberAxis(xLabel);
                xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                xAxis.setAutoRangeIncludesZero(false);
                xAxis.setAutoRange(true);
                plot.setDomainAxis(xAxis);
            }
            break;
        }

        return chart;
    }

    /**
     * save a given Chart as JPEG in the folder "Graphs_Of_Perf_Test"
     *
     * @param chart
     * @throws IOException
     */
    public static File saveGraphAsJPEG(JFreeChart chart, String relativeFileName, int width, int height) throws IOException {
        File graphFile = TesterraListener.getReport().getReportDirectory(relativeFileName);
        File dir = graphFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ChartUtilities.saveChartAsJPEG(graphFile, chart, width, height);
        return graphFile;
    }

}
