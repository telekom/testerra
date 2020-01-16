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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElementListPage;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GuiElementListTests extends AbstractTestSitesTest {

    private GuiElementListPage preparePage() {
        return PageFactory.create(GuiElementListPage.class, WebDriverManager.getWebDriver());
    }

    @Test
    public void test_getSubElement_getList_byTagName() {
        GuiElementListPage page = preparePage();
        GuiElement items = page.getNavigationSubElementsByTagName();
        testNavigationItems(items);
    }

    @Test
    public void test_getSubElement_getList_byChildrenXPath() {
        GuiElementListPage page = preparePage();
        GuiElement items = page.getNavigationSubElementsByChildrenXPath();
        testNavigationItems(items);
    }

    @Test
    public void test_getSubElement_getList_byDescendantsXPath() {
        GuiElementListPage page = preparePage();
        GuiElement items = page.getNavigationSubElementsByDescendantsXPath();
        testNavigationItems(items);
    }

    @Test
    public void test_getList_byAbsoluteChildrenXPath() {
        GuiElementListPage page = preparePage();
        GuiElement items = page.getNavigationSubElementsByAbsoluteChildrenXPath();
        testNavigationItems(items);
    }

    @Test
    public void test_getList_byAbsoluteDescendantsXPath() {
        GuiElementListPage page = preparePage();
        GuiElement items = page.getNavigationSubElementsByAbsoluteDescendantsXPath();
        testNavigationItems(items);
    }

    private void testNavigationItems(GuiElement items) {
        Assert.assertEquals(items.getNumberOfFoundElements(), 3);
        Assert.assertEquals(items.getList().size(), 3);
        Assert.assertEquals(items.getList().get(0).getText(), "First");
        Assert.assertEquals(items.getList().get(items.getNumberOfFoundElements() - 1).getText(), "Third");
    }

    @Test
    public void test_getSubElement_getList_tableRowsByTagName() {
        GuiElementListPage page = preparePage();
        GuiElement rows = page.getTableRowsByTagName();
        Assert.assertEquals(rows.getNumberOfFoundElements(), 4);

        GuiElement mkriProfileLink = rows.getList().get(0).getSubElement(By.tagName("a"));
        mkriProfileLink.asserts().assertAttributeContains("href", "mkri");

        GuiElement erkuProfileLink = rows.getList().get(rows.getNumberOfFoundElements() - 1).getSubElement(By.tagName("a"));
        erkuProfileLink.asserts().assertAttributeContains("href", "erku");
    }

    @Test
    public void test_getSubElement_getList_tableRowsByDescendantsXPath() {
        GuiElementListPage page = preparePage();
        GuiElement rows = page.getTableRowsByTagName();
        Assert.assertEquals(rows.getNumberOfFoundElements(), 4);

        GuiElement mkriProfileLink = rows.getList().get(0).getSubElement(By.xpath(".//a"));
        mkriProfileLink.asserts().assertAttributeContains("href", "mkri");

        GuiElement erkuProfileLink = rows.getList().get(rows.getNumberOfFoundElements() - 1).getSubElement(By.xpath(".//a"));
        erkuProfileLink.asserts().assertAttributeContains("href", "erku");
    }

    @Override
    protected TestPage getStartPage() {
        return TestPage.LIST;
    }
}
