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
package eu.tsystems.mms.tic.testframework.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultUiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.testng.annotations.Test;

public class LayoutCheckImageTest extends AbstractTestSitesTest implements LocatorFactoryProvider {

    @Override
    protected TestPage getTestPage() {
        return TestPage.LAYOUT_IMAGE;
    }

    private UiElement getUIElementQa(final String qaTag) {
        return new DefaultUiElementFactory().createWithWebDriver(getWebDriver(), LOCATE.byQa(qaTag));
    }

    /**
     * Uses the property tt.layoutcheck.pixel.rgb.deviation.percent=3 to verify a photo with some RGP deviations
     */
    @Test
    public void testT01_CheckImageLayout() {
        UiElement uiElement = getUIElementQa("testimage");
        uiElement.expect().screenshot().pixelDistance("TestImage").isLowerThan(0.5);
    }

}
