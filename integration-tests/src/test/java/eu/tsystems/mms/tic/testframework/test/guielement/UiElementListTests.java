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
package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementList;
import eu.tsystems.mms.tic.testframework.test.core.pageobjects.testdata.UiElementListPage;
import eu.tsystems.mms.tic.testframework.test.core.pageobjects.testdata.components.TableRow;
import org.testng.annotations.Test;

public class UiElementListTests extends AbstractExclusiveTestSitesTest<UiElementListPage> {

    @Override
    public Class<UiElementListPage> getPageClass() {
        return UiElementListPage.class;
    }

    @Test
    public void test_getSubElement_getList_byTagName() {
        UiElementListPage page = getPage();
        UiElement anchors = page.getNavigationSubElementsByTagName();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getSubElement_getList_byChildrenXPath() {
        UiElementListPage page = getPage();
        UiElement anchors = page.getNavigationSubElementsByChildrenXPath();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getSubElement_getList_byDescendantsXPath() {
        UiElementListPage page = getPage();
        UiElement anchors = page.getNavigationSubElementsByDescendantsXPath();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getList_byAbsoluteChildrenXPath() {
        UiElementListPage page = getPage();
        UiElement anchors = page.getNavigationSubElementsByAbsoluteChildrenXPath();
        testNavigationAnchors(anchors);
    }

    @Test
    public void test_getList_byAbsoluteDescendantsXPath() {
        UiElementListPage page = getPage();
        UiElement anchors = page.getNavigationSubElementsByAbsoluteDescendantsXPath();
        testNavigationAnchors(anchors);
    }

    private void testNavigationAnchors(UiElement anchors) {
        anchors.expect().foundElements().is(3);

        UiElementList<UiElement> list = anchors.list();
        list.first().expect().text("First");
        list.get(1).expect().text("Second");
        list.last().expect().text("Third");

        list.stream()
                .filter(uiElement -> uiElement.waitFor().classes("odd").is(true))
                .forEach(uiElement -> {});
    }

    private void testTableRowsAndData(TableRow tableRows) {
        tableRows.expect().foundElements().is(4);
        TestableUiElement tableDataUnspecified = tableRows.columns();
        tableDataUnspecified.expect().foundElements().is(2);

        TestableUiElement tableDataSpecified = tableRows.list().get(1).columns();
        tableDataSpecified.expect().foundElements().is(2);

        tableRows.list().stream().forEach(tableRow -> tableRow.expect().displayed(true));
    }

    @Test
    public void test_getSubElement_getList_tableRowsByTagName() {
        UiElementListPage page = getPage();
        TableRow tableRows = page.getTableRowsByTagName();
        testTableRowsAndData(tableRows);

        tableRows.list().first().linkByName().expect().attribute(Attribute.HREF).endsWith("mkri").is(true);
        tableRows.list().get(1).linkByName().expect().attribute(Attribute.HREF).endsWith("joku").is(true);
        tableRows.list().last().linkByName().expect().attribute(Attribute.HREF).endsWith("erku").is(true);
        tableRows.list().forEach(tableRow -> tableRow.linkByName().expect().attribute(Attribute.HREF).startsWith("http").is(true));
    }

    @Test
    public void test_getSubElement_getList_tableRowsByDescendantsXPath() {
        UiElementListPage page = getPage();
        TableRow tableRows = page.getTableRowsByTagName();
        testTableRowsAndData(tableRows);

        tableRows.list().first().linkByXPath().expect().attribute(Attribute.HREF).endsWith("mkri").is(true);
        tableRows.list().get(1).linkByXPath().expect().attribute(Attribute.HREF).endsWith("joku").is(true);
        tableRows.list().last().linkByXPath().expect().attribute(Attribute.HREF).endsWith("erku").is(true);
    }

    @Override
    protected TestPage getTestPage() {
        return TestPage.LIST;
    }
}
