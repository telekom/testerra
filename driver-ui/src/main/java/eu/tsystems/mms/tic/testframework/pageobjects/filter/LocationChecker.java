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
 package eu.tsystems.mms.tic.testframework.pageobjects.filter;

abstract class LocationChecker {
    abstract boolean check(int x, int y);

    static class Is extends LocationChecker {
        private int expectedX, expectedY;

        public Is(int expectedX, int expectedY) {
            this.expectedX = expectedX;
            this.expectedY = expectedY;
        }

        @Override
        boolean check(int x, int y) {
            return x == expectedX && y == expectedY;
        }

        @Override
        public String toString() {
            return "%s=[" + expectedX + ", " + expectedY + "]";
        }
    }

    static class IsBetween extends LocationChecker {
        private int minX, minY, maxX, maxY;

        public IsBetween(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        @Override
        boolean check(int x, int y) {
            return x >= minX && y >= minY && x <= maxX && y <= maxY;
        }

        @Override
        public String toString() {
            return "%1$s > [" + minX + ", " + minY + "] && %1$s < [" + maxX + ", " + maxY + "]";
        }
    }

}
