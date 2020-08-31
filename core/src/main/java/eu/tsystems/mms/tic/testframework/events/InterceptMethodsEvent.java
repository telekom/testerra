package eu.tsystems.mms.tic.testframework.events;

import java.util.List;
import org.testng.IMethodInstance;
import org.testng.ITestContext;

public class InterceptMethodsEvent {
    public interface Listener {
        void onInterceptMethods(InterceptMethodsEvent event);
    }
    private List<IMethodInstance> methodInstanceList;
    private ITestContext testContext;
    public List<IMethodInstance> getMethodInstanceList() {
        return methodInstanceList;
    }

    public InterceptMethodsEvent setMethodInstanceList(List<IMethodInstance> methodInstanceList) {
        this.methodInstanceList = methodInstanceList;
        return this;
    }

    public ITestContext getTestContext() {
        return testContext;
    }

    public InterceptMethodsEvent setTestContext(ITestContext testContext) {
        this.testContext = testContext;
        return this;
    }
}
