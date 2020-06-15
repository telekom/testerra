package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;

public class WebDriverProxy extends ObjectUtils.PassThroughProxy<WebDriver> implements Loggable {

    public WebDriverProxy(WebDriver driver) {
        super(driver);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SessionContext sessionContext = WebDriverManager.getSessionContextFromWebDriver(target);

        if (!method.getName().equals("toString")) {
            String msg = target.getClass().getSimpleName() + "." + method.getName();
            log().trace(msg);
        }

        WebDriverProxyUtils.updateSessionContextRelations(sessionContext);

        return invoke(method, args);
    }

    public WebDriver getWrappedWebDriver() {
        return target;
    }
}
