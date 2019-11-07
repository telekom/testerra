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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.Groups;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.FieldAction;

import java.lang.reflect.Field;

/**
 * Created by rnhb on 24.02.2016.
 */
public class GuiElementGroupAction extends FieldAction {

    private final GuiElementGroups guiElementGroups;

    public GuiElementGroupAction(Field field, AbstractPage declaringPage, GuiElementGroups guiElementGroups) {
        super(field, declaringPage);
        this.guiElementGroups = guiElementGroups;
    }

    @Override
    protected boolean before() {
        boolean isAnnotated = field.isAnnotationPresent(Groups.class);
        boolean isGuiElement = GuiElement.class.isAssignableFrom(typeOfField);
        return isAnnotated && isGuiElement;
    }

    @Override
    protected void execute() {
        Groups annotation = field.getAnnotation(Groups.class);
        String[] groupNames = annotation.groupNames();
        GuiElement guiElement;
        try {
            guiElement = (GuiElement) field.get(declaringPage);
        } catch (IllegalAccessException e) {
            logger.error("Internal Error", e);
            return;
        }

        for (String groupName : groupNames) {
            guiElementGroups.addGuiElementToGroup(guiElement, groupName);
        }
    }

    @Override
    protected void after() {

    }
}
