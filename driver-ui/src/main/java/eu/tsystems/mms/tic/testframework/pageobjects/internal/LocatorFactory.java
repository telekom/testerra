/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.PreparedLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import java.util.function.Consumer;
import org.openqa.selenium.By;

/**
 * A factory for {@link Locator}
 *
 * @author Mike Reiche
 */
public class LocatorFactory implements Loggable {
    ThreadLocal<Consumer<Locator>> locatorConfigurator = new ThreadLocal<>();

    public Locator by(By by) {
        return new DefaultLocator(by);
    }

    public Locator by(XPath xPath) {
        return by(new By.ByXPath(xPath.toString()));
    }

    public Locator byQa(String string) {
        return by(XPath.from("*").attribute(UiElement.Properties.QA_ATTRIBUTE.asString()).is(string));
    }
    public PreparedLocator prepare(final String format) {
        return new DefaultPreparedLocator(format);
    }

    public void setThreadLocalConfigurator(Consumer<Locator> consumer) {
        if (consumer != null) {
            locatorConfigurator.set(consumer);
            log().info("Using thread local configurator");
        } else {
            locatorConfigurator.remove();
            log().info("Unset thread local configurator");
        }
    }
}
