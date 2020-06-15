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

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.HasParent;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;

import java.lang.reflect.Modifier;

public abstract class CheckFieldAction extends FieldAction implements Loggable {
    @Deprecated
    protected final boolean findNot;
    @Deprecated
    private final boolean fast;

    @Deprecated
    protected BasicUiElement checkableInstance;

    public CheckFieldAction(FieldWithActionConfig fieldWithActionConfig, AbstractPage declaringPage) {
        super(fieldWithActionConfig.field, declaringPage);
        this.fast = fieldWithActionConfig.fast;
        this.findNot = fieldWithActionConfig.findNot;
    }

    protected String createReadableMessage() {
        try {
            HasParent element = (HasParent)field.get(declaringPage);
            if (findNot) {
                return String.format("Mandatory {%s} was found, but expected to be NOT", element.toString(true));
            } else {
                return String.format("Mandatory {%s} was not found", element.toString(true));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @deprecated Fast checks are deprecated. Use {@link Check#timeout()} instead
     */
    @Deprecated
    protected abstract void checkField(Check check, boolean fast);
    protected boolean execute = false;

    @Override
    public boolean before() {
        boolean isCheckAnnotated = field.isAnnotationPresent(Check.class);
        boolean isCheckable = BasicUiElement.class.isAssignableFrom(typeOfField);

        if (isCheckAnnotated && !isCheckable) {
            throw new TesterraRuntimeException("Field {" + fieldName + "} of " + declaringClass.getCanonicalName()
                    + " is annotated with @Check, but the class doesn't implement " + BasicUiElement.class.getCanonicalName());
        }

        if (isCheckable) {
            if (((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC)) {
                // if the Check annotated field is not static
                throw new TesterraRuntimeException("Checkable field MUST be non-static: " +
                        declaringClass.getCanonicalName() + "." + field.getName());
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

    protected abstract void additionalBeforeCheck();

    @Override
    public void execute() {
        try {
            checkableInstance = (BasicUiElement) field.get(declaringPage);
        } catch (IllegalAccessException e) {
            log().error("Internal Error", e);
            return;
        } catch (IllegalArgumentException e) {
            log().error("Internal Error. Maybe tried to get field from object that does not declare it.", e);
            return;
        }
        Check check = field.getAnnotation(Check.class);
        if (checkableInstance == null) {
            throw new TesterraRuntimeException(String.format("Field {%s.%s} is annotated with @Check and was never initialized (it is null)." +
                "This is not allowed because @Check indicates a mandatory GuiElement of a Page.", declaringClass.getSimpleName(), fieldName));
        } else {
            log().debug("Looking for GuiElement on " + declaringClass.getSimpleName() + ": " + fieldName
                    + " with locator " + checkableInstance.toString());
            try {
                checkField(check, fast);
            } catch (Throwable t) {
                MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
                if (methodContext != null && t.getMessage() != null) {
                    methodContext.errorContext().setThrowable(t.getMessage(), t);
                }
                throw t;
            }
        }
    }

    @Override
    public void after() {

    }
}
