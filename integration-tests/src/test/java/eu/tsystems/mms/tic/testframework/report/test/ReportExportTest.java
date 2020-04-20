package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class ReportExportTest extends TesterraTest {

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void test_TestNG_XML() {
        File testNGExportFile = new File(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()) + "/" + Report.XML_FOLDER_NAME + "/testng-results.xml");
        Assert.assertTrue(testNGExportFile.exists());
    }

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void test_JUnit_XML() {
        File testNGExportFile = new File(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()) + "/" + Report.XML_FOLDER_NAME + "/TEST-Results.xml");
        Assert.assertTrue(testNGExportFile.exists());
    }
}
