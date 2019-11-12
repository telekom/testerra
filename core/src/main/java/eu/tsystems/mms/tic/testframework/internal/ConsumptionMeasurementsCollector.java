/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConsumptionMeasurementsCollector {

    private final List<ContextMeasurement> contextMeasurements = new LinkedList<>();
    private boolean squash = false;

    public static class ContextMeasurement {

        private final String name;
        private final String yAxisTitle;
        private final String unit;
        private Long maxValue;
        private Map<Long, Long> measurements = new LinkedHashMap<>();
        private Map<Long, String> markers = new LinkedHashMap<>();

        public ContextMeasurement(String name, String yAxisTitle, String unit) {
            this.name = name;
            this.yAxisTitle = yAxisTitle;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }

        public String getyAxisTitle() {
            return yAxisTitle;
        }

        public Long getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(Long maxValue) {
            this.maxValue = maxValue;
        }
    }

    public void addValue(ContextMeasurement contextMeasurement, long value) {
        addValue(contextMeasurement, value, System.currentTimeMillis());
    }

    public void addValue(ContextMeasurement contextMeasurement, long value, long timestamp) {
        check(contextMeasurement);

        boolean put = true;
        if (squash) {
            Object[] objects = contextMeasurement.measurements.values().toArray();
            if (objects.length > 0 && (long) objects[objects.length - 1] == value) {
                put = false;
            }
        }

        if (put) {
            contextMeasurement.measurements.put(timestamp, value);
        }
    }

    public void addMarker(ContextMeasurement contextMeasurement, String text) {
        addMarker(contextMeasurement, text, System.currentTimeMillis());
    }

    public void addMarker(ContextMeasurement contextMeasurement, String text, long timestamp) {
        check(contextMeasurement);

        contextMeasurement.markers.put(timestamp, text);
    }

    private void check(ContextMeasurement contextMeasurement) {
        if (!contextMeasurements.contains(contextMeasurement)) {
            contextMeasurements.add(contextMeasurement);
        }
    }

    public List<ContextMeasurement> getAllSeries() {
        return contextMeasurements;
    }

    public Map<Long, Long> getMeasurements(ContextMeasurement contextMeasurement) {
        for (ContextMeasurement measurement : contextMeasurements) {
            if (measurement == contextMeasurement) {
                return measurement.measurements;
            }
        }
        return null;
    }

    public Map<Long, String> getMarkers(ContextMeasurement contextMeasurement) {
        for (ContextMeasurement measurement : contextMeasurements) {
            if (measurement == contextMeasurement) {
                return measurement.markers;
            }
        }
        return null;
    }

    public void setSquash(boolean squash) {
        this.squash = squash;
    }
}
