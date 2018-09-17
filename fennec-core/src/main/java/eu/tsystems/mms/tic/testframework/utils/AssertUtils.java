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
package eu.tsystems.mms.tic.testframework.utils;

import org.testng.Assert;

/**
 * Created by pele on 05.01.2015.
 */
public final class AssertUtils {

    public static void assertContains(String actual, String expected) {
        assertContains(actual, expected, "String contains " + expected + "\nactual: " + actual);
    }

    public static void assertContains(String actual, String expected, String description) {
        Assert.assertTrue(actual.contains(expected), description);
    }

    public static void assertGreaterThan(long actual, long expected, String descriptionPart) {
        Assert.assertTrue(actual > expected, descriptionPart + " satisfies " + actual + " > " + expected);
    }

}
