package io.testerra.report.test.pages.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtils {

    public static Matcher createMatcherForRegExp(final RegExp standardRegExp, final String evaluationString) {

        Pattern pattern = Pattern.compile(standardRegExp.getPatternString());
        return pattern.matcher(evaluationString);
    }

    public static String getRegExpResultOfString(final RegExp standardRegExp, String line) {

        final Matcher matcher = createMatcherForRegExp(standardRegExp, line);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }


    public enum RegExp {
        DIGITS_ONLY("\\d+"),
        LINE_BREAK("\\d+.+");

        private String patternString;

        RegExp(final String patternString) {
            this.patternString = patternString;
        }

        public String getPatternString() {
            return this.patternString;
        }

        public void setPatternString(final String valueToSet) {
            this.patternString=valueToSet;
        }

    }
}
