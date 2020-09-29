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

package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.WebDriver;

public class DefaultPageAssertions implements PageAssertions {
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);
    private static final Report report = Testerra.injector.getInstance(Report.class);
    private final PropertyAssertionConfig propertyAssertionConfig = new PropertyAssertionConfig();
    private final Page page;

    public DefaultPageAssertions(Page page, boolean throwErrors) {
        this.page = page;
        this.propertyAssertionConfig.throwErrors = throwErrors;
    }

    @Override
    public StringAssertion<String> title() {
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return page.getWebDriver().getTitle();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@title", page.toString(true));
            }
        });
    }

    @Override
    public StringAssertion<String> url() {
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return page.getWebDriver().getCurrentUrl();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@url", page.toString(true));
            }
        });
    }

    @Override
    public ImageAssertion screenshot(Report.Mode reportMode) {
        Screenshot screenshot = new Screenshot(page.toString());
        WebDriver webDriver = page.getWebDriver();
        UITestUtils.takeScreenshot(webDriver, screenshot);

        if (reportMode == Report.Mode.ALWAYS) {
            report.addScreenshot(screenshot, Report.FileMode.COPY);
        }

        AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();
        atomicScreenshot.set(screenshot);

        return propertyAssertionFactory.createWithConfig(DefaultImageAssertion.class, this.propertyAssertionConfig, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return atomicScreenshot.get().getScreenshotFile();
            }

            @Override
            public void failed(AbstractPropertyAssertion assertion) {
                // Take new screenshot only if failed
                UITestUtils.takeScreenshot(webDriver, atomicScreenshot.get());
            }

            @Override
            public void failedFinally(AbstractPropertyAssertion assertion) {
                report.addScreenshot(atomicScreenshot.get(), Report.FileMode.MOVE);
            }

            @Override
            public String getSubject() {
                return page.toString(true);
            }
        });
    }
}
