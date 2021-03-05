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

package eu.tsystems.mms.tic.testframework.common;

/**
 * New Properties with overridable default value
 * @author Mike Reiche
 */
public interface IProperties {
    /**
     * Gets the default value
     */
    Object getDefault();
    /**
     * @return Value as double
     */
    default Double asDouble() {
        return PropertyManager.getPropertiesParser().getDoubleProperty(toString(), getDefault());
    }
    /**
     * @return Value as long
     */
    default Long asLong() { return PropertyManager.getPropertiesParser().getLongProperty(toString(), getDefault()); }
    /**
     * @return Value as boolean
     */
    default Boolean asBool() { return PropertyManager.getPropertiesParser().getBooleanProperty(toString(), getDefault()); }
    /**
     * @return Value as string
     */
    default String asString() { return PropertyManager.getPropertiesParser().getProperty(toString(), getDefault()); }
    /**
     * @return Property string
     */
    String toString();
}
