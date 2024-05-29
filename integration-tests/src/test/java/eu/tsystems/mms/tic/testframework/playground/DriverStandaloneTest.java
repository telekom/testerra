package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created on 2024-05-29
 *
 * @author mgn
 */
public class DriverStandaloneTest extends AbstractWebDriverTest {

    @Test
    public void testFailing() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        request.setBrowser(Browsers.chrome);

        WEB_DRIVER_MANAGER.getWebDriver(request);
        Assert.assertTrue(false);
    }

}
