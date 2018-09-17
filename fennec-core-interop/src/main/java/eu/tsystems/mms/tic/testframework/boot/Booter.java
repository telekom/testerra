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
package eu.tsystems.mms.tic.testframework.boot;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.fennecCommons;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.fennecUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Booter {

    private static final Logger LOGGER;
    private static final List<ModuleHook> MODULE_HOOKS = new LinkedList<>();

    static {
        fennecCommons.init();
        LOGGER = LoggerFactory.getLogger(Booter.class);
        fennecUtils.prepareAsserts();
        // when logger is configured:
        printfennecBanner();
        initHooks();
    }

    public static void bootOnce() {}

    /**
     * Prints fennec build informations.
     */
    private static void printfennecBanner() {
        List<String> bannerfennec = new LinkedList<>();
        String fennecVersion = "";
        List<String> bannerVersions = new LinkedList<>();

        /*
        load banner
         */
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("banner.txt");
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            br.lines().forEach(bannerfennec::add);
        }
        else {
            LOGGER.debug("Could not read banner");
        }

        /*
        get versions info
         */
        PropertyManager.loadProperties("fennec-build.properties");
        String[] infos = {
                "build.java.version",
                "build.os.name",
                "build.os.arch",
                "build.os.version",
                "build.user.name",
                "build.timestamp"
        };
        for (String key : infos) {
            String value = PropertyManager.getProperty(key);
            System.setProperty(key, value);
            bannerVersions.add(key + ": " + value);
        }
        fennecVersion = PropertyManager.getProperty("fennec.version");

        /*
        beautify
         */
        String wall = " ° ";
        final int widthLogo = bannerfennec.stream().mapToInt(String::length).max().getAsInt();
        bannerfennec = bannerfennec.stream().map(s -> s + StringUtils.repeat(" ", widthLogo - s.length())).collect(Collectors.toList());
        final int width = bannerVersions.stream().mapToInt(String::length).max().getAsInt();
        bannerfennec = bannerfennec.stream().map(s -> wall + StringUtils.center(s, width) + wall).collect(Collectors.toList());
        fennecVersion = wall + StringUtils.center(fennecVersion, width) + wall;
        bannerVersions = bannerVersions.stream().map(s -> wall + s + StringUtils.repeat(" ", width - s.length()) + wall).collect(Collectors.toList());

        /*
        print banner
         */
        String ruler = StringUtils.repeat(wall, width / wall.length() + 2);
        LOGGER.info(ruler);
        bannerfennec.forEach(LOGGER::info);
        LOGGER.info(fennecVersion);
        LOGGER.info(ruler);
        bannerVersions.forEach(LOGGER::info);
        LOGGER.info(ruler);
    }

    private static void initHooks() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addClassLoader(Thread.currentThread().getContextClassLoader());
        configurationBuilder.forPackages("eu.tsystems.mms.tic");

        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<? extends ModuleHook>> hooks = reflections.getSubTypesOf(ModuleHook.class);
        if (hooks.isEmpty()) {
            LOGGER.info("No Init Hooks found");
        }

        final String startMarker = "******************* ";
        final String endMarker = "################### ";

        hooks.forEach(aClass -> {
            try {
                ModuleHook moduleHook = aClass.newInstance();
                LOGGER.info(startMarker + "Calling Init Hook " + aClass.getSimpleName() + "...");
                moduleHook.init();
                MODULE_HOOKS.add(moduleHook);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(startMarker + "Could not load Init Hook " + aClass.getSimpleName());
            }
        });

        LOGGER.info(endMarker + "Done processing Init Hooks.");
    }

    public static void shutdown() {
        MODULE_HOOKS.forEach(moduleHook -> {
            LOGGER.info("Shutting down " + moduleHook.getClass().getSimpleName());
            moduleHook.terminate();
        });
    }
}
