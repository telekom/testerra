package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributeView;

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
//        request.setBrowser(Browsers.chrome);

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UITestUtils.takeScreenshot(webDriver, true);
//        Assert.assertTrue(false);
//        File file = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
//        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
//        log().info("take screenshot src {}", file.toPath());
//        log().info("Posix {}", fileAttributeView.readAttributes().permissions().toString());
    }

}
