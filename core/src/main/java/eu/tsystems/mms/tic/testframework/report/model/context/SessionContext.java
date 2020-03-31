package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;

import java.util.LinkedHashMap;
import java.util.Map;

public class SessionContext extends AbstractContext implements SynchronizableContext {

    public String sessionKey;
    public String provider;
    public Map<String, Object> metaData = new LinkedHashMap<>();
    public String sessionId;

    public SessionContext(String sessionKey, String provider) {
        this.sessionKey = sessionKey;
        this.provider = provider;

        final MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
        this.name = currentMethodContext.getName() + "_" + sessionKey;
    }

    private SessionContext() {
    }

    @Override
    public TestStatusController.Status getStatus() {
        /*
        Status is always null here. There is no context result status for sessions.
         */
        return null;
    }

    @Override
    public String toString() {
        return "SessionContext{" +
                "id='" + id + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", provider='" + provider + '\'' +
                ", metaData=" + metaData +
                '}';
    }
}
