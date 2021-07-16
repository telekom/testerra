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

import eu.tsystems.mms.tic.testframework.internal.asserts.AbstractPropertyAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultFileAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultQuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.FileAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default implementation of {@link ImageAssertion}
 * @author Mike Reiche
 */
public class DefaultImageAssertion extends AbstractPropertyAssertion<File> implements ImageAssertion {

    public DefaultImageAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public QuantityAssertion<Double> pixelDistance(String referenceImageName) {
        final AtomicReference<LayoutCheck.MatchStep> atomicMatchStep = new AtomicReference<>();
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<Double>() {
            @Override
            public Double getActual() {
                LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels(provider.getActual(), referenceImageName);
                atomicMatchStep.set(matchStep);
                return matchStep.distance;
            }

            @Override
            public String createSubject() {
                return "pixel distance to image="+Format.param(referenceImageName);
            }

            @Override
            public void failedFinally(AbstractPropertyAssertion assertion) {
                LayoutCheck.MatchStep matchStep = atomicMatchStep.get();
                atomicMatchStep.get();
                if (matchStep!=null && !matchStep.takeReferenceOnly) {
                    LayoutCheck.toReport(matchStep);
                }
            }
        });
    }

    @Override
    public FileAssertion file() {
        return propertyAssertionFactory.createWithParent(DefaultFileAssertion.class, this, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return provider.getActual();
            }

            @Override
            public String createSubject() {
                return provider.getActual().getAbsolutePath();
            }
        });
    }
}
