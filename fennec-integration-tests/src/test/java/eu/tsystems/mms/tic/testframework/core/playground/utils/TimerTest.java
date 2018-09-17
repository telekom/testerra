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
package eu.tsystems.mms.tic.testframework.core.playground.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.exceptions.SequenceTimeoutException;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by pele on 16.04.2015.
 */
public class TimerTest extends AbstractTest {

    @Test
    public void testTimerWithMonitoring() throws Exception {
        Timer timer = new Timer(500, 3000);

        System.out.println(new Date());
        try {
            timer.executeSequenceThread(new Timer.Sequence<Object>() {
                @Override
                public void run() {
                    TestUtils.sleep(10000);
                }
            });
        } catch (SequenceTimeoutException e) {
            System.out.println("Exception at " + new Date());
        }
        System.out.println("Something else1");
        System.out.println("Something else2");
        System.out.println("Something else3");
    }
}
