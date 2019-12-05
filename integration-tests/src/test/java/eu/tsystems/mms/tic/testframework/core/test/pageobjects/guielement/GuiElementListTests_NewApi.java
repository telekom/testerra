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
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.NewGuiElementListPage;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.components.TableRow;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class GuiElementListTests_NewApi extends AbstractTestSitesTest {
    private NewGuiElementListPage preparePage() {
        return pageFactory.createPage(NewGuiElementListPage.class);
    }

    @Test
    public void test_getSubElement_getList_byTagName() {
        NewGuiElementListPage page = preparePage();
        IGuiElement items = page.getNavigationSubElementsByTagName();
        testNavigationItems(items);
    }

    @Test
    public void test_getSubElement_getList_byChildrenXPath() {
        NewGuiElementListPage page = preparePage();
        IGuiElement items = page.getNavigationSubElementsByChildrenXPath();
        testNavigationItems(items);
    }

    @Test
    public void test_getSubElement_getList_byDescendantsXPath() {
        NewGuiElementListPage page = preparePage();
        IGuiElement items = page.getNavigationSubElementsByDescendantsXPath();
        testNavigationItems(items);
    }

    @Test
    public void test_getList_byAbsoluteChildrenXPath() {
        NewGuiElementListPage page = preparePage();
        IGuiElement items = page.getNavigationSubElementsByAbsoluteChildrenXPath();
        testNavigationItems(items);
    }

    @Test
    public void test_getList_byAbsoluteDescendantsXPath() {
        NewGuiElementListPage page = preparePage();
        IGuiElement items = page.getNavigationSubElementsByAbsoluteDescendantsXPath();
        testNavigationItems(items);
    }

    private void testNavigationItems(IGuiElement items) {
        items.numberOfElements().is(3);
        items.firstElement().text().is("First");
        items.element(2).text().is("Second");
        items.lastElement().text().is("Third");

        items.list().first().text().is("First");
        items.list().get(1).text().is("Second");
        items.list().last().text().is("Third");
    }

    @Test
    public void test_getSubElement_getList_tableRowsByTagName() {
        NewGuiElementListPage page = preparePage();
        TableRow rows = page.getTableRowsByTagName();
        rows.numberOfElements().is(4);

//        rows.firstElement().linkByName().value(Attribute.HREF).endsWith("mkri");
//        rows.element(2).linkByName().value(Attribute.HREF).endsWith("joku");
//        rows.lastElement().linkByName().value(Attribute.HREF).endsWith("erku");
//        rows.forEach(tableRow -> tableRow.linkByName().value(Attribute.HREF).beginsWith("http"));

        rows.list().first().linkByName().value(Attribute.HREF).endsWith("mkri");
        rows.list().get(1).linkByName().value(Attribute.HREF).endsWith("joku");
        rows.list().last().linkByName().value(Attribute.HREF).endsWith("erku");
        rows.list().forEach(tableRow -> tableRow.linkByName().value(Attribute.HREF).beginsWith("http"));
    }

    @Test
    public void test_getSubElement_getList_tableRowsByDescendantsXPath() {
        NewGuiElementListPage page = preparePage();
        TableRow rows = page.getTableRowsByTagName();
        rows.numberOfElements().is(4);

        rows.firstElement().linkByXPath().value(Attribute.HREF).endsWith("mkri");
        rows.element(2).linkByXPath().value(Attribute.HREF).endsWith("joku");
        rows.lastElement().linkByXPath().value(Attribute.HREF).endsWith("erku");
    }

    @Override
    protected TestPage getStartPage() {
        return TestPage.LIST;
    }
}
