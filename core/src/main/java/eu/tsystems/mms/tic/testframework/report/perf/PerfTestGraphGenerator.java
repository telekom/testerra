/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.report.perf;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.GraphGenerator;
import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

/**
 * Created by clgr on 09.10.2014.
 */
public class PerfTestGraphGenerator {

    private final int widthOfImage = 800;

    private int widthOfRepetitionCharts = 800;

    private int numberOfTestExecutions = 1;

    private final int heightOfImage = 600;

    private final String subFolderForGraphs = "Graphs_Of_PerfTest";

    private final Logger logger = LoggerFactory.getLogger(PerfTestGraphGenerator.class);

    // default values for thy y-Axis value-rate for BarCharts based on test steps
    private long tickRateForStackedBarChart = 200;
    private long tickRateForMaxBarChart = 200;
    private long tickRateForMinBarChart = 200;
    private long tickRateForAvgBarChart = 200;

    // default values for thy y-Axis value-rate for BarCharts based on test executions
    private long tickRateForAvgRepetitionBarChart = 200;
    private long tickRateForMaxRepetitionBarChart = 200;
    private long tickRateForMinRepetitionBarChart = 200;
    private long tickRateForLayeredBarChart = 200;

    // default font sizes for Labels of BarCharts based on test steps
    private int fontSizeForStackeResponseTimeBarChart = 18;
    private int fontSizeForMinResponseTimeBarChart = 18;
    private int fontSizeForMaxResponseTimeBarChart = 18;
    private int fontSizeForAvgResponseTimeBarChart = 18;

    // default font sizes for Labels of BarCharts based on test executions
    private int fontSizeForLayeredRepetitionBarChart = 12;
    private int fontSizeForAvgRepetitionBarCharts = 12;
    private int fontSizeForMinRepetitionBarCharts = 12;
    private int fontSizeForMaxRepetitionBarCharts = 12;


//    /**
//     * generates graphs for all testmethods
//     *
//     * @param allPageLoadInfos
//     * @throws IOException
//     */
//    public void generateAllGraphs(Map<String, Map<Long, List<TimingInfo>>> allPageLoadInfos) throws IOException {
//        ITestNGMethod[] executedTestmethods = PerfTestContainer.getTestContext().getAllTestMethods();
//
//        // iterate Testmethods and generate graphs for each method and save in a folder named after this method
//        for (ITestNGMethod testmethod : executedTestmethods) {
//            String methodName = testmethod.getMethodName();
//            numberOfTestExecutions = testmethod.getInvocationCount();
//            // get the pageLoadInfoMap for a specific testMethod and sort it
//            Map<Long, List<TimingInfo>> pageLoadInfosPerTestMethod = allPageLoadInfos.get(methodName);
//
//            if(pageLoadInfosPerTestMethod != null) {
//                Map<Long, List<TimingInfo>> sortedPageLoadInfos = PerfTestReportUtils.sortMap(pageLoadInfosPerTestMethod);
//                // make a subdirectory "Graphs_Of_Perf_Test" in the folder with the test method name
//                makeSubDirectoryForTestMethod(methodName);
//                // generate metrics
//                generateMetrics(sortedPageLoadInfos);
//                // initialize default font sizes which show teststeps
//                adjustFontSizesToNumberOfTestSteps();
//                // initialize default font sizes and image width for graphs which use repetitions
//                adjustFontSizesAndImageWidthToNumberOfRepetitions();
//                // create graphs with the pre-generated metrics
//                createGraphsForLoadTestReport(methodName);
//            } else {
//                logger.info("No Measurements for: " + methodName);
//            }
//        }
//    }

