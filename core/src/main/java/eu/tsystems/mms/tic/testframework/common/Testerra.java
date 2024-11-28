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

import eu.tsystems.mms.tic.testframework.events.ModulesInitializedEvent;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.internal.BuildInformation;
import eu.tsystems.mms.tic.testframework.logging.MethodContextLogAppender;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * This is the core main class where everything begins.
 * Using this method will initialize Testerra and all its modules.
 *
 * @author Mike Reiche <mike.reiche@t-systems.com>
 */
public class Testerra {

    public enum Properties implements IProperties {
        DRY_RUN("tt.dryrun", false),
        MONITOR_MEMORY("tt.monitor.memory", true),
        DEMO_MODE("tt.demomode", false),
        DEMO_MODE_TIMEOUT("tt.demomode.timeout", 2000),
        @Deprecated
        SELENIUM_SERVER_HOST("tt.selenium.server.host", null),
        @Deprecated
        SELENIUM_SERVER_PORT("tt.selenium.server.port", 4444),
        SELENIUM_SERVER_URL("tt.selenium.server.url", null),
        BASEURL("tt.baseurl", null),
        WEBDRIVER_TIMEOUT_SECONDS_PAGELOAD("webdriver.timeouts.seconds.pageload", 120),
        WEBDRIVER_TIMEOUT_SECONDS_SCRIPT("webdriver.timeouts.seconds.script", 120),
        SELENIUM_WEBDRIVER_CREATE_RETRY("tt.selenium.webdriver.create.retry", 10),
        SELENIUM_REMOTE_TIMEOUT_READ("tt.selenium.remote.timeout.read", 90),
        SELENIUM_REMOTE_TIMEOUT_CONNECTION("tt.selenium.remote.timeout.connection", 10),
        PERF_TEST("tt.perf.test", false),
        PERF_GENERATE_STATISTICS("tt.perf.generate.statistics", false),
        /**
         * @deprecated Use {@link WebDriverRequest#getServerUrl()} instead
         */
        WEBDRIVER_MODE("tt.webdriver.mode", "remote"),
        FAILURE_CORRIDOR_ACTIVE("tt.failure.corridor.active", true),
        FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH("tt.failure.corridor.allowed.failed.tests.high", 0),
        FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID("tt.failure.corridor.allowed.failed.tests.mid", 0),
        FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW("tt.failure.corridor.allowed.failed.tests.low", 0),
        SCREENCASTER_ACTIVE("tt.screencaster.active", false),
        SCREENSHOTTER_ACTIVE("tt.screenshotter.active", true),
        SCREENSHOT_ON_PAGELOAD("tt.screenshot.on.pageload", false),
        ;

        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Testerra.class);
    private static final List<ModuleHook> moduleHooks;
    private static final Injector injector;
    private static final EventBus eventBus;
    private static final LoggerContext loggerContext;
    private static final BuildInformation buildInformation;
    private static final MethodContextLogAppender logAppender;

    static {
        String logLevel = System.getProperty("log4j.level");
        if (logLevel != null) {
            Level desiredLogLevel = Level.valueOf(logLevel.trim().toUpperCase(Locale.ROOT));
            Configurator.setRootLevel(desiredLogLevel);
        }
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        eventBus = new EventBus();
        loggerContext = Configurator.initialize(defaultConfiguration);
        logAppender = new MethodContextLogAppender();
        loggerContext.getRootLogger().addAppender(logAppender);
        logAppender.start();

        buildInformation = new BuildInformation();
        printTesterraBanner();
        moduleHooks = new LinkedList<>();
        injector = initIoc();
        initHooks();

        Runtime.getRuntime().addShutdownHook(new JVMExitHook());
        Runtime.getRuntime().addShutdownHook(new Thread(Testerra::shutdown));
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
     * We initialize the IoC modules in a custom class name order,
     * and override each previously configured module with the next.
     * <p>
     * The custom comparator is needed to prevent that custom modules are initialized before
     * the Testerra core modules 'CoreHook', 'DriverUiHook', 'DriverUi_Desktop'. So Testerra core modules cannot
     * overwrite custom implementations (of factories, providers etc.) but custom modules can do this to inject their own behaviour.
     */
    private static Injector initIoc() {
        FilterBuilder filterBuilder = new FilterBuilder();
        filterBuilder.includePackage(TesterraListener.DEFAULT_PACKAGES);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(TesterraListener.DEFAULT_PACKAGES).filterInputsBy(filterBuilder));
        Set<Class<? extends AbstractModule>> classes = reflections.getSubTypesOf(AbstractModule.class);
        Iterator<Class<? extends AbstractModule>> iterator = classes.iterator();
        TreeMap<String, Module> sortedModules = new TreeMap<>(new ModuleComparator());

        // Override each module with next
        Module prevModule = null;
        try {
            // Sort all modules
            while (iterator.hasNext()) {
                Class<? extends AbstractModule> moduleClass = iterator.next();
                Constructor<?> ctor = moduleClass.getConstructor();
                sortedModules.put(moduleClass.getSimpleName(), (Module) ctor.newInstance());
            }
            LOGGER.info(String.format("Register IoC modules: %s", String.join(", ", sortedModules.keySet())));
            for (Module overrideModule : sortedModules.values()) {
                if (overrideModule instanceof ModuleHook) {
                    moduleHooks.add((ModuleHook) overrideModule);
                }
                if (prevModule != null) {
                    overrideModule = Modules.override(prevModule).with(overrideModule);
                }
                prevModule = overrideModule;
            }
            //return Guice.createInjector(prevModule);
        } catch (Exception e) {
            LOGGER.error("Unable to initialize modules", e);
        }
        return Guice.createInjector(prevModule);
    }

