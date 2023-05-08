/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Telekom MMS GmbH, Deutsche Telekom AG
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
package io.testerra.report.test.pretest_status.classContext.pack2;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.Test;

/**
 * Created on 2023-03-22
 *
 * @author mgn
 */
@TestClassContext(name = "ClassContextSameClassPack2")
public class GenerateClassContextSameNameTest extends AbstractTestSitesTest {

    @Test
    public void testTestMethodInSameClassesPack2() {
    }

}
