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
package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.transfer.BooleanPackedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Path Parser utility class.
 * <p/>
 * Created by peter on 01.09.14.
 */
public final class PropertiesParser {

    private static final Pattern patternReplace = Pattern.compile("\\{[^\\}]*\\}");
    private static final String REGEX_SENSIBLE = "@SENSIBLE@";
    private static final Pattern PATTERN_SENSIBLE = Pattern.compile(REGEX_SENSIBLE);

    private PropertiesParser() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesParser.class);

    /**
     * Parses a property and searches for testerra properties replacement marks: "{...}".
     * These marks will then be replaced by the property (if defined).
     * The replacement is recursive and has a loop detection.
     * Do the initial call with: parseLine(p, null).
     *
     * @param line            the current line.
     * @param searchedStrings a list of replacement marks already replaced (loop detection).
     * @return the line with substitutions.
     */
    private static String parseLine(String line, List<String> searchedStrings) {
        if (searchedStrings == null) {
            searchedStrings = new ArrayList<String>(1);
        }

        Matcher matcherReplace = patternReplace.matcher(line);
        List<String> matches = new ArrayList<String>(1);
        while (matcherReplace.find()) {
            String match = line.substring(matcherReplace.start(), matcherReplace.end());
            matches.add(match);
        }
        for (String match : matches) {
            String key = match.substring(1, match.length() - 1);

            /*
            Check for loop
             */
            if (searchedStrings.contains(key)) {
                throw new TesterraSystemException("Loop detected while replacing a property: " + match);
            } else {
                /*
                ask
                 */
                String value = pGetPrioritizedProperty(key);

                if (value == null) {
                    LOGGER.warn("Property " + match + " not found");
                } else {
                    // look if SENSIBLE
                    final BooleanPackedResponse<String> response = findAndVoidSENSIBLETag(value);
                    value = response.getResponse();
                    boolean sensible = response.getBoolean();

                    // 1. remember the key because it was replaced
                    List<String> listCopy = new ArrayList<String>(searchedStrings.size() + 1);
                    listCopy.addAll(searchedStrings);
                    // 2. check recursive replacements
                    listCopy.add(key);
                    value = parseLine(value, listCopy);
                    // 3. finally replace
                    line = line.replace(match, value);

                    if (sensible) {
                        value = "###########";
                    }
                    LOGGER.info("Replacing " + match + " with >" + value + "<");
                }
            }
        }
        return line;
    }

    private static BooleanPackedResponse<String> findAndVoidSENSIBLETag(String value) {
        if (value == null) {
            return new BooleanPackedResponse<String>(value, false);
        }
        final Matcher matcherSensible = PATTERN_SENSIBLE.matcher(value);
        if (matcherSensible.find()) {
            value = value.replaceAll(REGEX_SENSIBLE, "");
            return new BooleanPackedResponse<String>(value, true);
        }
        return new BooleanPackedResponse<String>(value, false);
    }

    /**
     * Parses a property and searches for properties replacement marks: "{...}".
     * These marks will then be replaced by the property (if defined).
     * The replacement is recursive and has a loop detection.
     *
     * @param line the current line.
     * @return the line with substitutions.
     */
    public static String parseLine(String line) {
        return parseLine(line, null);
    }

    static String getParsedPropertyStringValue(String key) {
        String value = pGetPrioritizedProperty(key);

        // replace marked system properties in this value (bla_{huhu} to bla_blubb if huhu=blubb)
        if (value != null) {
            value = parseLine(value);
        }

        /*
        replace residual control tags
         */
        final BooleanPackedResponse<String> response = findAndVoidSENSIBLETag(value);
        value = response.getResponse();

        return value;
    }

    private static String pGetPrioritizedProperty(String key) {
        // load static property (by default loaded from test.properties)
        String value = PropertyManager.FILEPROPERTIES.getProperty(key);

        // overload by system property
        value = System.getProperty(key, value);

        // load default properties
        value = PropertyManager.GLOBALPROPERTIES.getProperty(key, value);

        // overload by threadlocal
        if (PropertyManager.THREAD_LOCAL_PROPERTIES.get() != null) {
            value = PropertyManager.THREAD_LOCAL_PROPERTIES.get().getProperty(key, value);
        }

        return value;
    }

}
