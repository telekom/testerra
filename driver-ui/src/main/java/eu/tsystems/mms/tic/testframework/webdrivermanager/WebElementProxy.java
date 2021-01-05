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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebElementProxy extends ObjectUtils.PassThroughProxy<WebElement> implements Loggable {

    private final WebDriver driver;

    public WebElementProxy(WebDriver driver, WebElement webElement) {
        super(webElement);
        this.driver = driver;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SessionContext sessionContext = WebDriverManager.getSessionContextFromWebDriver(driver);

        if (!method.getName().equals("toString")) {
            String msg = method.getName();
            if (args != null) {
                msg += " " + Arrays.stream(args).map(Object::toString).collect(Collectors.joining(" "));
            }
            log().trace(msg);
        }

        WebDriverProxyUtils.updateSessionContextRelations(sessionContext);

        return invoke(method, args);
    }
}
