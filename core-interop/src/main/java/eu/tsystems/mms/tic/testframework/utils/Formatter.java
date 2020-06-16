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
 *
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.testng.ITestNGMethod;

import java.util.Date;

/**
 * General formatter for everything
 * @author Mike Reiche
 */
public interface Formatter {
    default String toString(Rectangle rectangle) {
        return String.format("(left: %d, top: %d, right: %d, bottom: %d)", rectangle.x, rectangle.y, rectangle.x+rectangle.width,rectangle.y+rectangle.height);
    }
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

    default String byToXPath(By by) {
        String[] split = by.toString().split("\\: ", 2);
        if (split[0].startsWith("By.name")) {
            return String.format("//*[@name='%s']", split[1]);
        } else if (split[0].startsWith("By.className")) {
            return String.format("//*[@class='%s']", split[1]);
        } else if (split[0].startsWith("By.Id")) {
            return String.format("//*[@id='%s']", split[1]);
        } else if (split[0].startsWith("By.xpath")) {
            return split[1];
        }
        return "";
    }
}