    /**
     * initiates the creation of different Chart and corresponding Datasets
     */
    private void createGraphsForLoadTestReport(String methodName) throws IOException {

        // Create:
        // DataSets for BarCharts
        DefaultCategoryDataset defaultCategoryDataset = createDataSetForBarCharts("Responsetimes",
                MetricType.STACKED_RESPONSETIMES_PER_TESTSTEP);
        DefaultCategoryDataset layeredDataSet = createDataSetForBarCharts("LayeredChart",
                MetricType.LAYERED_RESPONSETIMES_PER_REPETITION);
        DefaultCategoryDataset avgPerRepetitionDataSet = createDataSetForBarCharts("Responsetimes_avg",
                MetricType.AVERAGE_RESPONSETIMES_PER_REPETITION);
        DefaultCategoryDataset minPerRepetitionDataSet = createDataSetForBarCharts("Responsetimes_min",
                MetricType.MIN_RESPONSETIME_PER_REPETITION);
        DefaultCategoryDataset maxPerRepetitionDataSet = createDataSetForBarCharts("Responsetimes_max",
                MetricType.MAX_RESPONSETIME_PER_REPETITION);
        DefaultCategoryDataset dataSet1 = createDataSetForBarCharts("Responsetimes",
                MetricType.AVERAGE_RESPONSETIMES_PER_TESTSTEP);
        DefaultCategoryDataset dataSet2 = createDataSetForBarCharts("Responsetimes",
                MetricType.MAX_RESPONSETIME_PER_TESTSTEP);
        DefaultCategoryDataset dataSetMinTimes = createDataSetForBarCharts("Responsetimes_min",
                MetricType.MIN_RESPONSETIME_PER_TESTSTEP);
        DefaultCategoryDataset dataSet4 = createDataSetForBarCharts("Transactionstatus",
                MetricType.TRANSACTION_STATUS);

        //Datasets for Line Charts
        XYDataset dataSet5 = createDataSetForLineChart("Responsetimehistory",
                MetricType.RESPONSETIMES_PER_TEST);
        XYDataset dataSetTransActionsPerSecond = createDataSetForLineChart(methodName,
                MetricType.TRANSACTIONS_PER_SECOND);
        XYDataset dataSetTransActionsPerMinute = createDataSetForLineChart(methodName,
                MetricType.TRANSACTIONS_PER_MINUTE);
        XYDataset dataSetTransActionsPerHour = createDataSetForLineChart(methodName,
                MetricType.TRANSACTIONS_PER_HOUR);
        XYDataset dataSetRequestsPerSecond = createDataSetForLineChart(methodName,
                MetricType.REQUESTS_PER_SECOND);
        XYDataset dataSetRequestsPerMinute = createDataSetForLineChart(methodName,
                MetricType.REQUESTS_PER_MINUTE);
        XYDataset dataSetRequestsPerHour = createDataSetForLineChart(methodName,
                MetricType.REQUESTS_PER_HOUR);
        XYDataset dataSetRequestsPerTransaction = createDataSetForLineChart(methodName,
                MetricType.RESPONSETIMES_PER_TESTSTEP);

        //Create:
        // Bar Charts
        JFreeChart barChart1 = createBarChart(dataSet1, "Average ResponseTimes per Teststep",
                "Teststep", "Response Time [ms]", methodName, "Responsetimes_Per_Teststep_Avg", MetricType.AVERAGE_RESPONSETIMES_PER_TESTSTEP);
        JFreeChart barChart2 = createBarChart(dataSet2, "Max ResponseTimes per Teststep",
                "Teststep", "Response Time [ms]", methodName, "Responsetimes_Per_Teststep_Max", MetricType.MAX_RESPONSETIME_PER_TESTSTEP);
        JFreeChart barChart3 = createBarChart(dataSet4, "Status of Transactions",
                "Transactionstatus", "Amount", methodName, "Number_Of_Transactionstatus_Bar", MetricType.TRANSACTION_STATUS);
        JFreeChart barChart4 = createBarChart(dataSetMinTimes, "Min ResponseTimes per Teststep",
                "Teststep", "Response Time [ms]", methodName, "Responsetimes_Per_Teststep_Min", MetricType.MIN_RESPONSETIME_PER_TESTSTEP);

        JFreeChart stackBarChart = createStackedBarChart(defaultCategoryDataset, "Responsetime_Overview",
                "Teststeps", "Response Time [ms]", methodName, "Responsetimes_Per_Teststep_All", MetricType.STACKED_RESPONSETIMES_PER_TESTSTEP);
        JFreeChart layeredBarChart = createLayeredBarChart(layeredDataSet, "All Responsetimes per Repetition",
                "Repetition", "Response Time [ms]", methodName, "Responsetimes_Per_Repetition_All", MetricType.LAYERED_RESPONSETIMES_PER_REPETITION);

        JFreeChart avgPerRepetitionBarChart = createBarChart(avgPerRepetitionDataSet, " Average Responsetimes Per Repetition",
                "Repetition", "Response Time [ms]", methodName, "Responsetimes_Per_Repetition_Avg", MetricType.AVERAGE_RESPONSETIMES_PER_REPETITION);
        JFreeChart maxPerRepetitionBarChart = createBarChart(maxPerRepetitionDataSet, "Max Responsetimes Per Repetition",
                "Repetition", "Response Time [ms]", methodName, "Responsetimes_Per_Repetition_Max", MetricType.MAX_RESPONSETIME_PER_REPETITION);
        JFreeChart minPerRepetitionBarChart = createBarChart(minPerRepetitionDataSet, " Min Responsetimes Per Repetition",
                "Repetition", "Response Time [ms]", methodName, "Responsetimes_Per_Repetition_Min", MetricType.MIN_RESPONSETIME_PER_REPETITION);

        // Line Charts
        JFreeChart lineChart = createLineChart(dataSet5, "Responsetime History",
                "Time", "Responsetime", methodName, "ResponseTime_History", MetricType.RESPONSETIMES_PER_TEST);
        JFreeChart lineChartTransactionsPerSecond = createLineChart(dataSetTransActionsPerSecond,
                "Transaction Throughput Per Second", "Date", "Transactions/s", methodName, "Transaction_Throughput_Per_Second", MetricType.TRANSACTIONS_PER_SECOND);
        JFreeChart lineChartTransactionsPerMinute = createLineChart(dataSetTransActionsPerMinute,
                "Transaction Throughput Per Minute", "Date", "Transactions/min", methodName, "Transaction_Throughput_Per_Minute", MetricType.TRANSACTIONS_PER_MINUTE);
        JFreeChart lineChartTransactionsPerHour = createLineChart(dataSetTransActionsPerHour,
                "Transaction Throughput Per Hour", "Date", "Transactions/h", methodName, "Transaction_Throughput_Per_Hour", MetricType.TRANSACTIONS_PER_HOUR);
        JFreeChart lineChartRequestPerSecond = createLineChart(dataSetRequestsPerSecond,
                "Request Throughput Per Second", "Date", "Requests/s", methodName, "Request_Throughput_Per_Second", MetricType.REQUESTS_PER_SECOND);
        JFreeChart lineChartRequestPerMinute = createLineChart(dataSetRequestsPerMinute,
                "Request Throughput Per Minute", "Date", "Requests/min", methodName, "Request_Throughput_Per_Minute", MetricType.REQUESTS_PER_MINUTE);
        JFreeChart lineChartRequestPerHour = createLineChart(dataSetRequestsPerHour,
                "Request Throughput Per Hour", "Date", "Requests/h", methodName, "Request_Throughput_Per_Hour", MetricType.REQUESTS_PER_HOUR);
        JFreeChart lineChartResponseTimePerTeststep = createLineChart(dataSetRequestsPerTransaction,
                "Response Time Per Step", "Date", "Response Time [ms]", methodName,
                "Responsetime_History_Per_Teststep", MetricType.RESPONSETIMES_PER_TESTSTEP);
    }

    //================= create Charts =================

