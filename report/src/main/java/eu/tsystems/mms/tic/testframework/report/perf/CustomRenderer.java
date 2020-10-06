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
 package eu.tsystems.mms.tic.testframework.report.perf;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;


/**
 * Renderer for 3D Bar Chart: changes color of bars to cyan (default) otherwise according to the given test status
 */
public class CustomRenderer extends BarRenderer3D {

    /**
     * compares item with standard colour and uses colours to show status of test
     *
     * @param row .
     * @param column .
     * @return .
     */
    public Paint getItemPaint(final int row, final int column) {
        CategoryDataset dataSet = getPlot().getDataset();
        // get the value of the x-Axis Entry
        Comparable comp = dataSet.getColumnKey(column);
        Class classOfComparable = comp.getClass();

        //choose a standard colour
        Paint paint = Color.cyan;
        if (classOfComparable == String.class) {
            String columnKey = (String) dataSet.getColumnKey(column);
            // for Status of Tests change colour according to the Status e.g. Success=Green,...
            if (columnKey.equals(PerfTestReportUtils.TEXT_TEST_SUCCESS)) {
                paint = Color.green;

            } else if (columnKey.equals(PerfTestReportUtils.TEXT_TEST_FAIL)) {
                paint = Color.red;

            } else if (columnKey.equals(PerfTestReportUtils.TEXT_TEST_SKIP)) {
                paint = Color.yellow;

            }
        }
        return paint;
    }
}
