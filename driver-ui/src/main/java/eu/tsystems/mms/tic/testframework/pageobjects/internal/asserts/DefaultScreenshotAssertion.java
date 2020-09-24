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
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import java.io.File;

/**
 * Default implementation of {@link ScreenshotAssertion}
 * @author Mike Reiche
 */
public class DefaultScreenshotAssertion extends DefaultImageAssertion implements ScreenshotAssertion {

    private static Report report = Testerra.injector.getInstance(Report.class);

    private final AssertionProvider<Screenshot> providerOverride;

    public DefaultScreenshotAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<Screenshot> provider) {
        super(parentAssertion, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return provider.getActual().getScreenshotFile();
            }

            @Override
            public String getSubject() {
                return String.format("%s.screenshot", provider.getSubject());
            }
        });
        providerOverride = provider;
    }

    @Override
    public ScreenshotAssertion toReport() {
        report.addScreenshot(providerOverride.getActual(), Report.Mode.COPY);
        return this;
    }
}
