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
/*
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting.sub.sub;

import eu.tsystems.mms.tic.testframework.annotations.SupportMethod;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testhelper.AbstractReportingTest;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
//@Listeners(FennecListener.class)
public class ReportingDetailsTest extends AbstractReportingTest {

    /*
    AnnotationTransformerListener only works with global listener. So execute the AnnotationTransformerListener.xml from resources.
     */


    @Test
    public void testT01Passed() {
        for (int i = 0; i < 200; i++) {
            logger.info("Log a lot of things");
        }
    }

    @Test
    @SupportMethod
    public void testT02Passed_SetupMethod() {
    }

    @Test
    public void testSteps() throws Exception {
        TestStep.begin("1) open frontend");
        logger.info("opening frontend");

        TestStep.begin("2) do something");
        logger.info("use frontend");

        TestStep.end();
        TestStep.begin("3) do something else");
        logger.info("use frontend again");

        TestStep.begin("4) do nothing");
        TestStep.begin("5) do something");
        logger.info("use frontend");
    }

    @Test
    public void testFailingWithSteps() throws Exception {
        failingStep();
    }
}
