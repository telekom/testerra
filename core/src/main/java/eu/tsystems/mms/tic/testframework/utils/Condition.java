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

package eu.tsystems.mms.tic.testframework.utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Fluent interface for AND/OR conditions
 * @author Mike Reiche
 */
public class Condition {
    public class Chain {
        private final ArrayList<Object> expressions = new ArrayList<>();
        private final Condition condition;

        Chain(Condition condition) {
            this.condition = condition;
        }
        Chain(Condition condition, Object expression) {
            this.condition = condition;
            this.expressions.add(expression);
        }

        public Chain and(Object expression) {
            if (this.expressions.size()>0) this.expressions.add(this.condition.andDelimiter);
            this.expressions.add(expression);
            return this;
        }

        public Chain or(Object expression) {
            if (this.expressions.size()>0) this.expressions.add(this.condition.orDelimiter);
            this.expressions.add(expression);
            return this;
        }

        public String toString() {
            return this.condition.groupStartDelimiter +
                    this.expressions.stream().map(Object::toString).collect(Collectors.joining(this.condition.expressionDelimiter)) +
                    this.condition.groupEndDelimiter;
        }
    }

    private final String andDelimiter;
    private final String orDelimiter;
    private final String groupStartDelimiter;
    private final String groupEndDelimiter;
    private final String expressionDelimiter;

    public Condition(String andDelimiter, String orDelimiter) {
        this(andDelimiter, orDelimiter, "(", ")", " ");
    }

    public Condition(
            String andDelimiter,
            String orDelimiter,
            String groupStartDelimiter,
            String conditionEndDelimiter,
            String expressionDelimiter
    ) {
        this.andDelimiter = andDelimiter;
        this.orDelimiter = orDelimiter;
        this.groupStartDelimiter = groupStartDelimiter;
        this.groupEndDelimiter = conditionEndDelimiter;
        this.expressionDelimiter = expressionDelimiter;
    }

    public Chain is() {
        return new Chain(this);
    }
    public Chain is(Object expression) {
        return new Chain(this, expression);
    }
}
