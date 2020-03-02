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
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sammlung von StringUtils.
 */
public final class StringUtils extends  org.apache.commons.lang3.StringUtils {

    /** Generator für Pseudozufallszahlen. */
    private static final Random GENERATOR = new Random();

    /** Private Konstruktor. Wird nicht gerufen. */
    /** Regexp-Pattern eine HTML-Entity. */
    private static final Pattern HTML_ENTITY = Pattern.compile("&([a-zA-Z]{4}|(#[0-9]{1,4}));");

    /** Sammlung von speziellen Sonderzeichen, Umlauten etc. */
    private static final Map<String, String> HTML_ENTITIES = new HashMap<String, String>();
    static {
        // CHECKSTYLE:OFF
        HTML_ENTITIES.put("&uuml;", "ü");
        // CHECKSTYLE:ON
        HTML_ENTITIES.put("&#174;", "®");
        HTML_ENTITIES.put("&#38;", "&");
        // ignorieren, Selenium hat damit Probleme
        HTML_ENTITIES.put("&nbsp;", "&nbsp;");
    }

    /**
     * Pattern matched auf alle HTML Tags.
     */
    public static final Pattern ALLHTMLTAGS = Pattern
            .compile("</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|\'.*?\'|[^\'\">\\s]+))?)+\\s*|\\s*)/?>");

    /** Private Konstruktor. Wird nicht gerufen. */
    private StringUtils() {
    }

    /**
     * Util-Methode zum Entfernen alle HTML Tags aus einem String.
     *
     * @param input Der zu untersuchende HTML-String
     * @return Der gestrippte String
     */
    private static String stripHtml(final String input) {
        return input.replaceAll("\\<.*?\\>", "");
    }

    /**
     * Util Methode, die alle Tabulatoren, Zeichenumbrüche und HTML Tags aus einem String entfernt.
     *
     * @param input Der zu untersuchende Text.
     * @return txt Der Plaintext.
     */
    public static String getPlainTextFromHTML(final String input) {
        String txt = stripHtml(input.replaceAll("[\\n\\r\\t]+", " ").trim());
        final String orig = txt;
        final Matcher matcher = StringUtils.HTML_ENTITY.matcher(orig);

        while (matcher.find()) {
            final String found = orig.substring(matcher.start(), matcher.end());
            txt = txt.replaceAll(found, StringUtils.HTML_ENTITIES.get(found));
        }

        return txt;
    }

    /**
     * Hilfsmethode zum String konkatinieren.
     *
     * @param strings Die Strings als Liste.
     * @return string Der zusammengesetzte String.
     */
    public static String concat(final String... strings) {
        final StringBuilder strBuffer = new StringBuilder();
        for (String s : strings) {
            strBuffer.append(s);
        }

        return strBuffer.toString();
    }

    /**
     * Hilfsmethode zum String konkatinieren.
     *
     * @param spacer Spacer.
     * @param objects Die Objetcs als Liste.
     * @return string Der zusammengesetzte String.
     */
    public static String concat(final char spacer, final Object... objects) {
        String out = "";
        for (Object object : objects) {
            out += object + "" + spacer;
        }
        // remove last spacer
        out = out.substring(0, out.length() - 1);
        return out;
    }

    /**
     * Hilfsmethode zum String konkatinieren.
     *
     * @param spacer Spacer.
     * @param objects Die Objetcs als Liste.
     * @return string Der zusammengesetzte String.
     */
    public static String concat(final String spacer, final Object... objects) {
        String out = "";
        for (Object object : objects) {
            out += object + "" + spacer;
        }
        // remove last spacer
        out = out.substring(0, out.length() - spacer.length());
        return out;
    }

    /**
     * Die Funktion liefert einen zufälligen String der Länge length. Es handelt sich um reguläre Sonderzeichen.
     *
     * @param length Länge des Strings
     * @return zufälliger String
     */
    public static String getRandomStringLowerCaseWithLength(final int length) {
        return getRandomStringWithLength(length).toLowerCase();
    }

