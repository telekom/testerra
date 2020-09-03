/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
 package eu.tsystems.mms.tic.testframework.boot;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.internal.TesterraBuildInformation;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Booter {

    private static final Logger LOGGER;
    private static final List<ModuleHook> MODULE_HOOKS = new LinkedList<>();

    static {
        TesterraCommons.init();
        LOGGER = LoggerFactory.getLogger(Booter.class);
        // when logger is configured:
        printTesterraBanner();
        initHooks();
        // log evidence collector
        TestEvidenceCollector.logInfo();
    }

    public static void bootOnce() {}

    /**
     * Prints testerra build information.
     */
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
        TesterraBuildInformation buildInformation = TesterraBuildInformation.getInstance();
        bannerVersions.add("build.java.version: " + buildInformation.buildJavaVersion);
        bannerVersions.add("build.os.name:      " + buildInformation.buildOsName);
        bannerVersions.add("build.os.arch:      " + buildInformation.buildOsArch);
        bannerVersions.add("build.os.version:   " + buildInformation.buildOsVersion);
        bannerVersions.add("build.user.name:    " + buildInformation.buildUserName);
        bannerVersions.add("build.timestamp:    " + buildInformation.buildTimestamp);

        buildVersion = buildInformation.buildVersion;

        /*
        beautify
         */
        String wall = " ° ";
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

    private static void initHooks() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addClassLoader(Thread.currentThread().getContextClassLoader());
        configurationBuilder.forPackages("eu.tsystems.mms.tic");

        final Reflections reflections = new Reflections(configurationBuilder);
        final Set<Class<? extends ModuleHook>> hooks = reflections.getSubTypesOf(ModuleHook.class);

        if (hooks.isEmpty()) {
            LOGGER.debug("No Init Hooks found");
        }

        // init hooks in alphabetical order to avoid random initialization
        hooks.stream()
                .sorted(Comparator.comparing(Class::getSimpleName))
                .forEach(aClass -> {
                    try {
                        final ModuleHook moduleHook = aClass.getConstructor().newInstance();
                        LOGGER.debug("Initialize " + ModuleHook.class.getSimpleName() + ": " + aClass.getSimpleName());
                        moduleHook.init();
                        MODULE_HOOKS.add(moduleHook);
                    } catch (Exception e) {
                        LOGGER.error("Could not load Init Hook " + aClass.getSimpleName());
                    }
                });
    }

    public static void shutdown() {
        MODULE_HOOKS.forEach(moduleHook -> {
            LOGGER.debug("Shutting down " + moduleHook.getClass().getSimpleName());
            moduleHook.terminate();
        });
    }
}
