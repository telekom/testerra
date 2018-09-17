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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rnhb on 24.02.2016.
 */
public class GuiElementGroups {

    private Map<String, GuiElementGroup> guiElementGroups;

    public GuiElementGroups() {
        guiElementGroups = new HashMap<>();
    }

    public void addGuiElementToGroup(GuiElement guiElement, String groupName) {
        GuiElementGroup guiElementList;
        if (guiElementGroups.containsKey(groupName)) {
            guiElementList = guiElementGroups.get(groupName);
        } else {
            guiElementList = new GuiElementGroup(groupName);
            guiElementGroups.put(groupName, guiElementList);
        }

        guiElementList.add(guiElement);
    }

    public GuiElementGroup getGuiElementGroup(String groupName) {
        return guiElementGroups.get(groupName);
    }
}
