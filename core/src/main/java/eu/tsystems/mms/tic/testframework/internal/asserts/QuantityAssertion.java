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
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface QuantityAssertion<TYPE> extends BinaryAssertion<TYPE> {
    default boolean is(Object expected) {
        return is(expected, null);
    }
    boolean is(Object expected, String subject);

    default boolean isNot(Object expected) {
        return isNot(expected, null);
    }
    boolean isNot(Object expected, String subject);

    default boolean isGreaterThan(long expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(double expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(long expected, String subject) {
        return isGreaterThan(new BigDecimal(expected), subject);
    }
    default boolean isGreaterThan(double expected, String subject) {
        return isGreaterThan(new BigDecimal(expected), subject);
    }
    default boolean isGreaterThan(BigDecimal expected) {
        return isGreaterThan(expected, null);
    }
    boolean isGreaterThan(BigDecimal expected, String subject);

    default boolean isLowerThan(long expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(double expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(long expected, String subject) {
        return isLowerThan(new BigDecimal(expected), subject);
    }
    default boolean isLowerThan(double expected, String subject) {
        return isLowerThan(new BigDecimal(expected), subject);
    }
    default boolean isLowerThan(BigDecimal expected) {
        return isLowerThan(expected, null);
    }
    boolean isLowerThan(BigDecimal expected, String subject);

    default boolean isGreaterEqualThan(long expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(double expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(long expected, String subject) {
        return isGreaterEqualThan(new BigDecimal(expected), subject);
    }
    default boolean isGreaterEqualThan(double expected, String subject) {
        return isGreaterEqualThan(new BigDecimal(expected), subject);
    }
    default boolean isGreaterEqualThan(BigDecimal expected) {
        return isGreaterEqualThan(expected, null);
    }
    boolean isGreaterEqualThan(BigDecimal expected, String subject);

    default boolean isLowerEqualThan(long expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(double expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(long expected, String subject) {
        return isLowerEqualThan(new BigDecimal(expected), subject);
    }
    default boolean isLowerEqualThan(double expected, String subject) {
        return isLowerEqualThan(new BigDecimal(expected), subject);
    }
    default boolean isLowerEqualThan(BigDecimal expected) {
        return isLowerEqualThan(expected, null);
    }
    boolean isLowerEqualThan(BigDecimal expected, String subject);

    default boolean isBetween(long lower, long higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(double lower, double higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(long lower, long higher, String subject) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), subject);
    }
    default boolean isBetween(double lower, double higher, String subject) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), subject);
    }
    default boolean isBetween(BigDecimal lower, BigDecimal higher) {
        return isBetween(lower, higher, null);
    }
    boolean isBetween(BigDecimal lower, BigDecimal higher, String subject);

    <MAPPED_TYPE> StringAssertion<MAPPED_TYPE> map(Function<? super TYPE, MAPPED_TYPE> mapFunction);

    QuantityAssertion<BigDecimal> absolute();
}
