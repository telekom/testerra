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
package eu.tsystems.mms.tic.testframework.layout;

public class DefaultParameter {
    public static final double LAYOUTCHECK_MATCH_THRESHOLD = 0.95d;
    public static final int LAYOUTCHECK_DISPLACEMENT_THRESHOLD = 5;
    public static final int LAYOUTCHECK_INTRA_GROUPING_THRESHOLD = 5;
    public static final int LAYOUTCHECK_MINIMUM_MARKED_PIXELS = 12;
    public static final double LAYOUTCHECK_MAXIMUM_MARKED_PIXELS_RATIO = 0.04d;
    public static final int LAYOUTCHECK_MIN_MATCH_DISTANCE = 5;
    public static final int LAYOUTCHECK_MIN_SIZE_DIFFERENCE_SUB_IMAGES = 10;
    public static final double LAYOUTCHECK_MIN_SIMULAR_MOVEMENT_ERRORS = 0.51d;
    public static final double LAYOUTCHECK_DISTANCE_MULTIPLE_MATCHES = 14;
}