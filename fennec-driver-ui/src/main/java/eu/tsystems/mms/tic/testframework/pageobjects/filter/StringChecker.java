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
package eu.tsystems.mms.tic.testframework.pageobjects.filter;

/**
 * Created by rnhb on 27.07.2015.
 */
abstract class StringChecker {
    abstract boolean check(String expectedValue, String actualValue);

    static class Is extends StringChecker {
        @Override
        boolean check(String expectedValue, String actualValue) {
            if (expectedValue == null) {
                return actualValue == null;
            }
            return expectedValue.equals(actualValue);
        }

        @Override
        public String toString() {
            return "%s = \"%s\"";
        }
    }

    static class IsNot extends StringChecker {
        @Override
        boolean check(String expectedValue, String actualValue) {
            if (expectedValue == null) {
                return actualValue != null;
            }
            return !expectedValue.equals(actualValue);
        }

        @Override
        public String toString() {
            return "%s != \"%s\"";
        }
    }

    static class Contains extends StringChecker {
        @Override
        boolean check(String expectedValue, String actualValue) {
            if (actualValue == null) {
                return expectedValue == null;
            } else {
                // this is correct: the second string is the actual element text, the first is the text that's expected to be contained
                return actualValue.contains(expectedValue);
            }
        }

        @Override
        public String toString() {
            return "%s.contains(\"%s\")";
        }
    }

    static class ContainsNot extends StringChecker {
        @Override
        boolean check(String expectedValue, String actualValue) {
            if (actualValue == null) {
                return expectedValue != null;
            }
            return !actualValue.contains(expectedValue);
        }

        @Override
        public String toString() {
            return "%s.containsNot(\"%s\")";
        }
    }

    static class Exists extends StringChecker {
        @Override
        boolean check(String expectedValue, String actualValue) {
            //would be "" but not null
            // CSS would be "" and Attributes==null
            if(actualValue==null) {
                return false;
            }
            if(actualValue.equals("")) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "%s exists%s";
        }
    }

    static class ExistsNot extends StringChecker {
        @Override
        boolean check(String expectedValue, String actualValue) {
            if(actualValue==null) {
                return true;
            }
            if(actualValue.equals("")) { //example: check existence of id in a tag,then id is an empty string, and not null
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "%s exists not%s";
        }
    }
}
