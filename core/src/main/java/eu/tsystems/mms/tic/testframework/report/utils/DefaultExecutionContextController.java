package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import java.util.Optional;
import org.testng.ITestResult;

public class DefaultExecutionContextController implements IExecutionContextController {

    @Override
    public Optional<SessionContext> getCurrentSessionContext() {
        return Optional.ofNullable(ExecutionContextController.getCurrentSessionContext());
    }

    @Override
    public Optional<MethodContext> getCurrentMethodContext() {
        return Optional.ofNullable(ExecutionContextController.getCurrentMethodContext());
    }

    @Override
    public Optional<ITestResult> getCurrentTestResult() {
        return Optional.ofNullable(ExecutionContextController.getCurrentTestResult());
    }

    @Override
    public ExecutionContext getExecutionContext() {
        return ExecutionContextController.getCurrentExecutionContext();
    }

    @Override
    public void setCurrentSessionContext(SessionContext sessionContext) {
        ExecutionContextController.setCurrentSessionContext(sessionContext);
    }
}
