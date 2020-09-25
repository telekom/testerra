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

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.Conditions;
import org.testng.annotations.Test;

public class ConditionsTest extends TesterraTest implements Loggable {
    @Test
    public void test_Expression() {
        Conditions conditions = new Conditions("and", "or");
        Conditions.Chain condition = conditions
                .is(conditions.is("ene").and("mene").and("muh"))
                .and(conditions.is("raus!=langenicht").or("pos(du)==age(du)"));

        String finalExpression = "if " + condition + " then raus=du";
        Assert.assertEquals(finalExpression, "if ((ene and mene and muh) and (raus!=langenicht or pos(du)==age(du))) then raus=du");
    }
}
