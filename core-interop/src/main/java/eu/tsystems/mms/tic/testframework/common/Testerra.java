package eu.tsystems.mms.tic.testframework.common;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class Testerra {

    public final static Injector injector = initIoc();

    public enum Properties implements IProperties {
        DRY_RUN("tt.dryrun", false),
        MONITOR_MEMORY("tt.monitor.memory", true),
        DEMO_MODE("tt.demomode",true),
        @Deprecated
        SELENIUM_SERVER_HOST("tt.selenium.server.host", "localhost"),
        @Deprecated
        SELENIUM_SERVER_PORT("tt.selenium.server.port", 4444),
        SELENIUM_SERVER_URL("tt.selenium.server.url", String.format("http://%s:%s/wd/hub", SELENIUM_SERVER_HOST, SELENIUM_SERVER_PORT)),
        BASEURL("tt.baseurl", "about:_blank"),
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
            return PropertyManager.parser.getDoubleProperty(toString(),defaultValue);
        }
        @Override
        public Long asLong() {
            return PropertyManager.parser.getLongProperty(toString(), defaultValue);
        }
        @Override
        public Boolean asBool() {
            return PropertyManager.parser.getBooleanProperty(toString(), defaultValue);
        }
        @Override
        public String asString() {
            return PropertyManager.parser.getProperty(toString(), defaultValue);
        }
    }

    /**
     * We initialize the IoC modules in class name order,
     * and override each previously configured module with the next.
     */
    private static Injector initIoc() {
        if (injector ==null) {
            Reflections reflections = new Reflections(TesterraCommons.DEFAULT_PACKAGE_NAME);
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
                System.out.println(String.format("%s - Register IoC modules: %s", Testerra.class.getCanonicalName(), sortedModules.keySet()));

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
                e.printStackTrace();
                //LOGGER.error("Unable to initialize IoC modules", e);
            }
        }
        return null;
    }
}
