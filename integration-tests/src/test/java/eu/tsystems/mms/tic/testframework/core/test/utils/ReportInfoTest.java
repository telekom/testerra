package eu.tsystems.mms.tic.testframework.core.test.utils;

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
