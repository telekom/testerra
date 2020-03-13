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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action;

import java.lang.reflect.Field;

public class FieldWithActionConfig {

    public final Field field;
    public final boolean findNot;
    public final boolean fast;
    public final boolean forceStandardAssert;

    public FieldWithActionConfig(Field field, boolean findNot, boolean fast, boolean forceStandardAssert) {
        this.field = field;
        this.findNot = findNot;
        this.fast = fast;
        this.forceStandardAssert = forceStandardAssert;
    }
}
