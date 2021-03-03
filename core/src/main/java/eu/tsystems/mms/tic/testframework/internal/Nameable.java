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
package eu.tsystems.mms.tic.testframework.internal;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This interface supports naming hierarchy
 * @param <SELF>
 */
public interface Nameable<SELF> {

    /**
     * Sets the name of this instance
     */
    SELF setName(String name);

    /**
     * Return the (detailed) name of this instance.
     * @return String Should never return NULL
     */
    String getName(boolean detailed);

    /**
     * Returns the parent instance of this. Can be NULL.
     */
    Nameable getParent();

    /**
     * Determines if the instance has name explicitly set by {@link #setName(String)}
     */
    boolean hasOwnName();

    default String getName() {
        return getName(false);
    }

    default String toString(boolean detailed) {
        StringBuilder sb = new StringBuilder();
        this.traceAncestors(parent -> {
            sb.append(parent.getName(detailed)).append(" -> ");
            return true;
        });
        sb.append(getName(detailed));
        return sb.toString();
    }

    /**
     * Traces the parents beginning by root and passes them to the predicate WITHOUT the calling element.
     * When the predicate returns FALSE, the tracing stops.
     */
    default void traceAncestors(Predicate<Nameable> consumer) {
        Nameable parent = getParent();
        if (parent!=null) {
            parent.traceAncestors(consumer);
            if (!consumer.test(parent)) {
                return;
            };
        }
    }
}