    /**
     * Die Funktion liefert einen zufälligen String der Länge length. Es handelt sich um Buchstaben A-Z und a-z.
     *
     * @param length Länge des Strings
     * @return zufälliger String
     */
    public static String getRandomStringWithLength(final int length) {
        final StringBuffer strbuffer = new StringBuffer(length);

        for (int i = 0; i < length; i++) {
            strbuffer.append(Character.toString((char) (Math.random() * ('Z' - 'A') + 'A')));
        }

        return strbuffer.toString();
    }

    /**
     * Die Funktion liefert einen zufälligen String der Länge length. Es handelt sich um Zahlen.
     *
     * @param length Länge des Strings
     * @return zufälliger String
     */
    public static String getRandomStringOfNumbersWithLength(final int length) {
        final StringBuffer strbuffer = new StringBuffer(length);

        for (int i = 0; i < length; i++) {
            strbuffer.append(Character.toString((char) (Math.random() * ('9' - '0') + '0')));
        }

        return strbuffer.toString();
    }

    /**
     * Die Funktion liefert einen zufälligen String der Länge length. Es handelt sich um reguläre Sonderzeichen.
     *
     * @param length Länge des Strings
     * @return zufälliger String
     */
    public static String getRandomSpecialCharacterStringWithLength(final int length) {
        final StringBuffer strBuffer = new StringBuffer(length);

        for (int i = 0; i < length; i++) {

            switch (StringUtils.GENERATOR.nextInt(4)) {
            case 0:
                strBuffer.append(Character.toString((char) (Math.random() * ('&' - '!') + '!')));
                break;
            case 1:
                strBuffer.append(Character.toString((char) (Math.random() * ('@' - ':') + ':')));
                break;
            case 2:
                strBuffer.append(Character.toString((char) (Math.random() * ('`' - '[') + '[')));
                break;
            case 3:
                strBuffer.append(Character.toString((char) (Math.random() * ('/' - '(') + '(')));
                break;
            default:
                strBuffer.append(Character.toString((char) (Math.random() * ('~' - '{') + '{')));
                break;
            }
        }

        return strBuffer.toString();
    }

    /**
     * Entfernt aus einem Selektor String den anstehenden Typ Präfix.
     *
     * @param selektor Selektor.
     * @return selektor ohne Präfix.
     */
    public static String stripSelektorType(final String selektor) {

        String stripped = selektor;
        if (selektor != null) {
            if (selektor.startsWith("css=")) {
                stripped = selektor.replace("css=", "");
            } else if (selektor.startsWith("xpath=")) {
                stripped = selektor.replace("xpath=", "");
            }
        }
        return stripped;
    }

    /**
     * Entfernt das "&" sowie den Platzhalter.
     *
     * @param selektor Selektor
     * @return Selektor ohne "&" oder Platzhalter
     */
    public static String trimTemplateSelektor(final String selektor) {

        String trimmed = selektor;
        if (selektor != null) {
            if (selektor.endsWith("%s&']")) {
                trimmed = selektor.replace("%s&", "");
            }
            trimmed = StringUtils.formatStr(selektor, "");
        }
        return trimmed;

    }

    /**
     * Enthält ein Selektor eine maxlength information, so wird sie mit dieser Funktion entfernt.
     *
     * @param selector Selektor mit maxlength Information.
     * @return Selektor ohne maxlength Information.
     */
    public static String extractLengthInformationFromSelector(final String selector) {
        return selector.replaceAll("\\[maxlength='[0-9]+'\\]", "");
    }

    /**
     * Formates a template with given bugIds.
     *
     * @param formatStr template
     * @param replaceID replace bugIds
     * @return formattes String
     */
    public static String formatStr(final String formatStr, final String... replaceID) {
        return String.format(formatStr, (Object[]) replaceID);
    }

