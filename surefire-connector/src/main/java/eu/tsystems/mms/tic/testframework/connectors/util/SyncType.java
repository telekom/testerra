/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.connectors.util;

/**
 * Enum contains names for syncType Numbers.
 *
 * @author mrgi
 */
public enum SyncType {

    /**
     * Names of the syncTypes.
     */
    ANNOTATION(3);

    /**
     * The number of syncType
     */
    private int number;

    /**
     * Constructor to initialize the number.
     *
     * @param number The number of syncType
     */
    SyncType(final int number) {
        this.number = number;
    }

    /**
     * gets the syncMethod of syncType
     *
     * @return annotation of sync type
     */
    public static SyncType getSyncMethod() {

        return SyncType.ANNOTATION;
    }

    /**
     * Get the number of the syncType.
     *
     * @return .
     */
    public int getNumber() {
        return number;
    }
}
