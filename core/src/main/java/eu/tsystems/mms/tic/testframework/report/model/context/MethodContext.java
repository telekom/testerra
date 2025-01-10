/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds the information of a test method.
 *
 * @author mibu
 */
public class MethodContext extends AbstractContext {

    public enum Type {
        TEST_METHOD,
        CONFIGURATION_BEFORE_SUITE,
        CONFIGURATION_BEFORE_TEST,
        CONFIGURATION_BEFORE_CLASS,
        CONFIGURATION_BEFORE_METHOD,
        CONFIGURATION_BEFORE_GROUPS,
        CONFIGURATION_AFTER_GROUPS,
        CONFIGURATION_AFTER_METHOD,
        CONFIGURATION_AFTER_CLASS,
        CONFIGURATION_AFTER_TEST,
        CONFIGURATION_AFTER_SUITE,
        CONFIGURATION_METHOD,       // only used as fallback
        DATA_PROVIDER;
    }

    private ITestResult testResult;
    private Status status = Status.NO_RUN;
    private final Type methodType;
    private List<Object> parameterValues;
    private int retryNumber = 0;
    private final int methodRunIndex;
    private final String threadName;
    private TestStep lastFailedStep;
    private Class failureCorridorClass = FailureCorridor.High.class;
    private final List<SessionContext> sessionContexts = new LinkedList<>();
    private final TestStepController testStepController = new TestStepController();
    private final List<MethodContext> relatedMethodContexts = new LinkedList<>();
    private final List<MethodContext> dependsOnMethodContexts = new LinkedList<>();
    private List<CustomContext> customContexts;
    private List<Annotation> customAnnotations;
    private List<LayoutCheckContext> layoutCheckContexts;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *
     * @param name The test method name.
     * @param methodType method type.
     * @param classContext .
     */
    public MethodContext(
            final String name,
            final Type methodType,
            final ClassContext classContext
    ) {
        this.setName(name);
        this.setParentContext(classContext);
        this.methodRunIndex = Counters.increaseMethodExecutionCounter();
        this.methodType = methodType;
        final Thread currentThread = Thread.currentThread();
        this.threadName = currentThread.getName() + "#" + currentThread.getId();
    }

    public void setRetryCounter(int retryCounter) {
        this.retryNumber = retryCounter;
    }

    public int getRetryCounter() {
        return this.retryNumber;
    }

    public void setFailureCorridorClass(Class failureCorridorClass) {
        this.failureCorridorClass = failureCorridorClass;
    }

    public Class getFailureCorridorClass() {
        return this.failureCorridorClass;
    }

    private List<CustomContext> getCustomContexts() {
        if (this.customContexts == null) {
            this.customContexts = new LinkedList<>();
        }
        return customContexts;
    }

    public void addCustomContext(CustomContext customContext) {
        this.getCustomContexts().add(customContext);
    }

    public Stream<CustomContext> readCustomContexts() {
        if (this.customContexts == null) {
            return Stream.empty();
        } else {
            return this.customContexts.stream();
        }
    }

    public Type getMethodType() {
        return this.methodType;
    }

    public Stream<SessionContext> readSessionContexts() {
        return sessionContexts.stream();
    }

    public void addSessionContext(SessionContext sessionContext) {
        if (!this.sessionContexts.contains(sessionContext)) {
            this.sessionContexts.add(sessionContext);
            sessionContext.addMethodContext(this);
            sessionContext.setParentContext(this);
            Testerra.getEventBus().post(new ContextUpdateEvent().setContext(this));
        }
    }

    public ClassContext getClassContext() {
        return (ClassContext) this.getParentContext();
    }

    public Stream<MethodContext> readRelatedMethodContexts() {
        return this.relatedMethodContexts.stream();
    }

    public Stream<MethodContext> readDependsOnMethodContexts() {
        return this.dependsOnMethodContexts.stream();
    }

    public void addRelatedMethodContext(MethodContext relatedMethodContext) {
        this.relatedMethodContexts.add(relatedMethodContext);
    }

    public void addDependsOnMethod(MethodContext methodContext) {
        if (!this.dependsOnMethodContexts.contains(methodContext)) {
            this.dependsOnMethodContexts.add(methodContext);
        }
    }

    private Stream<TestStepAction> readTestStepActions() {
        return this.readTestSteps().flatMap(TestStep::readActions);
    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        return testStepController.addLogMessage(logMessage);
    }

    public TestStep getTestStep(String name) {
        return this.testStepController.getTestStep(name);
    }

    public Stream<TestStep> readTestSteps() {
        return this.testStepController.getTestSteps().stream();
    }

    public TestStep getCurrentTestStep() {
        return this.testStepController.getCurrentTestStep();
    }

    public int getLastFailedTestStepIndex() {
        return this.testStepController.getTestSteps().indexOf(this.lastFailedStep);
    }

    public void setFailedStep(TestStep step) {
        this.lastFailedStep = step;
    }

    public Optional<TestStep> getFailedStep() {
        return Optional.ofNullable(this.lastFailedStep);
    }

