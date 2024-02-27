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
 *
 * @author Mike Reiche
 */
public class DefaultQuantityAssertion<TYPE> extends DefaultObjectAssertion<TYPE> implements QuantityAssertion<TYPE> {

    public DefaultQuantityAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<TYPE> provider) {
        super(parentAssertion, provider);
    }

    private BigDecimal toBigDecimal(Object given) {
        if (given == null) {
            return null;
        } else {
            return new BigDecimal(given.toString());
        }
    }

    @Override
    public boolean isGreaterThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertionImpl.isGreaterThan(toBigDecimal(actual), expected),
                (actual) -> assertionImpl.formatExpectGreaterThan(toBigDecimal(actual), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertionImpl.isLowerThan(toBigDecimal(actual), expected),
                (actual) -> assertionImpl.formatExpectLowerThan(toBigDecimal(actual), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertionImpl.isGreaterEqualThan(toBigDecimal(actual), expected),
                (actual) -> assertionImpl.formatExpectGreaterEqualThan(toBigDecimal(actual), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertionImpl.isLowerEqualThan(toBigDecimal(actual), expected),
                (actual) -> assertionImpl.formatExpectLowerEqualThan(toBigDecimal(actual), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertionImpl.isBetween(toBigDecimal(actual), lower, higher),
                (actual) -> assertionImpl.formatExpectIsBetween(toBigDecimal(actual), lower, higher, createFailMessage(failMessage))
        );
    }

    @Override
    public <MAPPED_TYPE> QuantityAssertion<MAPPED_TYPE> map(Function<? super TYPE, MAPPED_TYPE> mapFunction) {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<MAPPED_TYPE>() {
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
                return "mapped to " + Format.shortString(getActual());
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
                    number = (BigDecimal) provider.getActual();
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
