/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.report.perf;

public enum MetricType {
    AVERAGE_RESPONSETIMES_PER_TESTSTEP,
    MAX_RESPONSETIME_PER_TESTSTEP,
    MIN_RESPONSETIME_PER_TESTSTEP,
    STACKED_RESPONSETIMES_PER_TESTSTEP,
    AVERAGE_RESPONSETIMES_PER_REPETITION,
    MAX_RESPONSETIME_PER_REPETITION,
    MIN_RESPONSETIME_PER_REPETITION,
    LAYERED_RESPONSETIMES_PER_REPETITION,
    TRANSACTION_STATUS,
    RESPONSETIMES_PER_TEST,
    RESPONSETIMES_PER_TESTSTEP,
    REQUESTS_PER_SECOND,
    REQUESTS_PER_MINUTE,
    REQUESTS_PER_HOUR,
    TRANSACTIONS_PER_SECOND ,
    TRANSACTIONS_PER_MINUTE,
    TRANSACTIONS_PER_HOUR,
}
