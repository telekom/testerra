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
 */

package eu.tsystems.mms.tic.testframework.internal.asserts;

/**
 * Default implementation of {@link BinaryAssertion}
 * @author Mike Reiche
 */
public class DefaultBinaryAssertion<T> extends AbstractTestedPropertyAssertion<T> implements BinaryAssertion<T> {

    public DefaultBinaryAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(boolean expected, String failMessage) {
        if (expected) {
            return testSequence(
                    provider,
                    (actual) -> {
                        String actualString = actual.toString();
                        return (
                                actualString.equalsIgnoreCase("true")
                                || actualString.equalsIgnoreCase("on")
                                || actualString.equalsIgnoreCase("1")
                                || actualString.equalsIgnoreCase("yes")
                        );
                    },
                    (actual) -> assertion.format(actual, "is one of [true, 'on', '1', 'yes']", createFailMessage(failMessage))
            );
        } else {
            return testSequence(
                    provider,
                    (actual) -> {
                        String actualString = actual.toString();
                        return (
                                actualString.equalsIgnoreCase("false")
                                || actualString.equalsIgnoreCase("off")
                                || actualString.equalsIgnoreCase("0")
                                || actualString.equalsIgnoreCase("no")
                        );
                    },
                    (actual) -> assertion.format(actual, "is one of [false, 'off', '0', 'no']", createFailMessage(failMessage)));
        }
    }
}
