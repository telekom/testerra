package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.events.FennecEvent;
import eu.tsystems.mms.tic.testframework.events.FennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.events.FennecEventType;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.slf4j.Logger;

class ProxyUtils {

    static void log(Logger logger, SessionContext sessionContext, String msg) {
        String scid = "unrelated";
        if (sessionContext != null) {
            scid = sessionContext.id;
        }
        logger.info("[SCID:" + scid + "] - " + msg);
    }

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
                FennecEventService.getInstance().fireEvent(new FennecEvent(FennecEventType.CONTEXT_UPDATE)
                        .addData(FennecEventDataType.CONTEXT, methodContext));

            }
        }
    }

}
