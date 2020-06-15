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

import eu.tsystems.mms.tic.testframework.actions.Action;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public abstract class FieldAction extends Action {

    protected final Field field;
    protected final Class<?> declaringClass;
    protected final Class<?> typeOfField;
    protected final String fieldName;
    protected final AbstractPage declaringPage;

    public FieldAction(Field field, AbstractPage declaringPage) {
        this.field = field;
        this.declaringClass = field.getDeclaringClass();
        fieldName = field.getName();
        typeOfField = field.getType();
        this.declaringPage = declaringPage;
    }

}
