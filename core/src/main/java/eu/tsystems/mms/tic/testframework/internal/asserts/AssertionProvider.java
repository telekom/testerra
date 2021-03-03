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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides information and reacts to further assertions
 * @author Mike Reiche
 */
public abstract class AssertionProvider<T> implements ActualProperty<T> {

    public static class Format {
        private final static Formatter formatter = Testerra.getInjector().getInstance(Formatter.class);
        public static final String SEPARATOR=" ";
        public static String separate(Object...parts) {
            return Arrays.stream(parts).map(Object::toString).collect(Collectors.joining(SEPARATOR));
        }

        public static String cut(String subject) {
            return formatter.cutString(subject, 100);
        }

        public static String enclose(String method, Object...parameters) {
            if (parameters.length > 0) {
                method += "(" + Arrays.stream(parameters).map(Objects::toString).collect(Collectors.joining(", ")) + ")";
            }
            return method;
        }

        public static String label(String label, Object parameter) {
            return label+": " + param(parameter);
        }

        public static String shortString(Object param) {
            return param(cut(param.toString()));
        }

        public static String param(Object param) {
            if (param == null) {
                return "[null]";
            } else {
                return "["+param.toString()+"]";
            }
        }
    }

    @Override
    abstract public T getActual();

    /**
     * @return The subject ob the property (ea. "name", "url", ...)
     */
    abstract public String createSubject();

    /**
     * This method will be called recurisve from parent to descendants
     * if the assertion passed.
     * @param assertion
     */
    public void passed(AbstractPropertyAssertion<T> assertion) {
    }

    /**
     * This method will be called recursive from parent to descendants
     * if one of the assertions failed.
     * @param assertion The failed assertion
     */
    public void failed(AbstractPropertyAssertion<T> assertion) {
    }

    /**
     * This method will be called recursive from parent to descendants
     * if one of the assertions finally failed.
     * @param assertion The failed assertion
     */
    public void failedFinally(AbstractPropertyAssertion<T> assertion) {
    }

    /**
     * This method will be called recursive from parent to descendants
     * to wrap the given {@link AssertionError}.
     * @param assertionError The given assertion error
     */
    public AssertionError wrapAssertionError(AssertionError assertionError) {
        return assertionError;
    }
}
