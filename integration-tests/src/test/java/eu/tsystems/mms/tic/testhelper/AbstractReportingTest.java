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
package eu.tsystems.mms.tic.testhelper;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Created by piet on 27.07.16.
 */
public abstract class AbstractReportingTest extends TesterraTest {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected enum How {
        HARD,
        SOFT,
        MINOR
    }

    protected void failingStep() {
        failingStep(How.HARD);
    }

    protected void failingStep(How how) {
        TestStep.begin("1) open frontend");
        logger.info("opening frontend");

        TestStep.begin("2) do something");
        logger.info("use frontend");

        TestStep.end();
        TestStep.begin("3) do something else");
        logger.info("use frontend again");

        switch (how) {
            case HARD:
                Assert.assertTrue(false, "der mond besteht aus Buttermilch 1");
                break;
            case SOFT:
                AssertCollector.assertTrue(false, "der mond besteht aus Buttermilch 1");
                break;
            case MINOR:
                NonFunctionalAssert.assertTrue(false, "der mond besteht aus Buttermilch 1");
                break;
        }

        TestStep.begin("4) do nothing");
        TestStep.begin("5) do something");
        logger.info("use frontend");

        TestStep.begin("6) do something else");
        logger.info("use frontend again");

        switch (how) {
            case HARD:
                Assert.assertTrue(false, "der mond besteht aus Buttermilch 2");
                break;
            case SOFT:
                AssertCollector.assertTrue(false, "der mond besteht aus Buttermilch 2");
                break;
            case MINOR:
                NonFunctionalAssert.assertTrue(false, "der mond besteht aus Buttermilch 2");
                break;
        }

    }

}
