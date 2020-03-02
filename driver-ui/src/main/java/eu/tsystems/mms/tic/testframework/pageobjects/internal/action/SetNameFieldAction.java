/*
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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

import java.lang.reflect.Field;

/**
 * Created by rnhb on 02.02.2016.
 */
public class SetNameFieldAction extends FieldAction {

    public SetNameFieldAction(Field field, AbstractPage declaringPage) {
        super(field, declaringPage);
    }

    @Override
    public boolean before() {
        return true;
    }

    @Override
    public void execute() {
        Class<?> typeOfField = field.getType();
        if (Nameable.class.isAssignableFrom(typeOfField)) {
            Nameable guiElement = null;
            try {
                guiElement = (Nameable) field.get(declaringPage);
            } catch (IllegalAccessException e) {
                logger.error("Failed to assign description to " + field + ". Make sure the field was made accessible in the" +
                        " AbstractPage class before calling this method.");
            }
            if (guiElement != null) {
                // Add element description
                String name = guiElement.getName();
                if (StringUtils.isStringEmpty(name)) {
                    guiElement.setName(field.getName());
                }
            }
        }
    }

    @Override
    public void after() {

    }
}
