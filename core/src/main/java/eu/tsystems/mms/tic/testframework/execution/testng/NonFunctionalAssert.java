/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.common.Testerra;

/**
 * Created with IntelliJ IDEA.
 * User: pele
 * Date: 23.07.13
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class NonFunctionalAssert extends AssertCollector {

    static {
        assertion = Testerra.ioc().getInstance(NonFunctionalAssertion.class);
    }

    /**
     * Protect constructor since it is a static only class
     */
    protected NonFunctionalAssert() {
        // hide constructor
    }
}
