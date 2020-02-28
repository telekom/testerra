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
        GuiElement anchors = page.getNavigationSubElementsByTagName();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getSubElement_getList_byChildrenXPath() {
        GuiElementListPage page = preparePage();
        GuiElement anchors = page.getNavigationSubElementsByChildrenXPath();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getSubElement_getList_byDescendantsXPath() {
        GuiElementListPage page = preparePage();
        GuiElement anchors = page.getNavigationSubElementsByDescendantsXPath();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getList_byAbsoluteChildrenXPath() {
        GuiElementListPage page = preparePage();
        GuiElement anchors = page.getNavigationSubElementsByAbsoluteChildrenXPath();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getList_byAbsoluteDescendantsXPath() {
        GuiElementListPage page = preparePage();
        GuiElement anchors = page.getNavigationSubElementsByAbsoluteDescendantsXPath();
        testNavigationAnchors(anchors);
    }

    private void testNavigationAnchors(GuiElement anchors) {
        Assert.assertEquals(anchors.getNumberOfFoundElements(), 3);
        Assert.assertEquals(anchors.getList().size(), 3);
        Assert.assertEquals(anchors.getList().get(0).getText(), "First");
        Assert.assertEquals(anchors.getList().get(anchors.getNumberOfFoundElements() - 1).getText(), "Third");
    }

    private void testTableRowsAndData(GuiElement tableRows) {
        Assert.assertEquals(tableRows.getNumberOfFoundElements(), 4);
        GuiElement tableDataUnspecified = tableRows.getSubElement(By.tagName("td"));
        Assert.assertEquals(tableDataUnspecified.getNumberOfFoundElements(), 2);

        GuiElement tableDataSpecified = tableRows.getList().get(1).getSubElement(By.tagName("td"));
        Assert.assertEquals(tableDataSpecified.getNumberOfFoundElements(), 2);
    }

    @Test
    public void test_getSubElement_getList_tableRowsByTagName() {
        GuiElementListPage page = preparePage();
        GuiElement tableRows = page.getTableRowsByTagName();
        testTableRowsAndData(tableRows);

        GuiElement mkriProfileLink = tableRows.getList().get(0).getSubElement(By.tagName("a"));
        mkriProfileLink.asserts().assertAttributeContains("href", "mkri");

        GuiElement erkuProfileLink = tableRows.getList().get(tableRows.getNumberOfFoundElements() - 1).getSubElement(By.tagName("a"));
        erkuProfileLink.asserts().assertAttributeContains("href", "erku");
    }

    @Test
    public void test_getSubElement_getList_tableRowsByDescendantsXPath() {
        GuiElementListPage page = preparePage();
        GuiElement tableRows = page.getTableRowsByTagName();
        testTableRowsAndData(tableRows);

        GuiElement mkriProfileLink = tableRows.getList().get(0).getSubElement(By.xpath(".//a"));
        mkriProfileLink.asserts().assertAttributeContains("href", "mkri");

        GuiElement erkuProfileLink = tableRows.getList().get(tableRows.getNumberOfFoundElements() - 1).getSubElement(By.xpath(".//a"));
        erkuProfileLink.asserts().assertAttributeContains("href", "erku");
    }

    @Override
    protected TestPage getTestPage() {
        return TestPage.LIST;
    }
}