    private static void initHooks() {

        // Register
        //Multibinder<ModuleHook> hookBinder = Multibinder.newSetBinder(binder(), ModuleHook.class);
        //hookBinder.addBinding().to(SelenoidVideoHook.class).in(Scopes.SINGLETON);

        // Load
        // MODULE_HOOKS = new ArrayList<>(Testerra.getInjector().getInstance(Key.get(new TypeLiteral<Set<ModuleHook>>() {})));


        moduleHooks.forEach(moduleHook -> {
            LOGGER.debug("Init " + moduleHook.getClass().getSimpleName() + "...");
            moduleHook.init();
        });

        getEventBus().post(new ModulesInitializedEvent());
    }

    public static void shutdown() {
        moduleHooks.forEach(moduleHook -> {
            LOGGER.debug("Terminate " + moduleHook.getClass().getSimpleName());
            moduleHook.terminate();
        });
    }

    private static void printTesterraBanner() {
        List<String> frameworkBanner = new LinkedList<>();
        String buildVersion = "";
        List<String> bannerVersions = new LinkedList<>();

        /*
        load banner
         */
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("banner.txt");
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            br.lines().forEach(frameworkBanner::add);
        } else {
            LOGGER.debug("Could not read banner");
        }

        /*
        get versions info
         */
        bannerVersions.add("build.java.version: " + buildInformation.buildJavaVersion);
        bannerVersions.add("build.os.name:      " + buildInformation.buildOsName);
        bannerVersions.add("build.os.arch:      " + buildInformation.buildOsArch);
        bannerVersions.add("build.os.version:   " + buildInformation.buildOsVersion);
        bannerVersions.add("build.user.name:    " + buildInformation.buildUserName);
        bannerVersions.add("build.timestamp:    " + buildInformation.buildTimestamp);
        bannerVersions = bannerVersions.stream().map(s -> " " + s + " ").collect(Collectors.toList());
        buildVersion = buildInformation.buildVersion;

        /*
        beautify
         */
        String wall = "#";
        final int widthLogo = frameworkBanner.stream().mapToInt(String::length).max().getAsInt();
        frameworkBanner = frameworkBanner.stream().map(s -> s + StringUtils.repeat(" ", widthLogo - s.length())).collect(Collectors.toList());
        final int width = bannerVersions.stream().mapToInt(String::length).max().getAsInt();
        frameworkBanner = frameworkBanner.stream().map(s -> wall + StringUtils.center(s, width) + wall).collect(Collectors.toList());
        buildVersion = wall + StringUtils.center(buildVersion, width) + wall;
        bannerVersions = bannerVersions.stream().map(s -> wall + s + StringUtils.repeat(" ", width - s.length()) + wall).collect(Collectors.toList());

        /*
        print banner
         */
        String ruler = StringUtils.repeat(wall, width / wall.length() + 2);
        LOGGER.info(ruler);
        frameworkBanner.forEach(LOGGER::info);
        LOGGER.info(buildVersion);
        LOGGER.info(ruler);
        bannerVersions.forEach(LOGGER::info);
        LOGGER.info(ruler);
    }

}
