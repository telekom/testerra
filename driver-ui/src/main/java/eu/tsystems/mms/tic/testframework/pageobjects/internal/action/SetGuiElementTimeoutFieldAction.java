/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

import java.lang.reflect.Field;

public class SetGuiElementTimeoutFieldAction extends FieldAction {

    public SetGuiElementTimeoutFieldAction(Field field, AbstractPage declaringPage) {
        super(field, declaringPage);
    }

    @Override
    public boolean before() {
        return true;
    }

    @Override
    public void execute() {
        Class<?> typeOfField = field.getType();
        if (GuiElement.class.isAssignableFrom(typeOfField)) {
            GuiElement guiElement = null;
            try {
                guiElement = (GuiElement) field.get(declaringPage);
            } catch (IllegalAccessException e) {
                logger.error("Failed to set element timeout to " + field + ". Make sure the field was made accessible in the" +
                        " AbstractPage class before calling this method.");
            }
            int timeoutFromPage = declaringPage.getElementTimeoutInSeconds();
            if (guiElement != null) {
                int alreadySetTimeout = guiElement.getTimeoutInSeconds();
                if (alreadySetTimeout != timeoutFromPage) {
                    // override timeout setting only if it is set to default
                    logger.info("Setting page specific timeout for " + declaringPage.getClass().getSimpleName() + "/" + field.getName() + ": " + timeoutFromPage + "s");
                    guiElement.setTimeoutInSeconds(timeoutFromPage);
                }
            }
        }
    }

    @Override
    public void after() {

    }
}
