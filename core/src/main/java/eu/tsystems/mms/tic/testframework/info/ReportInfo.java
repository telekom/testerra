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
package eu.tsystems.mms.tic.testframework.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pele on 31.05.2016.
 */
public class ReportInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportInfo.class);

    public static abstract class Info {
        public abstract boolean hasInfos();
    }

    public static abstract class KeyValueInfo extends Info {

        private Map<String, String> infos = new LinkedHashMap<>();

        public Map<String, String> getInfos() {
            return infos;
        }

        public void addInfo(String key, String value) {
            infos.put(key, value);
        }

        public void removeInfo(String key) {
            infos.remove(key);
        }

        public String getInfo(String key) {
            return infos.get(key);
        }

        @Override
        public boolean hasInfos() {
            return infos.size() > 0;
        }
    }

    public static class InfoObject {

        private final int priority;
        private final String value;
        private String linkTo = null;

        public InfoObject(int priority, String value) {
            this.priority = priority;
            this.value = value;
        }

        public InfoObject(int priority, String value, String linkTo) {
            this(priority, value);
            this.linkTo = linkTo;
        }

        public int getPriority() {
            return priority;
        }

        public String getValue() {
            return value;
        }

        public String getLinkTo() {
            return linkTo;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static abstract class PriorityInfo extends Info {

        private Map<String, InfoObject> infos = new LinkedHashMap<>();

        public Map<String, InfoObject> getInfos() {
            return infos;
        }

        public void addInfoWithDefaultPrio(String value) {
            addInfo(null, value);
        }

        public synchronized void addInfo(Integer priority, String value) {
            addInfo(priority, value, null);
        }

        public synchronized void addInfo(Integer priority, String value, String linkToOrNull) {
            // prepare priority value
            if (priority == null) {
                priority = 99;
            } else if (priority < 0) {
                priority = 0;
            }

            if (priority > 99) {
                LOGGER.warn("Prio greater 99, reducing to 99 for: " + value);
                priority = 99;
            }

            // check if it already exists
            String deleteKey = null;
            for (String key : infos.keySet()) {
                InfoObject infoObject = infos.get(key);
                if (infoObject.getValue().equals(value)) {
                    if (infoObject.getPriority() < priority) {
                        // wenn bestehende prio höher ist, dann raus
                        return;
                    } else {
                        // sonst neuen Wert schreiben und alten löschen
                        deleteKey = key;
                    }
                }
            }
            if (deleteKey != null) {
                infos.remove(deleteKey);
            }

            // key prefix is priority, so it stays sortable (naturally)
            String prioString = priority + "";

            // add zero prior to single number
            if (priority < 10) {
                prioString = "0" + prioString;
            }

            // add timestamp
            prioString += System.currentTimeMillis();

            // add to map
            infos.put(prioString, new InfoObject(priority, value, linkToOrNull));
        }

        @Override
        public boolean hasInfos() {
            return infos.size() > 0;
        }
    }

    public static class MethodInfo extends KeyValueInfo {
    }

    public static class RunInfo extends KeyValueInfo {
    }

    public static class DashboardInfo extends PriorityInfo {
    }

    public static class DashboardWarning extends PriorityInfo {
    }

    private static final ThreadLocal<MethodInfo> currentMethodInfo = new ThreadLocal<>();
    private static final RunInfo runInfo = new RunInfo();
    private static final DashboardInfo dashboardInfo = new DashboardInfo();
    private static final DashboardWarning dashboardWarning = new DashboardWarning();

    public static MethodInfo getCurrentMethodInfo() {
        if (currentMethodInfo.get() == null) {
            currentMethodInfo.set(new MethodInfo());
        }
        return currentMethodInfo.get();
    }

    public static RunInfo getRunInfo() {
        return runInfo;
    }

    public static DashboardInfo getDashboardInfo() {
        return dashboardInfo;
    }

    public static DashboardWarning getDashboardWarning() {
        return dashboardWarning;
    }

    public static void clearCurrentMethodInfo() {
        currentMethodInfo.remove();
    }

}
