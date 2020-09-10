/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Date: 28.04.2020
 * Time: 06:38
 *
 * @author Eric Kubenka
 */
public class ReportInfoTest extends AbstractWebDriverTest {

    @Test
    public void testT01_AddReportInfo() {

        final ReportInfo.MethodInfo methodInfo = new ReportInfo.MethodInfo();
        Assert.assertFalse(methodInfo.hasInfos(), "Info present.");

        methodInfo.addInfo("foo", "bar");
        Assert.assertEquals(methodInfo.getInfo("foo"), "bar");
        Assert.assertTrue(methodInfo.hasInfos(), "Info present.");

        methodInfo.addInfo("foo", "modified bar");
        Assert.assertEquals(methodInfo.getInfo("foo"), "modified bar");
    }

    @Test
    public void testT02_OverrideInfo() {

        final ReportInfo.MethodInfo methodInfo = new ReportInfo.MethodInfo();

        methodInfo.addInfo("foo", "bar");
        Assert.assertEquals(methodInfo.getInfo("foo"), "bar");

        methodInfo.addInfo("foo", "modified bar");
        Assert.assertEquals(methodInfo.getInfo("foo"), "modified bar");
    }

    @Test
    public void testT03_RemoveInfo() {

        final ReportInfo.MethodInfo methodInfo = new ReportInfo.MethodInfo();

        methodInfo.addInfo("foo", "bar");
        Assert.assertEquals(methodInfo.getInfo("foo"), "bar");

        methodInfo.removeInfo("foo");
        Assert.assertFalse(methodInfo.hasInfos(), "Info present.");

        Assert.assertNull(methodInfo.getInfo("foo"));
    }

    @Test
    public void testT04_PriorityInfo() {

        final ReportInfo.DashboardInfo dashboardInfo = new ReportInfo.DashboardInfo();
        Assert.assertFalse(dashboardInfo.hasInfos(), "Infos set.");

        dashboardInfo.addInfo(2, "bar");
        dashboardInfo.addInfo(1, "foo");

        Assert.assertTrue(dashboardInfo.hasInfos(), "Infos set.");
    }
}
