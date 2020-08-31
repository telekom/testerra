package eu.tsystems.mms.tic.testframework.events;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import java.lang.reflect.Method;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public abstract class AbstractMethodEvent {
    private ITestResult testResult;
    private String methodName;
    private MethodContext methodContext;
    private ITestContext testContext;
    private IInvokedMethod invokedMethod;

    public ITestResult getTestResult() {
        return testResult;
    }

    public AbstractMethodEvent setTestResult(ITestResult testResult) {
        this.testResult = testResult;
        return this;
    }

    public ITestNGMethod getTestMethod() {
        return TestNGHelper.getTestMethod(testResult, testContext, invokedMethod);
    }

    public Method getMethod() {
        return getTestMethod().getConstructorOrMethod().getMethod();
    }

    public String getMethodName() {
        return methodName;
    }

    public AbstractMethodEvent setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodContext getMethodContext() {
        return methodContext;
    }

    public AbstractMethodEvent setMethodContext(MethodContext methodContext) {
        this.methodContext = methodContext;
        return this;
    }

    public ITestContext getTestContext() {
        return testContext;
    }

    public AbstractMethodEvent setTestContext(ITestContext context) {
        this.testContext = context;
        return this;
    }

    public IInvokedMethod getInvokedMethod() {
        return invokedMethod;
    }

    public AbstractMethodEvent setInvokedMethod(IInvokedMethod invokedMethod) {
        this.invokedMethod = invokedMethod;
        return this;
    }
    public boolean isSkipped() {
        return testResult.getStatus() == ITestResult.SKIP;
    }

    public boolean isFailed() {
        return (!testResult.isSuccess() && !isSkipped());
    }
}
