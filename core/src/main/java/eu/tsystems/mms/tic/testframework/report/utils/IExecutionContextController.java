package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import java.util.Optional;
import org.testng.ITestResult;

public interface IExecutionContextController {
    Optional<SessionContext> getCurrentSessionContext();
    Optional<MethodContext> getCurrentMethodContext();
    Optional<ITestResult> getCurrentTestResult();
    ExecutionContext getExecutionContext();
    void setCurrentSessionContext(SessionContext sessionContext);
}
