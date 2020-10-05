/*
 * Testerra
 *
 * (C) 2020, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.layout.core;

/**
 * User: rnhb
 * Date: 16.05.14
 */
public enum Corners {
    UPPER_LEFT(0),
    UPPER_RIGHT(1),
    LOWER_RIGHT(2),
    LOWER_LEFT(3);

    public final int i;

    private Corners(int index) {
        this.i = index;
    }

    public static String getName(int index) {
        for (Corners p : Corners.values()) {
            if (p.i == index) {
                return p.name().toLowerCase();
            }
        }
        return "";
    }
}
