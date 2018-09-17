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
package eu.tsystems.mms.tic.testframework.core.playground;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import eu.tsystems.mms.tic.testframework.internal.utils.TimingInfosCollector;
import eu.tsystems.mms.tic.testframework.report.utils.ReportUtils;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Arrays;


public class ReportViewsTest extends AbstractTest {

    @Test
    public void testMeasurements() {
        TimingInfosCollector c = new TimingInfosCollector();

        String[] categories = {"a", "b", "c"};

        Arrays.stream(categories).forEach(cat -> {
            for (int i = 0; i < 10; i++) {
                int value = RandomUtils.generateRandomInt(8000) + 2000;
                c.add(new TimingInfo(cat, cat, value, System.currentTimeMillis()));
                TestUtils.sleep(100);
            }
        });

        ReportUtils.addExtraTopLevelTimingsTab("MyTimings","mt.html", false, c);
    }
}
