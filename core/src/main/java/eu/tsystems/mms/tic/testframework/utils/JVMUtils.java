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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.utils;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public final class JVMUtils {

    private static final MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
    private static OperatingSystemMXBean osMBean;
    private static final Logger LOGGER = LoggerFactory.getLogger(JVMUtils.class);

    static {
        try {
            osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        } catch (IOException e) {
            LOGGER.error("Error getting MXBean for CPU Usage requests");
        }
    }

    public static int getCPUUsagePercent() {
        // cpu
        if (osMBean == null) {
            return -1;
        }
        int cpu = (int) (100 * osMBean.getSystemCpuLoad());
        return cpu;
    }

    public static int getMemoryUsagePercent() {
        // mem
        double maxMemory = getMaxMemory();
        double usedMemory = getUsedMemory();
        int percent = (int) (100 * (usedMemory / maxMemory));
        return percent;
    }

    private static final long MB = 1024 * 1024;

    public static long getMaxMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / MB;

        return maxMemory;
    }

    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() / MB;

        return usedMemory;
    }

}
