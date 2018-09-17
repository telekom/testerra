/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.playground.dynamicproxy;

import eu.tsystems.mms.tic.testframework.dynamicproxy.IDefaultMethodWrapper;
import eu.tsystems.mms.tic.testframework.dynamicproxy.FennecInterceptor;
import eu.tsystems.mms.tic.testframework.dynamicproxy.FennecReversibleInterceptor;
import eu.tsystems.mms.tic.testframework.dynamicproxy.FennecReversibleInterceptorFactory;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Created by joku on 21.11.2016.
 * all 3 possible proxy usages.
 *
 * might be useful to proxy seetestclient, webelement, webdriver, for instance.
 * saves double declaration of methods necessary in other patterns (decorator, adapter?)
 */
public final class ProxyExamples {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyExamples.class);

    /**
     * proxify ANY object. -> downside: no retrieval of original object from proxied object.
     * original object can of course be used separately, it remains unchanged during proxification
     */
    @Test
    public void nonReversibleProxyUsage() {
        Logger proxiedLogger = FennecInterceptor.proxify(LOGGER, new IDefaultMethodWrapper() {
            @Override
            public void before(Object proxy, Method method, Object[] args) {
                System.out.println("method is being called : " + method + " of object " + LOGGER);
            }

            @Override
            public void after(Object proxy, Method method, Object[] args) {
                System.out.println("method was called : " + method + " of object " + LOGGER);
            }

            @Override
            public void handleExceptions(Object proxy, Method method, Object[] args, Exception e) {
                throw new FennecRuntimeException("this should not happen, AT ALL. abort!!",e);
            }
        });

        proxiedLogger.info("log log log .. THIS right here is a method call of a logger wrapped with a default wrapping proxy");
    }

    /**
     * retrieval of proxied Object from Proxifier with FennecReversibleInterceptor
     */
    @Test
    public void singleReversibleProxyUsage() {

        FennecReversibleInterceptor<Logger> Proxifier = new FennecReversibleInterceptor<>(LOGGER, new IDefaultMethodWrapper() {
            @Override
            public void before(Object proxy, Method method, Object[] args) {
                System.out.println("method is being called : " + method + " of object " + LOGGER);
            }

            @Override
            public void after(Object proxy, Method method, Object[] args) {
                System.out.println("method was called : " + method + " of object " + LOGGER);
            }

            @Override
            public void handleExceptions(Object proxy, Method method, Object[] args, Exception e) {
            }
        });

        Logger proxiedLogger = Proxifier.newProxiedObject();

        proxiedLogger.info("logging some information");
        proxiedLogger.debug("logging some debug text");
        proxiedLogger.warn("logging some warnings");

        Logger unproxiedLogger = Proxifier.getUnproxiedObject();

        unproxiedLogger.info("logging some information");
        unproxiedLogger.debug("logging some debug text");
        unproxiedLogger.warn("logging some warnings");
    }

    /**
     * see @FennecReversibleInterceptorFactory
     */
    @Test
    public void reversibleProxyFactoryUsage() {
        FennecReversibleInterceptorFactory<Logger> factory = new FennecReversibleInterceptorFactory<>();

        // use two different wrappers, for illustration of factory capabilities

        IDefaultMethodWrapper beforeWrapper = new IDefaultMethodWrapper() {
            @Override
            public void before(Object proxy, Method method, Object[] args) {
                System.out.println("method is being called : " + method + " of object " + LOGGER);
            }

            @Override
            public void after(Object proxy, Method method, Object[] args) {
            }

            @Override
            public void handleExceptions(Object proxy, Method method, Object[] args, Exception e) {
                throw new FennecSystemException("this should not happen. Breaking", e);
            }
        };

        IDefaultMethodWrapper afterWrapper = new IDefaultMethodWrapper() {
            @Override
            public void before(Object proxy, Method method, Object[] args) {
            }

            @Override
            public void after(Object proxy, Method method, Object[] args) {
                System.out.println("method was called : " + method + " of object " + LOGGER);
            }

            @Override
            public void handleExceptions(Object proxy, Method method, Object[] args, Exception e) {
                throw new FennecSystemException("this should not happen. Breaking", e);
            }
        };

        Logger notifyBeforeLoggingLogger = factory.proxify(LOGGER,beforeWrapper);
        Logger notifyAfterLoggingLogger = factory.proxify(LOGGER,afterWrapper);

        notifyAfterLoggingLogger.info("Logger numero uno logged something");
        notifyBeforeLoggingLogger.info("Logger numero duo logged something");

        // double proxy. does show proxy meta method calls of first proxied object! Can be fixed if necessary.
        Logger notifyBeforeAndAfterLoggingLogger = factory.proxify(notifyAfterLoggingLogger, beforeWrapper);

        notifyBeforeAndAfterLoggingLogger.info("This log entry should have a notification before and after the Logger.info() method call");

        // you can test for being proxied
        LOGGER.info("is proxied? "+factory.isProxied(notifyBeforeAndAfterLoggingLogger)+" object: "+notifyBeforeAndAfterLoggingLogger);

        // remove 2 proxies
        Logger unproxiedLogger =
            factory.getUnproxied(
                factory.getUnproxied(notifyBeforeAndAfterLoggingLogger)
        );

        unproxiedLogger.info("No before or after informations should surround this entry");
    }
}
