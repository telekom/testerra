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
package eu.tsystems.mms.tic.testframework.internal;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Timings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Timings.class);

    public static final long LARGE_LIMIT = 2000;

    public static final Map<Integer, Long> TIMING_GUIELEMENT_FIND = new ConcurrentHashMap<Integer, Long>();
    public static final Map<Integer, Long> TIMING_GUIELEMENT_FIND_WITH_PARENT = new ConcurrentHashMap<Integer, Long>();

    private static int findCounter = 0;

    private Timings() {
        
    }

    public static synchronized int raiseFindCounter() {
        findCounter++;
        return findCounter;
    }

    public static int getFindCounter() {
        return findCounter;
    }

    private static File createGraph(String title, String filename, Map<Integer, Long> map) {
        try {
            XYSeriesCollection collection = new XYSeriesCollection();
            XYSeries series = new XYSeries("find()s");
            for (Integer integer : map.keySet()) {
                series.add(integer, map.get(integer));
            }
            collection.addSeries(series);
            JFreeChart chart = GraphGenerator.createLineChart(collection, title, "#", "ms", GraphGenerator.DataType.NUMBER);
            File file = GraphGenerator.saveGraphAsJPEG(chart, "graph/" + filename + ".jpg", 800, 400);
            return file;
        } catch (Exception e) {
            LOGGER.error("Could not save graph", e);
            return null;
        }
    }

    public static File createGuiElementFindTimingsGraph() {
        /*
        Create chart
         */
        return createGraph("find()s", "timingsFind", TIMING_GUIELEMENT_FIND);
    }

    public static File createGuiElementFindWithParentTimingsGraph() {
        /*
        Create chart
         */
        return createGraph("find()s with parent", "timingsFindParent", TIMING_GUIELEMENT_FIND_WITH_PARENT);
    }
}
