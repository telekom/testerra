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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.UiElementBase;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class AbstractCheckFieldAction extends AbstractFieldAction implements Loggable {

    private UiElementBase checkableInstance;

    public AbstractCheckFieldAction(Field field, AbstractPage declaringPage) {
        super(field, declaringPage);
    }

    protected abstract void checkField(UiElementBase checkableInstance, Check check);
    protected boolean execute = false;

    @Override
    public boolean before() {
        boolean isCheckAnnotated = field.isAnnotationPresent(Check.class);
        boolean isCheckable = UiElementBase.class.isAssignableFrom(field.getType());

        if (isCheckAnnotated && !isCheckable) {
            throw new RuntimeException("Field {" + field.getName() + "} of " + declaringPage
                    + " is annotated with @Check, but the class doesn't implement " + UiElementBase.class.getCanonicalName());
        }

        if (isCheckable) {
            if (((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC)) {
                // if the Check annotated field is not static
                throw new RuntimeException("Checkable field MUST be non-static: " +
                        declaringPage + "." + field.getName());
            }
            else {
                if (isCheckAnnotated) {
                    execute = true;
                }
            }
        }

        additionalBeforeCheck();
        return execute;
    }

    protected void additionalBeforeCheck() {}

    @Override
    public void execute() {
        String fieldName = field.getName();
        try {
            checkableInstance = (UiElementBase) field.get(declaringPage);
        } catch (IllegalAccessException e) {
            log().error("Internal Error", e);
            return;
        } catch (IllegalArgumentException e) {
            log().error("Internal Error. Maybe tried to get field from object that does not declare it.", e);
            return;
        }
        Check check = field.getAnnotation(Check.class);
        if (checkableInstance == null) {
            throw new RuntimeException(String.format("Field {%s.%s} is annotated with @Check and was never initialized (it is null)." +
                "This is not allowed because @Check indicates a mandatory GuiElement of a Page.", declaringPage, fieldName));
        } else {
            try {
                checkField(checkableInstance, check);
            } catch (Throwable t) {
                MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
                if (methodContext != null && t.getMessage() != null) {
                    methodContext.getErrorContext().setThrowable(t.getMessage(), t);
                }
                throw t;
            }
        }
    }

    @Override
    public void after() {

    }
}
