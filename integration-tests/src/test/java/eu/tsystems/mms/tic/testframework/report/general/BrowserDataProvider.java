package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.report.model.TestReportSevenWebDriverSetupConfig;
import org.testng.annotations.DataProvider;

/**
 * Created by fakr on 23.10.2017
 */
public class BrowserDataProvider {

    @DataProvider(name = "configDP")
    public Object[][] createConfigDP() {
        Object[][] objects = new Object[5][1];
        objects[0][0] = TestReportSevenWebDriverSetupConfig.FIREFOXv57;
        objects[1][0] = TestReportSevenWebDriverSetupConfig.FIREFOXv58;
        objects[2][0] = TestReportSevenWebDriverSetupConfig.CHROMEv62;
        objects[3][0] = TestReportSevenWebDriverSetupConfig.CHROMEv63;
        objects[4][0] = TestReportSevenWebDriverSetupConfig.CHROMEv64;
        return objects;
    }

}
