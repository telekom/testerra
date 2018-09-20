package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.BrowserDataProvider;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.testundertest.WebDriverSetupTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.report.model.TestReportSevenWebDriverSetupConfig;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStepsPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import org.testng.annotations.Test;

import java.text.ParseException;


/**
 * Created by fakr on 17.10.2017
 */
@FennecClassContext("Functional-BrowserConfiguration")
public class BrowserConfigTest {

    /**
     * Checks whether the browser info on Method Details Step is displayed and has the expected content
     *
     * @param wdsConfig
     * @throws ParseException
     */
    @Test(dataProvider = "configDP", dataProviderClass = BrowserDataProvider.class, groups = {SystemTestsGroup.SYSTEMTESTSFILTER7})
    @Fails(description="sagu: BrowserName/Version not anymore in the test name of ClassesDetailsPage")
    public void testT01_checkBrowserOnMethodDetails(TestReportSevenWebDriverSetupConfig wdsConfig) throws ParseException {

        final String testMethodTagName = "(1) " + wdsConfig.name();

        MethodStepsPage methodStepsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPageWithTag(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_7.toString()),
                WebDriverSetupTest.class,
                testMethodTagName
        ).clickStepsTab();
        methodStepsPage.assertBrowserInLogsIsConfiguredBrowser(wdsConfig);
    }

}
