/*
 * Testerra
 *
 * (C) 2024, Sebastian Kiehne, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

public class DefaultObjectAssertion<T> extends DefaultBinaryAssertion<T> implements ObjectAssertion<T> {

    public DefaultObjectAssertion(AbstractPropertyAssertion<T> parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(Object expected, String failMessage) {
        if (expected instanceof Boolean) {
            boolean expectedBoolean = (Boolean) expected;
            return this.is(expectedBoolean, failMessage);
        }
        return testSequence(
                provider,
                (actual) -> assertionImpl.equals(actual, expected),
                (actual) -> assertionImpl.formatExpectEquals(null, expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        if (expected instanceof Boolean) {
            boolean expectedBoolean = (Boolean) expected;
            return this.is(!expectedBoolean, failMessage);
        } else {
            return testSequence(
                    provider,
                    (actual) -> assertionImpl.notEquals(actual, expected),
                    (actual) -> assertionImpl.formatExpectNotEquals(null, expected, createFailMessage(failMessage))
            );
        }
    }
}