    public Stream<ErrorContext> readErrors() {
        return readTestStepActions().flatMap(TestStepAction::readErrors);
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof MethodContext)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return obj.toString().equals(toString());
    }

    public void addOptionalAssertion(Throwable throwable) {
        ErrorContext errorContext = new ErrorContext(throwable, true);
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(errorContext);
        this.updateLayoutCheckContext(errorContext);
    }

    public void addError(Throwable throwable) {
        addError(new ErrorContext(throwable, false));
    }

    public void addError(ErrorContext errorContext) {
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(errorContext);
        this.updateLayoutCheckContext(errorContext);
    }

    private List<LayoutCheckContext> getLayoutCheckContexts() {
        if (this.layoutCheckContexts == null) {
            this.layoutCheckContexts = new LinkedList<>();
        }
        return layoutCheckContexts;
    }

    public void addLayoutCheckContext(LayoutCheckContext layoutCheckContext) {
        this.getLayoutCheckContexts().add(layoutCheckContext);
    }

    public Stream<LayoutCheckContext> readLayoutCheckContexts() {
        if (this.layoutCheckContexts == null) {
            return Stream.empty();
        } else {
            return this.layoutCheckContexts.stream();
        }
    }

    /**
     * Tries to link the last LayoutcheckContext with current ErrorContext
     */
    private void updateLayoutCheckContext(ErrorContext context) {
        this.getLayoutCheckContexts().stream()
                .filter(elem -> elem.errorContext == null)
                .findFirst()
                .ifPresent(elem -> elem.errorContext = context);
    }

    @Deprecated
    public void addPriorityMessage(String msg) {
        log().info(msg, this, Loggable.prompt);
    }

    public boolean isConfigMethod() {
        return !isTestMethod();
    }

    public boolean isTestMethod() {
        return methodType == Type.TEST_METHOD;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isStatusOneOf(Status... statuses) {
        return Arrays.stream(statuses).anyMatch(givenStatus -> givenStatus == this.status);
    }

    @Override
    public String toString() {
        return "MethodContext{" +
                "methodRunIndex=" + methodRunIndex +
                ", name='" + getName() + '\'' +
                '}';
    }

    /**
     * Publish the screenshots to the report into the current errorContext.
     */
    public void addScreenshots(Stream<Screenshot> screenshots) {
        screenshots.forEach(this::addScreenshot);
    }

    public void addScreenshot(Screenshot screenshot) {
        this.testStepController.getCurrentTestStep().getCurrentTestStepAction().addScreenshot(screenshot);
    }

    /**
     * Proper parameter names are available by setting {https://stackoverflow.com/questions/6759880/getting-the-name-of-a-method-parameter}
     */
    public Parameter[] getParameters() {
        return getTestNgResult().map(testResult -> testResult.getMethod().getConstructorOrMethod().getMethod().getParameters()).orElse(new Parameter[]{});
    }

    public MethodContext setParameterValues(Object[] parameters) {
        // TestNG method parameters can be NULL
        this.parameterValues = Arrays.stream(parameters).filter(Objects::nonNull).collect(Collectors.toList());
        return this;
    }

    public List<Object> getParameterValues() {
        if (this.parameterValues == null) {
            return Collections.emptyList();
        } else {
            return this.parameterValues;
        }
    }

    public Optional<ITestResult> getTestNgResult() {
        return Optional.ofNullable(this.testResult);
    }

    public MethodContext setTestNgResult(ITestResult testResult) {
        this.testResult = testResult;
        return this;
    }

    public Stream<Annotation> readAnnotations() {
        return Stream.concat(
                (this.customAnnotations != null) ? this.customAnnotations.stream() : Stream.empty(),
                getTestNgResult()
                        .map(testResult -> Stream.of(testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotations()))
                        .orElse(Stream.empty())
        );
    }

    public Optional<Fails> getFailsAnnotation() {
        return getAnnotation(Fails.class);
    }

    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return readAnnotations().filter(annotationClass::isInstance).map(annotation -> (T) annotation).findFirst();
    }

    /**
     * Required by cucumber-connector
     *
     * @param annotation
     */
    public void addAnnotation(Annotation annotation) {
        if (this.customAnnotations == null) {
            this.customAnnotations = new LinkedList<>();
        }
        this.customAnnotations.add(annotation);
    }

    public int getMethodRunIndex() {
        return methodRunIndex;
    }

    public String getThreadName() {
        return threadName;
    }

    /**
     * @deprecated Use {@link TestStepAction#readEntries()} instead
     */
    public Stream<String> readInfos() {
        return Stream.empty();
    }

    /**
     * @deprecated Use {@link TestStepAction#addLogMessage(LogMessage)} instead
     */
    public void addInfo(String info) {
        log().info(info, this, Loggable.prompt);
    }

    /**
     * @deprecated Use {@link TestStepAction#readEntries()} instead
     */
    public Optional<String> getPriorityMessage() {
        return Optional.empty();
    }
}
