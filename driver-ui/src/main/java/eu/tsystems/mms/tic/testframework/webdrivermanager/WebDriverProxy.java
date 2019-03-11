package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class WebDriverProxy extends ObjectUtils.PassThroughProxy<WebDriver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverProxy.class);

    public WebDriverProxy(WebDriver driver) {
        super(driver);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SessionContext sessionContext = WebDriverManager.getSessionContextFromWebDriver(target);

        if (!method.getName().equals("toString")) {
            String msg = target.getClass().getSimpleName() + "." + method.getName();
            ProxyUtils.log(LOGGER, sessionContext, msg);
        }

        ProxyUtils.updateSessionContextRelations(sessionContext);

        return method.invoke(target, args);
    }

    public WebDriver getWrappedWebDriver() {
        return target;
    }
}
