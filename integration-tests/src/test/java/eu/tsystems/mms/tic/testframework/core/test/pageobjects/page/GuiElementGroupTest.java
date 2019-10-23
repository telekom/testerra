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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithGuiElementGroups;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups.GuiElementGroup;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups.GuiElementGroups;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 24.02.2016.
 */
public class GuiElementGroupTest extends AbstractTestSitesTest {

    /**
     * Test basic behaviour of GuiElement Groups. No WebDriver necessary.
     *
     * @throws Exception
     */
    @Test
    public void testT01_GuiElementGroups() throws Exception {
        PageWithGuiElementGroups pageWithGuiElementGroups = new PageWithGuiElementGroups(WebDriverManager.getWebDriver());

        GuiElementGroups guiElementGroups = pageWithGuiElementGroups.getGuiElementGroups();

        GuiElementGroup group1 = guiElementGroups.getGuiElementGroup(PageWithGuiElementGroups.GROUP_1);
        GuiElementGroup group2 = guiElementGroups.getGuiElementGroup(PageWithGuiElementGroups.GROUP_2);
        GuiElementGroup group3 = guiElementGroups.getGuiElementGroup(PageWithGuiElementGroups.GROUP_3);

        String message = "Group contains correct number of entries.";
        Assert.assertTrue(group1.elements().size() == 2, message);
        Assert.assertTrue(group2.elements().size() == 1, message);
        Assert.assertTrue(group3.elements().size() == 1, message);

        message = "Group contains correct GuiElement";
        Assert.assertEquals(group1.elements().get(0).getName(), "g1", message);
        Assert.assertEquals(group1.elements().get(1).getName(), "g3", message);
        Assert.assertEquals(group2.elements().get(0).getName(), "g3", message);
        Assert.assertEquals(group3.elements().get(0).getName(), "g2", message);
    }
}
