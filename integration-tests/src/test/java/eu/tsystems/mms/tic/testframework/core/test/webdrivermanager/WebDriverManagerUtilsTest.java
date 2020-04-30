package eu.tsystems.mms.tic.testframework.core.test.webdrivermanager;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Tests for WebDriverManager
 * <p>
 * Date: 27.04.2020
 * Time: 06:49
 *
 * @author Eric Kubenka
 */
public class WebDriverManagerUtilsTest extends AbstractWebDriverTest {

    @Test
    public void testT01_AddProxyToCapabilities() throws MalformedURLException {

        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        WebDriverManagerUtils.addProxyToCapabilities(desiredCapabilities, new URL("http://proxy.example.com:8080"));

        final Proxy proxyCap = (Proxy) desiredCapabilities.getCapability(CapabilityType.PROXY);
        Assert.assertEquals(proxyCap.getHttpProxy(), "proxy.example.com:8080");
    }

}
