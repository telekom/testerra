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
package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by piet on 02.12.16.
 */
final class ClassFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassFinder.class);

    private static final String SCHEMA_DIV = "_";
    private static final String KEYWORD_PIXEL = "px";
    private static final String KEYWORD_RES = "Res";
    private static final String KEYWORD_MIN = "Min";
    private static final String KEYWORD_MAX = "Max";
    private static final String RESOLUTION_REGEX = "\\d+" + KEYWORD_PIXEL;

    private static final String PATTERN_LOW = SCHEMA_DIV + KEYWORD_MIN + SCHEMA_DIV + RESOLUTION_REGEX;
    private static final String PATTERN_MID = SCHEMA_DIV + RESOLUTION_REGEX + SCHEMA_DIV + RESOLUTION_REGEX;
    private static final String PATTERN_HI = SCHEMA_DIV + RESOLUTION_REGEX + SCHEMA_DIV + KEYWORD_MAX;
    private static final String PATTERN_RES = "TODO"; // TODO

    private static final String PROJECT_PACKAGE = PropertyManager.getProperty(FennecProperties.PROJECT_PACKAGE,
            FennecCommons.DEFAULT_PACKAGE_NAME);

    private static class Caches {

        private static final String NULL_PAGE_PREFIX = " _ ";

        private static final Map<Class<? extends Page>, Map<String, PrioritizedClassInfos<? extends Page>>> IMPLEMENTATIONS_CACHE = new ConcurrentHashMap<>();

        private static PrioritizedClassInfos getCache(Class<? extends Page> pageClass, String prefixOrNull) {
            if (StringUtils.isStringEmpty(prefixOrNull)) {
                prefixOrNull = NULL_PAGE_PREFIX;
            }

            synchronized (IMPLEMENTATIONS_CACHE) {
                if (!IMPLEMENTATIONS_CACHE.containsKey(pageClass)) {
                    return null;
                }

                Map<String, PrioritizedClassInfos<? extends Page>> map = IMPLEMENTATIONS_CACHE.get(pageClass);
                if (!map.containsKey(prefixOrNull)) {
                    return null;
                }

                return map.get(prefixOrNull);
            }
        }

        private static void setCache(Class<? extends Page> pageClass, String prefixOrNull, PrioritizedClassInfos<? extends Page> prioritizedClassInfos) {
            if (StringUtils.isStringEmpty(prefixOrNull)) {
                prefixOrNull = NULL_PAGE_PREFIX;
            }

            synchronized (IMPLEMENTATIONS_CACHE) {
                if (!IMPLEMENTATIONS_CACHE.containsKey(pageClass)) {
                    IMPLEMENTATIONS_CACHE.put(pageClass, new HashMap<>());
                }
                Map<String, PrioritizedClassInfos<? extends Page>> map = IMPLEMENTATIONS_CACHE.get(pageClass);

                map.put(prefixOrNull, prioritizedClassInfos);
            }
        }
    }

    /**
     * Gets all matching subclasses of the given class. Matching means the subclasses name must consist of the name of the
     * parent class followed by an integer number representing the minimum width and nothing else.
     *
     * @param baseClass The base page class
     */
    @SuppressWarnings("unchecked")
    private static <T extends Page> void findSubPagesOf(final Class<T> baseClass, String prefix) {
        LOGGER.debug(String.format("Searching for subtypes of class <%s>", baseClass));

        if (prefix == null) {
            prefix = "";
        }

        final Reflections reflections = new Reflections(PROJECT_PACKAGE);
        final String baseClassName = baseClass.getSimpleName();
        final String prefixedClassName = prefix + baseClassName;

        PrioritizedClassInfos<T> prioritizedClassInfos = new PrioritizedClassInfos<>();

        // at first, add the base page it self, only if not abstract
        if (!Modifier.isAbstract(baseClass.getModifiers())) {
            prioritizedClassInfos.setBaseClass(baseClass);
        }

        // search for sub pages
        Set<? extends Class<T>> subClasses = reflections.getSubTypesOf((Class) baseClass);
        for (Class<T> subClass : subClasses) {
            String classname = subClass.getSimpleName();

            if (Modifier.isAbstract(subClass.getModifiers())) {
                LOGGER.debug("Not taking " + classname + " into consideration, because it is abstract");
            } else {
                tryToFindImplementationOf(subClass, classname, baseClassName, prefix, prioritizedClassInfos);
            }
        }

        Caches.setCache(baseClass, prefix, prioritizedClassInfos);
    }

    private static <T extends Page> void tryToFindImplementationOf(Class<T> subClass, String classname,
                                                                   String baseClassName, String prefix,
                                                                   PrioritizedClassInfos<T> prioritizedClassInfos) {
        if (classname.startsWith(prefix + baseClassName)) {
            String resPart = classname.replace(prefix + baseClassName, "");
            if (matchesOurAnyOfPatterns(resPart)) {
                // prefixed classes with existing and matching res part
                prioritizedClassInfos.getPrefixedClasses().add(new ResolutionClassInfo<>(subClass, resPart));
            }
            else if (StringUtils.isStringEmpty(resPart)) {
                // prefixed classes without res part
                prioritizedClassInfos.setPrefixedBaseClass(subClass);
            }
        }
        else if (classname.startsWith(baseClassName)) {
            String resPart = classname.replace(baseClassName, "");
            if (matchesOurAnyOfPatterns(resPart)) {
                // non-prefixed classes with existing and matching res part
                prioritizedClassInfos.getNonPrefixedClasses().add(new ResolutionClassInfo<>(subClass, resPart));
            }
        }
    }

    private static boolean matchesOurAnyOfPatterns(String resPartOfClassName) {
        if (StringUtils.isStringEmpty(resPartOfClassName)) {
            return false;
        }
        if (    resPartOfClassName.matches(PATTERN_LOW) ||
                resPartOfClassName.matches(PATTERN_MID) ||
                resPartOfClassName.matches(PATTERN_HI) ||
                resPartOfClassName.matches(PATTERN_RES)
                ) {
            return true;
        }
        return false;
    }

    /**
     * Gets the browsers viewport width.
     *
     * @param driver The web driver
     * @return int
     */
    private static int getBrowserViewportSize(final WebDriver driver, final int viewportWidth) {
        if (viewportWidth > 0) {
            // mehrfaches auslesen verhindern, nur auslesen wenn nötig
            return viewportWidth;
        }
        Object o = JSUtils.executeScript(driver, "return window.innerWidth");
        if (o instanceof Long) {
            int viewportWidthNew = (int) (long) (Long) o;
            LOGGER.debug(String.format("Browser viewport width is %dpx", viewportWidthNew));
            return viewportWidthNew;
        }
        return viewportWidth;
    }

    @SuppressWarnings("unchecked")
    static <T extends Page> Class<T> getBestMatchingClass(Class<T> baseClass, WebDriver driver, String prefix) {
        PrioritizedClassInfos<? extends Page> prioritizedClassInfos = Caches.getCache(baseClass, prefix);

        if (prioritizedClassInfos == null) {
            // scan!
            findSubPagesOf(baseClass, prefix);
            prioritizedClassInfos = Caches.getCache(baseClass, prefix);

            if (prioritizedClassInfos == null) {
                throw new FennecSystemException("Something went wrong scanning this class for sub types: " + baseClass.getName());
            }
        }

        /*
        log map
         */
        prioritizedClassInfos.logContent();

        /*
        find best matching class
         */
        List<? extends ResolutionClassInfo<? extends Page>> prefixedClasses = prioritizedClassInfos.getPrefixedClasses();
        List<? extends ResolutionClassInfo<? extends Page>> nonPrexidClasses = prioritizedClassInfos.getNonPrefixedClasses();
        int viewPortWidth = -1;
        Class<T> bestMatchingClass = null;

        // check prefixed
        for (ResolutionClassInfo<? extends Page> c : prefixedClasses) {
            if (c.hasResolution()) {
                viewPortWidth = getBrowserViewportSize(driver, viewPortWidth);
                if (c.matchesResolution(viewPortWidth)) {
                    bestMatchingClass = (Class<T>) c.implClass;
                }
            }
        }

        // prefixedBase
        if (bestMatchingClass == null) {
            if (prioritizedClassInfos.prefixedBaseClass != null) {
                bestMatchingClass = (Class<T>) prioritizedClassInfos.prefixedBaseClass;
            }
        }

        // check non-prefixed
        if (bestMatchingClass == null) {
            for (ResolutionClassInfo<? extends Page> c : nonPrexidClasses) {
                if (c.hasResolution()) {
                    viewPortWidth = getBrowserViewportSize(driver, viewPortWidth);
                }
                if (c.matchesResolution(viewPortWidth)) {
                    bestMatchingClass = (Class<T>) c.implClass;
                }
            }
        }

        // base class
        if (bestMatchingClass == null) {
            if (prioritizedClassInfos.baseClass!= null) {
                bestMatchingClass = (Class<T>) prioritizedClassInfos.baseClass;
            }
        }

        /*
        Evaluate
         */
        if (bestMatchingClass == null) {
            throw new FennecRuntimeException("Could not find a matching page class implementation for " + baseClass.getSimpleName() +
                    "\nMaybe you can solve this by making the base class non-abstract.");
        } else {
            if (viewPortWidth > 0) {
                LOGGER.debug("For " + viewPortWidth + "px view port width, I'm choosing: " + bestMatchingClass.getSimpleName());
            } else {
                LOGGER.debug("Without any viewport check, I'm choosing: " + bestMatchingClass.getSimpleName());
            }
            return bestMatchingClass;
        }
    }

    static void clearCache() {
        Caches.IMPLEMENTATIONS_CACHE.clear();
    }

    private static class PrioritizedClassInfos<T extends Page> {
        List<ResolutionClassInfo<T>> prefixedClasses = new LinkedList<>();
        Class<T> prefixedBaseClass;
        List<ResolutionClassInfo<T>> nonPrefixedClasses = new LinkedList<>();
        Class<T> baseClass;

        public List<ResolutionClassInfo<T>> getPrefixedClasses() {
            return prefixedClasses;
        }

        public List<ResolutionClassInfo<T>> getNonPrefixedClasses() {
            return nonPrefixedClasses;
        }

        public Class<T> getBaseClass() {
            return baseClass;
        }

        public void setBaseClass(Class<T> baseClass) {
            this.baseClass = baseClass;
        }

        public void setPrefixedBaseClass(Class<T> prefixedBaseClass) {
            this.prefixedBaseClass = prefixedBaseClass;
        }

        public void logContent() {
            String msg = "";
            if (prefixedClasses.size() > 0) {
                msg += "Prefixed Res Impls:\n";
                for (ResolutionClassInfo<T> c : prefixedClasses) {
                    msg += "  " + c + "\n";
                }
            }
            if (prefixedBaseClass != null) {
                msg += "Prefixed Base:\n";
                msg += "  " + prefixedBaseClass.getSimpleName() + "\n";
            }
            if (nonPrefixedClasses.size() > 0) {
                msg += "Res Impls:\n";
                for (ResolutionClassInfo<T> c : nonPrefixedClasses) {
                    msg += "  " + c + "\n";
                }
            }
            if (baseClass != null) {
                msg += "Base:\n";
                msg += "  " + baseClass.getSimpleName() + "\n";
            }

            if ("".equals(msg)) {
                msg += "No usable (non-abstract) implementations found.";
            }
            LOGGER.debug(msg);
        }
    }

    private static class ResolutionClassInfo<T extends Page> {
        int resLowerLimit = -1;
        int resUpperLimit = -1;

        Class<T> implClass;

        public ResolutionClassInfo(Class<T> implClass, String resPart) {
            this.implClass = implClass;
            grabResolutions(resPart);
        }

        private void grabResolutions(String resPart) {
            if (StringUtils.isStringEmpty(resPart)) {
                return;
            }

            String[] split = resPart.split(SCHEMA_DIV);
            String leftValue = split[1];
            String rightValue = split[2];
            if (KEYWORD_RES.equals(leftValue)) {
                // TODO
            }
            else {
                if (!KEYWORD_MIN.equals(leftValue)) {
                    resLowerLimit = Integer.valueOf(leftValue.replace(KEYWORD_PIXEL, ""));
                }

                if (!KEYWORD_MAX.equals(rightValue)) {
                    resUpperLimit = Integer.valueOf(rightValue.replace(KEYWORD_PIXEL, ""));
                }
            }
        }

        public boolean hasResolution() {
            if (resLowerLimit < 0 && resUpperLimit < 0) {
                return false;
            }
            return true;
        }

        public boolean matchesResolution(int resolution) {
            if (resolution < 1) {
                return true;
            }

            if (resLowerLimit < 0 && resUpperLimit < 0) {
                return true;
            }

            boolean lowMatches = false;
            boolean highMatches = false;

            if (resolution >= resLowerLimit) {
                lowMatches = true;
            }

            if (resUpperLimit < 0 || resolution <= resUpperLimit) {
                highMatches = true;
            }

            return lowMatches && highMatches;
        }

        @Override
        public String toString() {
            return implClass.getSimpleName();
        }
    }
}
