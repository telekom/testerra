/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.webdrivermanager.SeleniumBiDiTools;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.Credentials;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.LogInspector;
import org.openqa.selenium.bidi.Network;
import org.openqa.selenium.remote.Augmenter;

import java.util.function.Supplier;

/**
 * Created on 2024-02-28
 *
 * @author mgn
 */
public class DefaultSeleniumBiDiTools implements SeleniumBiDiTools {

    @Override
    public Network getNetwork(WebDriver webDriver) {
        validateSupport(webDriver);

        WebDriver originalFromDecorated = WEB_DRIVER_MANAGER.getOriginalFromDecorated(webDriver);

        if (this.isRemoteDriver(webDriver)) {
            Augmenter augmenter = new Augmenter();
            return new Network(augmenter.augment(originalFromDecorated));
        }
        return new Network(originalFromDecorated);
    }

    @Override
    public LogInspector getLogInsepctor(WebDriver webDriver) {
        validateSupport(webDriver);

        WebDriver originalFromDecorated = WEB_DRIVER_MANAGER.getOriginalFromDecorated(webDriver);

        if (this.isRemoteDriver(webDriver)) {
            Augmenter augmenter = new Augmenter();
            return new LogInspector(augmenter.augment(originalFromDecorated));
        }
        return new LogInspector(originalFromDecorated);
    }

    @Override
    public void setBasicAuthentication(WebDriver webDriver, Supplier<Credentials> credentials) {
        throw new NotImplementedException("Basic authentication is missing for remote webdriver");
    }

    @Override
    public void validateSupport(WebDriver webDriver) {
        SeleniumBiDiTools.super.validateSupport(webDriver);

        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(webDriver).get().getWebDriverRequest();
        if (!webDriverRequest.getCapabilities().is("webSocketUrl")) {
            throw new RuntimeException("For using Selenium BiDi the capability 'webSocketUrl=true' is needed.");
        }
    }

}
