/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.webdrivermanager;
import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

/**
 * Class holding configuration settings for the WebDriverManager. Some are writable. This class is not ThreadSafe, some
 * settings may not be valid.
 */
@Deprecated
public class WebDriverManagerConfig extends DesktopWebDriverRequest implements
        Loggable,
        PropertyManagerProvider
{
    @Deprecated
    public WebDriverManagerConfig reset() {
        // do nothing here
        return this;
    }

    @Deprecated
    public boolean shouldShutdownSessions() {
        return true;
    }

    @Deprecated
    public WebDriverManagerConfig setShutdownSessions(boolean close) {
        return this;
    }

    @Deprecated
    public boolean shouldShutdownSessionAfterTestMethod() {
        return getShutdownAfterTest();
    }

    @Deprecated
    public WebDriverManagerConfig setShutdownSessionAfterTestMethod(boolean shutdown) {
        PROPERTY_MANAGER.setSystemProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, shutdown);
        this.setShutdownAfterTest(shutdown);
        return this;
    }

    @Deprecated
    public boolean shouldShutdownSessionOnFailure() {
        return getShutdownAfterTestFailed();
    }

    @Deprecated
    public boolean shouldMaximizeViewport() {
        return getMaximizeBrowser();
    }
}
