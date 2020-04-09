package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import org.testng.ITestNGMethod;

import java.util.Date;

/**
 * General formatter for everything
 * @author Mike Reiche
 */
public interface Formatter {
    default String cutString(String string, int maxLength) {
        return cutString(string, maxLength, "...");
    }
    default String cutString(String string, int maxLength, String replacement) {
        if (string.length()>maxLength) {
            int first = Math.round((maxLength-replacement.length())/2);
            int second = string.length()-first;
            return String.format("%s%s%s", string.substring(0, first), replacement, string.substring(second));
        } else {
            return string;
        }
    }
    default String DATE_TIME_FORMAT() {
        return "dd.MM.yyyy-HH:mm:ss.SSS";
    }
    String logTime(Date date);

    default String toString(ITestNGMethod method) {
        StringBuilder sb = new StringBuilder();
        sb
            .append((method.isTest()?"Test":"Configuration"))
            .append("(")
            .append(method.getTestClass().getName().replace(TesterraCommons.FRAMEWORK_PACKAGE+".", ""))
            .append(".")
            .append(method.getMethodName())
            .append(")");
        return sb.toString();
    }
}
