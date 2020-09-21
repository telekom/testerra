/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.PageObject;
import java.util.function.Consumer;

/**
 * This interface supports naming hierarchy of {@link PageObject}
 * @param <SELF>
 */
public interface Nameable<SELF> {

    SELF setName(String name);
    String getName(boolean detailed);
    Nameable getParent();

    default String getName() {
        return getName(false);
    }
    default String toString(boolean detailed) {
        return getName(detailed);
    }

    /**
     * Traces the parents beginning by root and passes them to the consumer
     * @param consumer
     */
    default void traceParent(Consumer<Nameable> consumer) {
        Nameable parent = getParent();
        if (parent!=null) {
            parent.traceParent(consumer);
            consumer.accept(parent);
        }
    }
}
