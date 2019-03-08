package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.events.FennecEvent;
import eu.tsystems.mms.tic.testframework.events.FennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.events.FennecEventType;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class WebDriverProxy extends ObjectUtils.PassThroughProxy<WebDriver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverProxy.class);

    public WebDriverProxy(WebDriver driver) {
        super(driver);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SessionContext sessionContext = WebDriverManager.getSessionContextFromWebDriver(target);
        String scid = "unrelated";
        if (sessionContext != null) {
            scid = sessionContext.id;
        }
        LOGGER.info("[SCID:" + scid + "] - " + method.getName() + " -");

        /*
        assign usage in current method
        (this is useful for sessions that are shared between method contexts)
         */
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            if (!methodContext.sessionContexts.contains(sessionContext)) {
                methodContext.sessionContexts.add(sessionContext);

                // fire sync of method context
                FennecEventService.getInstance().fireEvent(new FennecEvent(FennecEventType.CONTEXT_UPDATE)
                        .addData(FennecEventDataType.CONTEXT, methodContext));

            }
        }

        return method.invoke(target, args);
    }

    public WebDriver getWrappedWebDriver() {
        return target;
    }
}
