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
package eu.tsystems.mms.tic.testframework.core.playground.logging;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by pele on 14.04.2015.
 */
public class ReportLoggingTest extends AbstractTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testLoggingOfExceptions() throws Exception {
        logger.info("Non-Clickable message");
        logger.info("Clickable message", new RuntimeException("test exception"));

        logger.warn("Non-Clickable message");
        logger.warn("Clickable message", new RuntimeException("test exception"));

        logger.error("Non-Clickable message");
        logger.error("Clickable message", new RuntimeException("test exception"));
    }
}
