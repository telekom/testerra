/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.tsystems.mms.tic.testframework.common;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.internal.BuildInformation;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the core main class where everything begins.
 * Using this method will initialize Testerra and all its modules.
 * @author Mike Reiche <mike.reiche@t-systems.com>
 */
public class Testerra {

    private final static Logger LOGGER = LoggerFactory.getLogger(Testerra.class);
    private final static Injector injector = initIoc();
    private static final EventBus eventBus;
    private static final Booter booter;
    private static LoggerContext loggerContext;
    private static BuildInformation buildInformation;

    public enum Properties implements IProperties {
        DRY_RUN("tt.dryrun", false),
        MONITOR_MEMORY("tt.monitor.memory", true),
        DEMO_MODE("tt.demomode",false),
        @Deprecated
        SELENIUM_SERVER_HOST("tt.selenium.server.host", "localhost"),
        @Deprecated
        SELENIUM_SERVER_PORT("tt.selenium.server.port", 4444),
        SELENIUM_SERVER_URL("tt.selenium.server.url", String.format("http://%s:%s/wd/hub", SELENIUM_SERVER_HOST.asString(), SELENIUM_SERVER_PORT.asString())),
        BASEURL("tt.baseurl", null),
        LOG_LEVEL("tt.loglevel", "INFO"),
        WEBDRIVER_TIMEOUT_SECONDS_PAGELOAD("webdriver.timeouts.seconds.pageload", 120),
        WEBDRIVER_TIMEOUT_SECONDS_SCRIPT("webdriver.timeouts.seconds.script", 120),
        WEBDRIVER_TIMEOUT_SECONDS_RETRY("webdriver.timeouts.seconds.retry", 10),
        PERF_TEST("tt.perf.test", false),
        PERF_GENERATE_STATISTICS("tt.perf.generate.statistics", false),
        REUSE_DATAPROVIDER_DRIVER_BY_THREAD("tt.reuse.dataprovider.driver.by.thread", false),
        /**
         * @todo Default should be based on WebDriverMode class
         */
        WEBDRIVER_MODE("tt.webdriver.mode", "local"),
        FAILURE_CORRIDOR_ACTIVE("tt.failure.corridor.active", true),
        FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH("tt.failure.corridor.allowed.failed.tests.high", 0),
        FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID("tt.failure.corridor.allowed.failed.tests.mid", 0),
        FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW("tt.failure.corridor.allowed.failed.tests.low", 0),
        EXECUTION_OMIT_IN_DEVELOPMENT("tt.execution.omit.indevelopment", false),
        ;

        private final String property;
        private Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }

        @Override
        public IProperties newDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public Double asDouble() {
            return PropertyManager.getPropertiesParser().getDoubleProperty(toString(),defaultValue);
        }
        @Override
        public Long asLong() {
            return PropertyManager.getPropertiesParser().getLongProperty(toString(), defaultValue);
        }
        @Override
        public Boolean asBool() {
            return PropertyManager.getPropertiesParser().getBooleanProperty(toString(), defaultValue);
        }
        @Override
        public String asString() {
            return PropertyManager.getPropertiesParser().getProperty(toString(), defaultValue);
        }
    }

    static {
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        loggerContext = Configurator.initialize(defaultConfiguration);
        buildInformation = new BuildInformation();
        eventBus = new EventBus();
        booter = new Booter();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            booter.shutdown();
        }));
    }

    public static LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static Injector getInjector() {
        return injector;
    }

    public static BuildInformation getBuildInformation() {
        return buildInformation;
    }

    /**
     * We initialize the IoC modules in class name order,
     * and override each previously configured module with the next.
     */
    private static Injector initIoc() {
        if (injector ==null) {
            Reflections reflections = new Reflections(TesterraListener.DEFAULT_PACKAGE);
            Set<Class<? extends AbstractModule>> classes = reflections.getSubTypesOf(AbstractModule.class);
            Iterator<Class<? extends AbstractModule>> iterator = classes.iterator();
            TreeMap<String, Module> sortedModules = new TreeMap<>();
            try {
                // Sort all modules
                while (iterator.hasNext()) {
                    Class<? extends AbstractModule> moduleClass = iterator.next();
                    Constructor<?> ctor = moduleClass.getConstructor();
                    sortedModules.put(moduleClass.getSimpleName(), (Module) ctor.newInstance());
                }
                LOGGER.info(String.format("Register IoC modules: %s", String.join(", ", sortedModules.keySet())));

                // Override each module with next
                Module prevModule = null;
                for (Module overrideModule : sortedModules.values()) {
                    if (prevModule!=null) {
                        overrideModule = Modules.override(prevModule).with(overrideModule);
                    }
                    prevModule = overrideModule;
                }
                return Guice.createInjector(prevModule);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }
}
