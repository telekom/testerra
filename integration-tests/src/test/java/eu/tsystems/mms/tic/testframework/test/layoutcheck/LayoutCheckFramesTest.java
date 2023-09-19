/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LayoutCheckFramesTest extends AbstractTestSitesTest implements LocatorFactoryProvider, UiElementFinderFactoryProvider {

    @Override
    protected TestPage getTestPage() {
        return TestPage.LAYOUT_FRAME;
    }

    @Test
    public void testT01_CheckElementLayoutFrame() {
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(getWebDriver());
        UiElement frame = uiElementFinder.find(By.id("iframe"));
        UiElement uiElement = frame.find(LOCATE.byQa("section/layoutTestArticle"));

        uiElement.expect().screenshot().pixelDistance("TestArticle").isLowerThan(1.3);
    }

    @Test
    public void testT02_CheckElementLayoutWithSubfolderFrame() {
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(getWebDriver());
        UiElement frame = uiElementFinder.find(By.id("iframe"));
        UiElement uiElement = frame.find(LOCATE.byQa("section/layoutTestArticle"));
        uiElement.assertThat().screenshot().pixelDistance("subfolder/TestArticle").isLowerThan(1.3);
    }

}
