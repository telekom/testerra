/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.location.TesterraBy;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class LayoutCheckTest extends AbstractTestSitesTest implements Loggable {

    @Override
    protected TestPage getStartPage() {
        return TestPage.LAYOUT;
    }

    private GuiElement getGuiElementQa(final String qaTag) {
        return new GuiElement(WebDriverManager.getWebDriver(), TesterraBy.xpath(String.format("//*[@data-qa='%s']",qaTag)));
    }

    @Test
    public void testCheckElementLayout() throws IOException {
        GuiElement guiElement = getGuiElementQa("section/layoutTestArticle");
        guiElement.asserts().assertPixelDistanceGreaterEqualThan("TestArticle", 1);
    }
}