    /**
     * generates a BarChart for the average responsetime of every VU
     *
     * @param dataSet
     * @param title
     * @param xLabel
     * @param yLabel
     * @param saveName
     * @param metricType
     * @return barChart
     */
    private JFreeChart createBarChart(DefaultCategoryDataset dataSet,
                                      String title, String xLabel, String yLabel,
                                      String methodName, String saveName, MetricType metricType) throws IOException {

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        JFreeChart barChart = ChartFactory.createBarChart3D(
                title,
                xLabel,
                yLabel,
                dataSet,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        barChart.setBackgroundPaint(Color.white);

        /*
         *canvas configuration
         */
        final CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.blue);
        plot.setRangeGridlinePaint(Color.blue);

        /*
         * renderer configuration
         */
        BarRenderer3D renderer = new CustomRenderer();
        renderer.setShadowVisible(false);
        DecimalFormat format = new DecimalFormat("#####.#####");
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", format));
        renderer.setDrawBarOutline(false);

        // position of labels
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER,
                TextAnchor.CENTER));
        // visibility of labels
        renderer.setBaseItemLabelsVisible(true);
        // font colour of labels
        renderer.setBaseItemLabelPaint(Color.black);

        /*
         * axis configuration
         */
        final NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        CategoryAxis xAxis = plot.getDomainAxis();
        // margin to yAxis (left endTime of Chart)
        xAxis.setLowerMargin(0.01);
        // margin to right endTime of Chart
        xAxis.setUpperMargin(0.01);

        /*
         * customize legend in case of multiple data columns
         */
        if (dataSet.getColumnCount() > 0) {
            Comparable comp = dataSet.getColumnKey(0);
            Class classOfComparable = comp.getClass();

            /**
             * create Legend
             * transaction Status: 3 different colors (red, yellow, green), else: one color (cyan)
             */
            if (classOfComparable == String.class) {
                String columnKey = (String) dataSet.getColumnKey(0);
                LegendItemCollection legendItemCollection = renderer.getLegendItems();
                // for Status of Tests change colour according to the Status e.g. Success=Green,...
                if (columnKey.equals(PerfTestReportUtils.TEXT_TEST_SUCCESS) ||
                        columnKey.equals(PerfTestReportUtils.TEXT_TEST_FAIL) ||
                        columnKey.equals(PerfTestReportUtils.TEXT_TEST_SKIP)) {
                    legendItemCollection.add(new LegendItem(
                            new AttributedString(PerfTestReportUtils.TEXT_TEST_SUCCESS),
                            "description 0", "tooltip 0", "url 0", new Rectangle(10, 10), Color.green));
                    legendItemCollection.add(new LegendItem(
                            new AttributedString(PerfTestReportUtils.TEXT_TEST_FAIL),
                            "description 1", "tooltip 1", "url 1", new Rectangle(10, 10), Color.red));
                    legendItemCollection.add(new LegendItem(
                            new AttributedString(PerfTestReportUtils.TEXT_TEST_SKIP),
                            "description 2", "tooltip 2", "url 2", new Rectangle(10, 10), Color.yellow));
                    plot.setFixedLegendItems(legendItemCollection);

                } else {
                    renderer.setSeriesPaint(0, Color.cyan);
                }
            } else {
                renderer.setSeriesPaint(0, Color.cyan);
            }
        }

        // adjustment of tickrate and fontsize
        setFontSizeAndTickRateOfBarCharts(metricType, renderer, yAxis);

        // use the configs for the chart
        plot.setRenderer(renderer);

        saveGraphAsJPEG(barChart, saveName, methodName, metricType);
        return barChart;
    }

    /**
     * creates a stackedBarChart for min, avg, max response times per test step
     *
     * @param categoryDataset
     * @param title
     * @param xLabel
     * @param yLabel
     * @param methodName
     * @param saveName
     * @param metricType
     * @return
     * @throws IOException
     */
    private JFreeChart createStackedBarChart(CategoryDataset categoryDataset,
                                             String title, String xLabel, String yLabel,
                                             String methodName, String saveName, MetricType metricType) throws IOException {
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        final JFreeChart chart = ChartFactory.createStackedBarChart3D(
                title,                      // chart title
                xLabel,                     // domain axis label
                yLabel,                     // range axis label
                categoryDataset,            // data
                PlotOrientation.VERTICAL,   // the plot orientation
                true,                       // legend
                true,                       // tooltips
                false                       // urls
        );
        chart.setBackgroundPaint(Color.white);

        /*
         * canvas configuration
         */
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.blue);
        plot.setRangeGridlinePaint(Color.blue);

        /*
         * renderer configuration
         */
        StackedBarRenderer3D renderer = new StackedBarRenderer3D();
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        // position of labels
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        // visibility of labels
        renderer.setBaseItemLabelsVisible(true);
        // font colour of labels
        renderer.setBaseItemLabelPaint(Color.black);

        // contains all used default values of Colors used in JFreeCharts
        Paint[] array = ChartColor.createDefaultPaintArray();
        // set colours of bars
        renderer.setSeriesPaint(0, array[17]); // green
        renderer.setSeriesPaint(1, array[20]); // cyan
        renderer.setSeriesPaint(2, array[15]); // red

        /*
         * axis configuration
         */
        final NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        CategoryAxis xAxis = plot.getDomainAxis();
        // margin to yAxis (left endTime of Chart)
        xAxis.setLowerMargin(0.01);
        // margin to right endTime of Chart
        xAxis.setUpperMargin(0.01);

        // adjustment of tickrate and fontsize
        setFontSizeAndTickRateOfBarCharts(metricType, renderer, yAxis);
        // use the configs for the chart
        plot.setRenderer(renderer);

        saveGraphAsJPEG(chart, saveName, methodName, metricType);
        return chart;
    }

    /**
     * creates a stackedBarChart for min, avg, max response times
     *
     * @param categoryDataset
     * @param title
     * @param xLabel
     * @param yLabel
     * @param methodName
     * @param saveName
     * @param metricType
     * @return
     * @throws IOException
     */
    private JFreeChart createLayeredBarChart(CategoryDataset categoryDataset,
                                             String title, String xLabel, String yLabel,
                                             String methodName, String saveName, MetricType metricType) throws IOException {
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        final JFreeChart chart = ChartFactory.createBarChart(
                title,                      // chart title
                xLabel,                     // domain axis label
                yLabel,                     // range axis label
                categoryDataset,            // data
                PlotOrientation.VERTICAL,   // the plot orientation
                true,                       // legend
                true,                       // tooltips
                false                       // urls
        );
        chart.setBackgroundPaint(Color.white);

        /*
         * canvas configuration
         */
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.blue);
        plot.setRangeGridlinePaint(Color.blue);

        /*
         * renderer configuration
         */
        LayeredBarRenderer renderer = new LayeredBarChartRenderer();
        StandardCategoryItemLabelGenerator categoryLabel = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("###.##"));
        renderer.setBaseItemLabelGenerator(categoryLabel);
        // position of labels: Negative has to be used here, although we don't have any negative values
        renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER));
        // visibility of labels
        renderer.setBaseItemLabelsVisible(true);
        // font colour of labels
        renderer.setBaseItemLabelPaint(Color.black);

        // contains all used default values of Colors used in JFreeCharts
        Paint[] array = ChartColor.createDefaultPaintArray();
        // set colours of bars
        renderer.setSeriesPaint(2, array[17]); // green
        renderer.setSeriesPaint(1, array[20]); // cyan
        renderer.setSeriesPaint(0, array[15]); // red
        // set all bars to the same width
        renderer.setSeriesBarWidth(0, 1.0);
        renderer.setSeriesBarWidth(1, 1.0);
        renderer.setSeriesBarWidth(2, 1.0);

        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        /*
         * axis configuration
         */
        final NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        CategoryAxis xAxis = plot.getDomainAxis();
        // margin to yAxis (left endTime of Chart)
        xAxis.setLowerMargin(0.01);
        // margin to right endTime of Chart
        xAxis.setUpperMargin(0.01);

        // adjustment of tickrate and fontsize
        setFontSizeAndTickRateOfBarCharts(metricType, renderer, yAxis);

        // use the configs for the chart
        plot.setRenderer(renderer);

        saveGraphAsJPEG(chart, saveName, methodName, metricType);
        return chart;
    }

    /**
     * creates a lineChart for requests/transactions per sec, min, hour and a response time history for the whole performance test
     *
     * @param xyDataset
     * @param title
     * @param xLabel
     * @param yLabel
     * @param methodName
     * @param saveName
     * @param metricType
     * @return
     * @throws IOException
     */
    private JFreeChart createLineChart(XYDataset xyDataset, String title, String xLabel,
                                       String yLabel, String methodName, String saveName, MetricType metricType) throws IOException {

        JFreeChart chart = GraphGenerator.createLineChart(xyDataset, title, xLabel, yLabel, GraphGenerator.DataType.NUMBER);
        saveGraphAsJPEG(chart, saveName, methodName, metricType);
        return chart;
    }

    //================= create Data Sets =================

    /**
     * create DataSet for LineCharts
     *
     * @param title
     * @param metricType
     * @return
     */
    private XYDataset createDataSetForLineChart(String title, MetricType metricType) {
        TimeSeriesCollection dataSet = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries(title);

        String nameOfMetricType = metricType.name();
        // indicates an empty Dataset for RESPONSETIMES_PER_TESTSTEP (all possible series are empty)
        boolean dataSetIsEmpty = true;
        switch (metricType) {
            //Responsetimes per Test (all executions)
            case RESPONSETIMES_PER_TEST:
                Map<Integer, List<TimingInfo>> allResponseTimes = PerfTestReportUtils.getAllResponseTimesPerTestStep();
                Set<Integer> keys = allResponseTimes.keySet();
                for (Integer key : keys) {
                    //TimeSeries series = new TimeSeries("Teststep " + key);
                    List<TimingInfo> timingInfos = allResponseTimes.get(key);
                    for (TimingInfo timingInfo : timingInfos) {
                        long timeStamp = timingInfo.getTimeStamp();
                        Date dateOfMeasurement = new Date(timeStamp);
                        long loadDuration = timingInfo.getLoadDuration();
                        series.addOrUpdate(new Millisecond(dateOfMeasurement), loadDuration);
                    }
                }
                dataSet.addSeries(series);
                break;
            case RESPONSETIMES_PER_TESTSTEP:
                Map<Integer, List<TimingInfo>> allResponseTimes1 = PerfTestReportUtils.getAllResponseTimesPerTestStep();
                Set<Integer> keys1 = allResponseTimes1.keySet();
                for (Integer key : keys1) {
                    series = new TimeSeries("Teststep " + key);
                    List<TimingInfo> timingInfos = allResponseTimes1.get(key);
                    for (TimingInfo timingInfo : timingInfos) {
                        long timeStamp = timingInfo.getTimeStamp();
                        Date dateOfMeasurement = new Date(timeStamp);
                        long loadDuration = timingInfo.getLoadDuration();
                        series.addOrUpdate(new Millisecond(dateOfMeasurement), loadDuration);
                    }
                    dataSet.addSeries(series);
                    // after each series is added check if the series was empty
                    if (series.getItemCount() == 0) {
                        dataSetIsEmpty = true;
                    } else {
                        dataSetIsEmpty = false;
                    }
                }
                break;
            case REQUESTS_PER_SECOND:
                Map<Long, Integer> requestsPerSecond2 = PerfTestReportUtils.getRequestsPerSecond();
                Set<Long> datesOfRequestsPerSecond2 = requestsPerSecond2.keySet();
                for (Long timeStamp : datesOfRequestsPerSecond2) {
                    int amount = requestsPerSecond2.get(timeStamp);
                    series.add(new Second(new Date(timeStamp)), amount);
                }
                dataSet.addSeries(series);
                break;
            case REQUESTS_PER_MINUTE:
                Map<Long, Integer> requestsPerMinute2 = PerfTestReportUtils.getRequestsPerMinute();
                Set<Long> datesOfRequestsPerMinute2 = requestsPerMinute2.keySet();
                for (Long timeStamp : datesOfRequestsPerMinute2) {
                    int amount = requestsPerMinute2.get(timeStamp);
                    series.add(new Minute(new Date(timeStamp)), amount);
                }
                dataSet.addSeries(series);
                break;
            case REQUESTS_PER_HOUR:
                Map<Long, Integer> requestsPerHour2 = PerfTestReportUtils.getRequestsPerHour();
                Set<Long> datesOfRequestsPerHour2 = requestsPerHour2.keySet();
                for (Long timeStamp : datesOfRequestsPerHour2) {
                    int amount = requestsPerHour2.get(timeStamp);
                    series.add(new Hour(new Date(timeStamp)), amount);
                }
                dataSet.addSeries(series);
                break;
            case TRANSACTIONS_PER_SECOND:
                Map<Long, Integer> transactionsPerSecond2 = PerfTestReportUtils.getTransactionsPerSecond();
                Set<Long> datesOfTransactionsPerSecond2 = transactionsPerSecond2.keySet();
                for (Long timeStamp : datesOfTransactionsPerSecond2) {
                    int amount = transactionsPerSecond2.get(timeStamp);
                    series.add(new Second(new Date(timeStamp)), amount);
                }
                dataSet.addSeries(series);
                break;
            case TRANSACTIONS_PER_HOUR:
                Map<Long, Integer> transactionsPerHour2 = PerfTestReportUtils.getTransactionsPerHour();
                Set<Long> datesOfTransactionsPerHour2 = transactionsPerHour2.keySet();
                for (Long timeStamp : datesOfTransactionsPerHour2) {
                    int amount = transactionsPerHour2.get(timeStamp);
                    series.add(new Hour(new Date(timeStamp)), amount);
                }
                dataSet.addSeries(series);
                break;
            case TRANSACTIONS_PER_MINUTE:
                Map<Long, Integer> transactionsPerMinute2 = PerfTestReportUtils.getTransactionsPerMinute();
                Set<Long> datesOfTransactionsPerMinute2 = transactionsPerMinute2.keySet();
                for (Long timeStamp : datesOfTransactionsPerMinute2) {
                    int amount = transactionsPerMinute2.get(timeStamp);
                    series.add(new Minute(new Date(timeStamp)), amount);
                }
                dataSet.addSeries(series);
                break;

            /*
            Rest: fallthrough
            */
            case AVERAGE_RESPONSETIMES_PER_TESTSTEP:
            case MAX_RESPONSETIME_PER_TESTSTEP:
            case MIN_RESPONSETIME_PER_TESTSTEP:
            case STACKED_RESPONSETIMES_PER_TESTSTEP:
            case AVERAGE_RESPONSETIMES_PER_REPETITION:
            case MAX_RESPONSETIME_PER_REPETITION:
            case MIN_RESPONSETIME_PER_REPETITION:
            case LAYERED_RESPONSETIMES_PER_REPETITION:
            case TRANSACTION_STATUS:
            default:
                throw new TesterraRuntimeException("Metric Type \"" + nameOfMetricType +
                        "\" is not supported for a Line Chart Data Set");
        }
        if (series.getItemCount() == 0 && dataSetIsEmpty) {
            logger.info("given DataSet is empty, Line Chart for " + nameOfMetricType + " has nothing to show");
        }
        return dataSet;
    }

    /**
     * fill a dataSet with the values of the map responseTimes
     *
     * @param dataSet
     * @param responseTimes
     * @param title
     * @return
     */
    private CategoryDataset fillDataSet(DefaultCategoryDataset dataSet, Map<Integer, Long> responseTimes, String title) {
        Set<Integer> mapIds = responseTimes.keySet();
        for (Integer mapId : mapIds) {
            long responseTime = responseTimes.get(mapId);
            dataSet.addValue(responseTime, title, mapId);
        }
        return dataSet;
    }


    /**
     * creates the Dataset for the BarCharts
     *
     * @param title
     * @param metricType
     * @return
     */
    private DefaultCategoryDataset createDataSetForBarCharts(String title, MetricType metricType) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        String nameOfMetricType = metricType.name();
        switch (metricType) {
            case AVERAGE_RESPONSETIMES_PER_REPETITION:
                Map<Integer, Long> averageResponseTimesPerRepetition = PerfTestReportUtils.getAverageResponseTimePerTestRepetition();
                // fill the Data Set
                fillDataSet(dataSet, averageResponseTimesPerRepetition, title);
                // get highest value of Map
                long highestValue4 = getMaxValueOfMap(averageResponseTimesPerRepetition);
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForRepetitionBarCharts(highestValue4, metricType);
                break;
            case MAX_RESPONSETIME_PER_REPETITION:
                Map<Integer, Long> maxResponseTimesPerRepetition = PerfTestReportUtils.getMaxResponseTimePerTestRepetition();
                // fill the Data Set
                fillDataSet(dataSet, maxResponseTimesPerRepetition, title);
                // get highestValue of Map
                long highestValue5 = getMaxValueOfMap(maxResponseTimesPerRepetition);
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForRepetitionBarCharts(highestValue5, metricType);
                break;
            case MIN_RESPONSETIME_PER_REPETITION:
                Map<Integer, Long> minResponseTimesPerRepetition = PerfTestReportUtils.getMinResponseTimePerTestRepetition();
                // fill the Data Set
                fillDataSet(dataSet, minResponseTimesPerRepetition, title);
                // get lowest value of Map
                long maxValueOfMap1 = getMaxValueOfMap(minResponseTimesPerRepetition);
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForRepetitionBarCharts(maxValueOfMap1, metricType);
                break;
            case AVERAGE_RESPONSETIMES_PER_TESTSTEP:
                Map<Integer, Long> averageResponseTimesPerTestStep = PerfTestReportUtils.getAverageResponseTimePerTestStep();
                // fill the Data Set
                fillDataSet(dataSet, averageResponseTimesPerTestStep, title);
                // get highestValue of Map
                long highestValue2 = getMaxValueOfMap(averageResponseTimesPerTestStep);
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForTestStepBarChart(highestValue2, metricType);
                break;
            case MAX_RESPONSETIME_PER_TESTSTEP:
                Map<Integer, Long> maxResponseTimesPerTestStep = PerfTestReportUtils.getMaxResponseTimePerTestStep();
                // fill the Data Set
                fillDataSet(dataSet, maxResponseTimesPerTestStep, title);
                // get highestValue of Map
                long highestValue = getMaxValueOfMap(maxResponseTimesPerTestStep);
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForTestStepBarChart(highestValue, metricType);
                break;
            case MIN_RESPONSETIME_PER_TESTSTEP:
                Map<Integer, Long> minResponseTimesPerTestStep = PerfTestReportUtils.getMinResponseTimePerTestStep();
                // fill the Data Set
                fillDataSet(dataSet, minResponseTimesPerTestStep, title);
                // get highest value of Map
                long maxValueOfMap = getMaxValueOfMap(minResponseTimesPerTestStep);
                // adjust font and tickrate to lowest Value
                setDefaultFontSizeAndTickRateForTestStepBarChart(maxValueOfMap, metricType);
                break;
            case STACKED_RESPONSETIMES_PER_TESTSTEP:
                Map<Integer, Long> averageResponseTimePerTestStep = PerfTestReportUtils.getAverageResponseTimePerTestStep();
                Map<Integer, Long> maxResponseTimePerTestStep = PerfTestReportUtils.getMaxResponseTimePerTestStep();
                Map<Integer, Long> minResponseTimePerTestStep = PerfTestReportUtils.getMinResponseTimePerTestStep();

                int numberOfTestSteps = PerfTestReportUtils.getNumberOfTestSteps();
                long highestValue3 = 0l;
                // iteration starts with index 1 and the last entry is at index==size
                for (int i = 1; i <= numberOfTestSteps; i++) {
                    // order of adding values has to be retained for correct output
                    //1. add min responsetime to dataset
                    long minResponseTimeOfTestStep = minResponseTimePerTestStep.get(i);
                    dataSet.addValue(minResponseTimeOfTestStep, "Min Responsetime", String.valueOf(i));

                    //2. add max responsetime to dataset
                    Long averageResponseTimeOfTestStep = averageResponseTimePerTestStep.get(i);
                    dataSet.addValue(averageResponseTimeOfTestStep, "Avg Responsetime", String.valueOf(i));

                    //3. add avg responsetime to dataset
                    Long maxResponseTimeOfTestStep = maxResponseTimePerTestStep.get(i);
                    dataSet.addValue(maxResponseTimeOfTestStep, "Max Responsetime", String.valueOf(i));
                    // get highestValue;
                    highestValue3 = getMaxValueOfMap(maxResponseTimePerTestStep);
                }
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForTestStepBarChart(highestValue3, metricType);
                break;

            case TRANSACTION_STATUS:
                Map<String, Integer> transactionStatus = PerfTestReportUtils.getTransactionStatus();
                Set<String> statusKeys = transactionStatus.keySet();
                for (String statusKey : statusKeys) {
                    dataSet.addValue(transactionStatus.get(statusKey).doubleValue(), title, statusKey);
                }
                break;
            case LAYERED_RESPONSETIMES_PER_REPETITION:
                Map<Integer, Long> averageResponseTimePerTestRepetition = PerfTestReportUtils.getAverageResponseTimePerTestRepetition();
                Map<Integer, Long> maxResponseTimePerTestRepetition = PerfTestReportUtils.getMaxResponseTimePerTestRepetition();
                Map<Integer, Long> minResponseTimePerTestRepetition = PerfTestReportUtils.getMinResponseTimePerTestRepetition();
                Long highestValue7 = 0l;
                // iteration starts with index 1 and the last entry is at index==size
                for (int i = 1; i <= numberOfTestExecutions; i++) {
                    // order of adding values has to be retained for correct output
                    //1. add avg response time to dataset
                    Long maxResponseTimeOfTestStep = maxResponseTimePerTestRepetition.get(i);
                    dataSet.addValue(maxResponseTimeOfTestStep, "Max Responsetime", String.valueOf(i));
                    //2. add max response time to dataset
                    Long averageResponseTimeOfTestStep = averageResponseTimePerTestRepetition.get(i);
                    dataSet.addValue(averageResponseTimeOfTestStep, "Avg Responsetime", String.valueOf(i));
                    //3. add min response time to dataset
                    Long minResponseTimeOfTestStep = minResponseTimePerTestRepetition.get(i);
                    dataSet.addValue(minResponseTimeOfTestStep, "Min Responsetime", String.valueOf(i));
                    // get highestValue;
                    highestValue7 = getMaxValueOfMap(maxResponseTimePerTestRepetition);
                }
                // adjust font and tickrate to highest Value
                setDefaultFontSizeAndTickRateForRepetitionBarCharts(highestValue7, metricType);
                break;
                /*
            Rest: fallthrough
            */
            case RESPONSETIMES_PER_TEST:
            case RESPONSETIMES_PER_TESTSTEP:
            case REQUESTS_PER_MINUTE:
            case REQUESTS_PER_HOUR:
            case TRANSACTIONS_PER_SECOND:
            case REQUESTS_PER_SECOND:
            case TRANSACTIONS_PER_MINUTE:
            case TRANSACTIONS_PER_HOUR:
            default:

                throw new TesterraRuntimeException("Metric Type \"" + nameOfMetricType +
                        "\" is not supported for a Bar Chart Data Set");

        }
        if (dataSet.getColumnCount() == 0) {
            logger.info("given DataSet is empty, Line Chart for " + nameOfMetricType + " has nothing to show");
        }
        return dataSet;
    }

    //================= configure Font and Tick Rate =================

    /**
     * set the fontsize and tickrate for y-Axis of BarCharts
     *
     * @param metricType
     * @param renderer
     * @param rangeAxis
     */
    private void setFontSizeAndTickRateOfBarCharts(MetricType metricType, BarRenderer renderer, NumberAxis rangeAxis) {
        Font font = renderer.getItemLabelFont(0, 0);
        switch (metricType) {
            case AVERAGE_RESPONSETIMES_PER_TESTSTEP:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForAvgResponseTimeBarChart));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForAvgBarChart));
                break;
            case MAX_RESPONSETIME_PER_TESTSTEP:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForMaxResponseTimeBarChart));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForMaxBarChart));
                break;
            case AVERAGE_RESPONSETIMES_PER_REPETITION:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForAvgRepetitionBarCharts));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForAvgRepetitionBarChart));
                break;
            case MAX_RESPONSETIME_PER_REPETITION:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForMaxRepetitionBarCharts));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForMaxRepetitionBarChart));
                break;
            case MIN_RESPONSETIME_PER_REPETITION:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForMinRepetitionBarCharts));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForMinRepetitionBarChart));
                break;
            case LAYERED_RESPONSETIMES_PER_REPETITION:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForLayeredRepetitionBarChart));
                renderer.setBaseLegendTextFont(new Font(font.getName(), Font.BOLD, fontSizeForLayeredRepetitionBarChart));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForLayeredBarChart));
                break;
            case MIN_RESPONSETIME_PER_TESTSTEP:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForMinResponseTimeBarChart));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForMinBarChart));
                break;
            case TRANSACTION_STATUS:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, 12));
                rangeAxis.setTickUnit(new NumberTickUnit(200));
                break;
            case STACKED_RESPONSETIMES_PER_TESTSTEP:
                renderer.setBaseItemLabelFont(new Font(font.getName(), Font.BOLD, fontSizeForStackeResponseTimeBarChart));
                rangeAxis.setTickUnit(new NumberTickUnit(tickRateForStackedBarChart));
                break;

            /**
             * Rest:
             */
            case RESPONSETIMES_PER_TEST:
            case RESPONSETIMES_PER_TESTSTEP:
            case REQUESTS_PER_SECOND:
            case REQUESTS_PER_MINUTE:
            case REQUESTS_PER_HOUR:
            case TRANSACTIONS_PER_SECOND:
            case TRANSACTIONS_PER_MINUTE:
            case TRANSACTIONS_PER_HOUR:

            default:
                throw new TesterraRuntimeException("Metric Type \"" + metricType.name() + "\" is not supported for Fontsetting and TickRate of BarCharts");
        }
    }

    /**
     * Set fontsize and tickRate for use of the renderer in dBarCharts dependent on highestValue
     *
     * @param highestValue
     * @param metricType
     */
    private void setDefaultFontSizeAndTickRateForRepetitionBarCharts(long highestValue, MetricType metricType) {
        String stringOfHighestValue = String.valueOf(highestValue);
        int numberOfDigits = stringOfHighestValue.length();
        int divisor = 10;
        int adjustedFontSize = 10;
        long adjustedTickRate = highestValue / divisor;
        switch (metricType) {
            case LAYERED_RESPONSETIMES_PER_REPETITION:
                if (numberOfDigits > 3 && numberOfTestExecutions > 20) {
                    fontSizeForLayeredRepetitionBarChart = adjustedFontSize;
                }
                if (highestValue > 10000) {
                    tickRateForLayeredBarChart = adjustedTickRate;
                }
                break;
            case MAX_RESPONSETIME_PER_REPETITION:
                if (highestValue > 10000) {
                    tickRateForMaxRepetitionBarChart = adjustedTickRate;
                }
                if (numberOfDigits > 3 && numberOfTestExecutions > 20) {
                    fontSizeForMaxRepetitionBarCharts = adjustedFontSize;
                }
                break;
            case AVERAGE_RESPONSETIMES_PER_REPETITION:
                if (highestValue > 10000) {
                    tickRateForAvgRepetitionBarChart = adjustedTickRate;
                }
                if (numberOfDigits > 3 && numberOfTestExecutions > 20) {
                    fontSizeForAvgRepetitionBarCharts = adjustedFontSize;
                }
                break;
            case MIN_RESPONSETIME_PER_REPETITION:
                if (highestValue > 10000) {
                    tickRateForMinRepetitionBarChart = adjustedTickRate;
                }
                if (numberOfDigits > 3 && numberOfTestExecutions > 20) {
                    fontSizeForMinRepetitionBarCharts = adjustedFontSize;
                }
                break;

            case AVERAGE_RESPONSETIMES_PER_TESTSTEP:
            case MAX_RESPONSETIME_PER_TESTSTEP:
            case MIN_RESPONSETIME_PER_TESTSTEP:
            case STACKED_RESPONSETIMES_PER_TESTSTEP:
            case TRANSACTION_STATUS:
            case RESPONSETIMES_PER_TEST:
            case RESPONSETIMES_PER_TESTSTEP:
            case REQUESTS_PER_SECOND:
            case REQUESTS_PER_MINUTE:
            case REQUESTS_PER_HOUR:
            case TRANSACTIONS_PER_SECOND:
            case TRANSACTIONS_PER_MINUTE:
            case TRANSACTIONS_PER_HOUR:

            default:
                throw new TesterraRuntimeException("Metric Type \"" + metricType.name() + "\" is not supported for Fontsetting and TickRate of Charts based on Testexecutions");
        }
    }

    /**
     * Set fontsize and tickRate for StackedBarChart dependent on highestValue
     *
     * @param highestValue
     * @param metricType
     */
    private void setDefaultFontSizeAndTickRateForTestStepBarChart(long highestValue, MetricType metricType) {
        String stringOfHighestValue = String.valueOf(highestValue);
        int numberOfDigits = stringOfHighestValue.length();
        int adjustedFontSize = 10;
        int divisor = 10;
        int numberOfTestSteps = PerfTestReportUtils.getNumberOfTestSteps();
        long adjustedTickRate = highestValue / divisor;
        switch (metricType) {
            case AVERAGE_RESPONSETIMES_PER_TESTSTEP:
                if (numberOfDigits > 4 && numberOfTestSteps > 15) {
                    fontSizeForAvgResponseTimeBarChart = adjustedFontSize;
                }
                if (highestValue > 10000) {
                    tickRateForAvgBarChart = adjustedTickRate;
                }
                break;
            case MAX_RESPONSETIME_PER_TESTSTEP:
                if (numberOfDigits > 4 && numberOfTestSteps > 15) {
                    fontSizeForMaxResponseTimeBarChart = adjustedFontSize;
                }
                if (highestValue > 10000) {
                    tickRateForMaxBarChart = adjustedTickRate;
                }
                break;
            case MIN_RESPONSETIME_PER_TESTSTEP:
                if (numberOfDigits > 4 && numberOfTestSteps > 15) {
                    fontSizeForMinResponseTimeBarChart = adjustedFontSize;
                }
                if (highestValue > 10000) {
                    tickRateForMinBarChart = adjustedTickRate;
                }
                break;
            case STACKED_RESPONSETIMES_PER_TESTSTEP:
                if (numberOfDigits > 4 && numberOfTestSteps > 15) {
                    fontSizeForStackeResponseTimeBarChart = adjustedFontSize;
                }
                // lower amount of y-axis values
                if (highestValue * 3 > 10000) {
                    tickRateForStackedBarChart = highestValue * 3 / divisor;
                }
                break;
            case TRANSACTION_STATUS:
            case RESPONSETIMES_PER_TEST:
            case RESPONSETIMES_PER_TESTSTEP:
            case REQUESTS_PER_SECOND:
            case REQUESTS_PER_MINUTE:
            case REQUESTS_PER_HOUR:
            case TRANSACTIONS_PER_SECOND:
            case TRANSACTIONS_PER_MINUTE:
            case TRANSACTIONS_PER_HOUR:
            case LAYERED_RESPONSETIMES_PER_REPETITION:
            case MAX_RESPONSETIME_PER_REPETITION:
            case AVERAGE_RESPONSETIMES_PER_REPETITION:
            case MIN_RESPONSETIME_PER_REPETITION:

            default:
                throw new TesterraRuntimeException("Metric Type \"" + metricType.name() + "\" is not supported for Fontsetting and TickRate of Charts based on Testrepetition");
        }

    }

    /**
     * set the fontSizes for Charts dependent on the number Of TestSteps
     * has to be called after generateMetrics, because number Of TestSteps is generated there
     */
    private void adjustFontSizesToNumberOfTestSteps() {
        int numberOfTestSteps = PerfTestReportUtils.getNumberOfTestSteps();
        if (numberOfTestSteps > 5) {
            fontSizeForAvgResponseTimeBarChart = 12;
            fontSizeForStackeResponseTimeBarChart = 12;
            fontSizeForMinResponseTimeBarChart = 12;
            fontSizeForMaxResponseTimeBarChart = 12;
        }
        if (numberOfTestSteps > 18) {
            fontSizeForStackeResponseTimeBarChart = 10;
            fontSizeForMaxResponseTimeBarChart = 10;
        }
        if (numberOfTestSteps > 20) {
            fontSizeForAvgResponseTimeBarChart = 10;
            fontSizeForMinResponseTimeBarChart = 10;
        }
    }

    /**
     * set the fontSizes for Charts dependent on the amount of repetitions
     */
    private void adjustFontSizesAndImageWidthToNumberOfRepetitions() {
        // increase image-width
        if (numberOfTestExecutions > 14) {
            widthOfRepetitionCharts = 1600;
        }
        // adjust font size
        if (numberOfTestExecutions > 35) {
            fontSizeForLayeredRepetitionBarChart = 10;
            fontSizeForAvgRepetitionBarCharts = 10;
            fontSizeForMinRepetitionBarCharts = 10;
            fontSizeForMaxRepetitionBarCharts = 10;
        }
    }

    //================= Save Stuff =================

    /**
     * create subDirectory named after testmethodname to save corresponding graphs there
     *
     * @param testMethod
     */
    private void makeSubDirectoryForTestMethod(String testMethod) {
        File resultsSubFolder = new File(PerfTestReportUtils.getDestinationFolder() + testMethod.concat("\\") +
                subFolderForGraphs);
        if (!resultsSubFolder.exists()) {
            resultsSubFolder.mkdirs();
        }
    }

    /**
     * save a given Chart as JPEG in the folder "Graphs_Of_Perf_Test"
     *
     * @param chart
     * @param methodName
     * @param metricType
     * @throws IOException
     */
    private void saveGraphAsJPEG(JFreeChart chart, String chartName, String methodName, MetricType metricType) throws IOException {
        File lineChart = new File(PerfTestReportUtils.getDestinationFolder() + methodName.concat("\\") +
                subFolderForGraphs.concat("\\") + chartName + ".jpeg");
        switch (metricType) {
            /**
             * fallthrough because all Graphs showing repetition have the same width: widthOfRepetitionCharts
             */
            case AVERAGE_RESPONSETIMES_PER_REPETITION:
            case MAX_RESPONSETIME_PER_REPETITION:
            case MIN_RESPONSETIME_PER_REPETITION:
            case LAYERED_RESPONSETIMES_PER_REPETITION:
                ChartUtilities.saveChartAsJPEG(lineChart, chart, widthOfRepetitionCharts, heightOfImage);
                break;

            /**
             * fallthrough because all other Graphs use final widthOfImage
             */
            case AVERAGE_RESPONSETIMES_PER_TESTSTEP:
            case MAX_RESPONSETIME_PER_TESTSTEP:
            case MIN_RESPONSETIME_PER_TESTSTEP:
            case STACKED_RESPONSETIMES_PER_TESTSTEP:
            case TRANSACTION_STATUS:
            case RESPONSETIMES_PER_TEST:
            case RESPONSETIMES_PER_TESTSTEP:
            case REQUESTS_PER_SECOND:
            case REQUESTS_PER_MINUTE:
            case REQUESTS_PER_HOUR:
            case TRANSACTIONS_PER_SECOND:
            case TRANSACTIONS_PER_MINUTE:
            case TRANSACTIONS_PER_HOUR:
                ChartUtilities.saveChartAsJPEG(lineChart, chart, widthOfImage, heightOfImage);
                break;
            default:
                ChartUtilities.saveChartAsJPEG(lineChart, chart, widthOfImage, heightOfImage);
                logger.warn("No specific dimensions for the saving of the given MetricType: \"" + metricType.name() + "\" are supported yet. Using default values.");
        }
    }

    //================= Generation Of Metrics =================

    /**
     * sorts RawData and generates metrics
     *
     * @param pageLoadInfos
     */
    private void generateMetrics(Map<Long, List<TimingInfo>> pageLoadInfos) {
        //Initialization for GraphGeneration
        PerfTestReportUtils.initializeDataForGraphGeneration(pageLoadInfos);
        //Generate Average, Min, Max response times per test method repetition
        PerfTestReportUtils.generateMinMaxAvgResponseTimePerMethodExecution();
        //Generate Average, Min, Max response times per TestStep
        PerfTestReportUtils.generateMinMaxAvgResponseTimePerTestStep();
        // collect TestStatus per Transaction
        PerfTestReportUtils.generateTransactionStatus();
        // create Map with all Responsetimes
        PerfTestReportUtils.generateResponseTimeHistory();
        // generate Transaktions per Second
        PerfTestReportUtils.generateTransactionThroughput(Calendar.SECOND);
        // generate Transaktions per Minute
        PerfTestReportUtils.generateTransactionThroughput(Calendar.MINUTE);
        // generate Transaktions per Hour
        PerfTestReportUtils.generateTransactionThroughput(Calendar.HOUR_OF_DAY);
        //generate Request per Second
        PerfTestReportUtils.generateRequestThroughput(Calendar.SECOND);
        //generate Request per Minute
        PerfTestReportUtils.generateRequestThroughput(Calendar.MINUTE);
        //generate Request per Hour
        PerfTestReportUtils.generateRequestThroughput(Calendar.HOUR_OF_DAY);
    }

    //================= Getter for Min, Max Values of Maps<Integer, Long> =================

    /**
     * extract the max value of the given map
     *
     * @param responseTimeMap
     * @return highestValue
     */
    private long getMaxValueOfMap(Map<Integer, Long> responseTimeMap) {
        if (responseTimeMap == null) {
            throw new TesterraSystemException("Map is null");
        }

        int size = responseTimeMap.size();
        if (size == 0) {
            return 0L;
        }

        long highestValue = 0L;
        Set<Integer> mapKeys = responseTimeMap.keySet();
        for (Integer mapKey : mapKeys) {
            long maxResponseTimeOfTestStep = responseTimeMap.get(mapKey);
            if (maxResponseTimeOfTestStep > highestValue) {
                highestValue = maxResponseTimeOfTestStep;
            }
        }
        return highestValue;
    }

    /**
     * extract the min value of the given map
     *
     * @param responseTimeMap
     * @return lowestValue
     */
    private long getMinValueOfMap(Map<Integer, Long> responseTimeMap) {
        if (responseTimeMap == null) {
            throw new TesterraSystemException("Map is null");
        }

        int size = responseTimeMap.size();
        if (size == 0) {
            return 0L;
        }

        long lowestValue = 100000L;
        Set<Integer> mapKeys = responseTimeMap.keySet();
        for (Integer mapKey : mapKeys) {
            long minResponseTimeOfTestStep = responseTimeMap.get(mapKey);
            if (lowestValue > minResponseTimeOfTestStep) {
                lowestValue = minResponseTimeOfTestStep;
            }
        }
        return lowestValue;
    }
}
