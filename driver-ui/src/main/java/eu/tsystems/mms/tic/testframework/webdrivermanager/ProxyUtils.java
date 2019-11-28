package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;

class ProxyUtils {

    static void updateSessionContextRelations(SessionContext sessionContext) {
        /*
        assign usage in current method
        (this is useful for sessions that are shared between method contexts)
         */
        ExecutionContextController.setCurrentSessionContext(sessionContext);
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            if (!methodContext.sessionContexts.contains(sessionContext)) {
                methodContext.sessionContexts.add(sessionContext);

                // fire sync of method context
                TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.CONTEXT_UPDATE).addData(TesterraEventDataType.CONTEXT, methodContext));
            }
        }
    }

}
