/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.internal.Nameable;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractPage;
import java.lang.reflect.Field;

/**
 * Sets the name of a {@link Nameable} to its field name of the class
 */
public class SetNameFieldAction extends AbstractFieldAction implements Loggable {

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
        /**
         * We dont overwrite the name of {@link AbstractComponent#rootElement}
         */
        if (
                declaringPage instanceof AbstractComponent
                && field.getName().equals("rootElement")
        ) {
            return;
        }

        if (Nameable.class.isAssignableFrom(typeOfField)) {
            Nameable nameable = null;
            try {
                nameable = (Nameable) field.get(declaringPage);
            } catch (IllegalAccessException e) {
                log().error("Failed to assign description to " + field + ". Make sure the field was made accessible in the" +
                        " AbstractPage class before calling this method.");
            }
            if (nameable != null && !nameable.hasOwnName()) {
                nameable.setName(field.getName());
            }
        }
    }

    @Override
    public void after() {

    }
}