    /**
     * Hilfsmethode welche einen String entgegen nimmt und überprüft ob dieser zu einem Integer geparsed werden kann.
     *
     * @param input Stringeingabe.
     * @return true wenn parsebar, sonst false.
     */
    public static boolean tryParse(final String input) {

        boolean isParseable = true;
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            isParseable = false;
        }
        return isParseable;
    }

    /**
     * Returns true if string==null or string.length==0.
     *
     * @param string .
     * @return .
     */
    public static boolean isStringEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Returns true if string==null or string.length==0.
     *
     * @param strings .
     * @return .
     */
    public static boolean isAnyStringEmpty(final String... strings) {
        if (strings == null) {
            return true;
        }
        for (String string : strings) {
            if (isStringEmpty(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces all symbols, that could destroy the html report.
     *
     * @param input String to clean from html symbols.
     * @return Cleaned String.
     */
    public static String prepareStringForHTML(final String input) {
        if (input == null) {
            return "";
        }
        return input
                //                .replaceAll("&amp;", "&")
                //                .replaceAll("&lt;", "<")
                //                .replaceAll("&gt;", ">")
                //                .replaceAll("&quot;", "\"")
                //                .replaceAll("", "\r")
                //                .replaceAll("<br/>", "\n")
                //
                //                .replaceAll("&Auml;", "\u00c4") // Ä
                //                .replaceAll("&Ouml;", "\u00d6") // Ö
                //                .replaceAll("&Uuml;", "\u00dc") // Ü
                //                .replaceAll("&auml;", "\u00e4") // ä
                //                .replaceAll("&ouml;", "\u00f6") // ö
                //                .replaceAll("&uuml;", "\u00fc") // ü
                //
                //                .replaceAll("&szlig;", "\u00df") // ß

                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\r", "")
                .replaceAll("\n", "<br/>")

                .replaceAll("\u00c4", "&Auml;") // Ä
                .replaceAll("\u00d6", "&Ouml;") // Ö
                .replaceAll("\u00dc", "&Uuml;") // Ü
                .replaceAll("\u00e4", "&auml;") // a
                .replaceAll("\u00f6", "&ouml;") // ö
                .replaceAll("\u00fc", "&uuml;") // ü

                .replaceAll("\u00df", "&szlig;"); // ß
    }

    /**
     * Splits the input string and checks every single char against the regex. If it doesn't match, it will be
     * replaced by the replacement string.
     * Illegal chars regex could be: \d|\w
     * This would allow only numbers and letters and no special characters.
     *
     * @param in
     * @param allowedCharacterRegex
     * @return
     */
    public static String removeIllegalCharacters(final String in, final String allowedCharacterRegex,
                                                 String replacement) {
        if (isAnyStringEmpty(in, allowedCharacterRegex)) {
            return in;
        }
        if (replacement == null) {
            replacement = "";
        }

        String out = "";
        Pattern pattern = Pattern.compile(allowedCharacterRegex);

        char[] chars = in.toCharArray();
        for (char aChar : chars) {
            Matcher matcher = pattern.matcher(aChar + "");
            if (matcher.find()) {
                out += aChar;
            }
            else {
                out += replacement;
            }
        }
        return out;
    }

    public static String enhanceList(String list, String value, String seperator, boolean spaceAfterSeperator) {
        String out = list;
        if (isStringEmpty(value)) {
            return list;
        }
        if (seperator == null) {
            seperator = "";
        }

        if (!isStringEmpty(list)) {
            out += seperator;
            if (spaceAfterSeperator) {
                out += " ";
            }
            out += value;
        }
        else {
            out = value;
        }

        return out;
    }

    /**
     * Replaces all characters not matching \d|\w with _.
     *
     * @param in String input.
     * @return legalized output.
     */
    public static String legalizeString(final String in) {
        return removeIllegalCharacters(in, "\\d|\\w", "_");
    }

    public static boolean equalsWithNull(String... strings) {
        if (strings == null || strings.length <= 1) {
            // nothing AND really nothing AND 1 String is OK
            return true;
        }

        String first = strings[0];

        /*
        null check
         */
        if (first == null) {
            for (String string : strings) {
                if (string != null) {
                    return false;
                }
            }

            return true;
        }

        /*
        string equals
         */
        for (String string : strings) {
            if (!first.equals(string)) {
                return false;
            }
        }

        return true;
    }

    public static String getFirstValidString(String... s) {
        if (s == null || s.length == 0) {
            return null;
        }

        for (String string : s) {
            if (string != null) {
                return string;
            }
        }

        return null;
    }

    public static boolean containsAll(String input, boolean ignoreCase, String... contains) {
        if (input == null) {
            return false;
        }

        if (ignoreCase) {
            input = input.toLowerCase();
        }

        for (String s : contains) {
            if (ignoreCase) {
                s = s.toLowerCase();
            }

            if (!input.contains(s)) {
                return false;
            }
        }

        return true;
    }
}
