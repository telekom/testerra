/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.report.hooks;

import eu.tsystems.mms.tic.testframework.annotations.DismissDryRun;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestNGMethod;
import org.testng.internal.ConstructorOrMethod;

public abstract class Hook {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hook.class);

    protected static boolean dryRun(ITestNGMethod testNGMethod) {
        final ConstructorOrMethod constructorOrMethod = testNGMethod.getConstructorOrMethod();
        final DismissDryRun dismissDryRun = constructorOrMethod.getMethod().getAnnotation(DismissDryRun.class);
        if (dismissDryRun == null) {
            LOGGER.info("Dry run: " + testNGMethod.getMethodName());
            TimerUtils.sleep(50);
            return true;
        }
        return false;
    }

}
