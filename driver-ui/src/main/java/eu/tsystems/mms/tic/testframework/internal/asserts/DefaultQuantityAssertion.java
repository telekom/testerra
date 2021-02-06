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

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Default implementation of {@link QuantityAssertion}
 * @author Mike Reiche
 */
public class DefaultQuantityAssertion<TYPE> extends DefaultBinaryAssertion<TYPE> implements QuantityAssertion<TYPE> {

    public DefaultQuantityAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<TYPE> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(Object expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.equals(actual, expected),
                (actual) -> testAssertion.formatExpectEquals(null, expected ,createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.notEquals(actual, expected),
                (actual) -> testAssertion.formatExpectNotEquals(null, expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.isGreaterThan(new BigDecimal(actual.toString()), expected),
                (actual) -> testAssertion.formatExpectGreaterThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.isLowerThan(new BigDecimal(actual.toString()), expected),
                (actual) -> testAssertion.formatExpectLowerThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.isGreaterEqualThan(new BigDecimal(actual.toString()), expected),
                (actual) -> testAssertion.formatExpectGreaterEqualThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.isLowerEqualThan(new BigDecimal(actual.toString()), expected),
                (actual) -> testAssertion.formatExpectLowerEqualThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage) {
        return testSequence(
                provider,
                (actual) -> testAssertion.isBetween(new BigDecimal(actual.toString()), lower, higher),
                (actual) -> testAssertion.formatExpectIsBetween(new BigDecimal(actual.toString()), lower, higher, createFailMessage(failMessage))
        );
    }

    @Override
    public <MAPPED_TYPE> StringAssertion<MAPPED_TYPE> map(Function<? super TYPE, MAPPED_TYPE> mapFunction) {
        return propertyAssertionFactory.createWithParent(DefaultStringAssertion.class, this, new AssertionProvider<MAPPED_TYPE>() {
            @Override
            public MAPPED_TYPE getActual() {
                TYPE actual = provider.getActual();
                if (actual == null) {
                    return null;
                } else {
                    return mapFunction.apply(provider.getActual());
                }
            }

            @Override
            public String createSubject() {
                return "map";
            }
        });
    }

    @Override
    public QuantityAssertion<BigDecimal> absolute() {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<BigDecimal>() {
            @Override
            public BigDecimal getActual() {
                BigDecimal number;
                if (!(provider.getActual() instanceof BigDecimal)) {
                    number = new BigDecimal(provider.getActual().toString());
                } else {
                    number = (BigDecimal)provider.getActual();
                }
                return number.abs();
            }

            @Override
            public String createSubject() {
                return "absolute";
            }
        });
    }
}
