/*
 * Testerra
 *
 * (C) 2023, Sebastian Kiehne, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.internal.asserts;

public interface ObjectAssertion<T> extends BinaryAssertion<T> {
    default boolean is(Object expected) {
        return is(expected, null);
    }

    boolean is(Object expected, String subject);

    default boolean isNot(Object expected) {
        return isNot(expected, null);
    }

    boolean isNot(Object expected, String subject);
}
